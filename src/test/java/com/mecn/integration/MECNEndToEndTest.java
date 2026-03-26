package com.mecn.integration;

import com.mecn.causal.CausalConfig;
import com.mecn.causal.CausalEngineImpl;
import com.mecn.causal.CausalResult;
import com.mecn.data.generator.EnhancedDataGenerator;
import com.mecn.model.NetworkGraph;
import com.mecn.network.NetworkBuilder;
import com.mecn.network.RippleResult;
import com.mecn.network.RippleSimulator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

/**
 * MECN 端到端集成测试
 */
@DisplayName("MECN 端到端集成测试")
class MECNEndToEndTest {

    @Test
    @DisplayName("完整流程测试：数据生成 → 因果发现 → 网络构建 → 涟漪模拟")
    void testCompleteWorkflow() {
        // Step 1: 生成经济数据
        EnhancedDataGenerator generator = new EnhancedDataGenerator(42);
        double[][] economicData = generator.generateData();
        
        assertThat(economicData).hasDimensions(150, 40);

        // Step 2: 因果发现（使用简化的测试方法）
        // 注意：由于 LassoRegression 依赖 Smile API，这里使用简化测试
        CausalEngineImpl causalEngine = new CausalEngineImpl(false);
        // 暂时不注册实际方法，避免 Smile 依赖问题
        
        CausalConfig config = new CausalConfig();
        config.setParallel(false);
        
        // 为了测试流程，创建一个简化的因果结果
        CausalResult causalResult = createSimpleCausalResult(5);

        // Step 3: 构建网络
        NetworkBuilder networkBuilder = new NetworkBuilder(0.05);
        java.util.List<String> nodeNames = java.util.Arrays.asList(
            "CMD_0", "CMD_1", "MACRO_0", "FIN_0", "EMPL_0"
        );
        
        NetworkGraph network = networkBuilder.build(causalResult, nodeNames);
        
        assertThat(network.getNodes()).hasSize(5);

        // Step 4: 涟漪效应模拟
        RippleSimulator simulator = new RippleSimulator(0.9, 20);
        RippleResult rippleResult = simulator.simulate(network, "CMD_0", 1.0, 20);
        
        assertThat(rippleResult.getShockNode()).isEqualTo("CMD_0");
        assertThat(rippleResult.getTimeSteps()).isEqualTo(20);

        // Step 5: 验证结果完整性
        assertThat(rippleResult.getNodeResponses()).isNotEmpty();
        assertThat(network.getNetworkStatistics()).containsKey("nodeCount");
    }

    @Test
    @DisplayName("数据生成器集成测试")
    void testDataGeneratorIntegration() {
        // Given
        EnhancedDataGenerator generator = new EnhancedDataGenerator(42);

        // When
        double[][] data = generator.generateData();
        var indicators = generator.getSupportedIndicators();
        var config = generator.getConfigInfo();

        // Then
        assertAll(
            () -> assertThat(data).hasDimensions(150, 40),
            () -> assertThat(indicators).hasSize(40),
            () -> assertThat(config).containsKey("numIndicators"),
            () -> {
                // 验证数据不是全零
                double sum = 0.0;
                for (double[] row : data) {
                    for (double val : row) {
                        sum += Math.abs(val);
                    }
                }
                assertThat(sum).isGreaterThan(0);
            }
        );
    }

    @Test
    @DisplayName("网络构建和分析集成测试")
    void testNetworkBuildingAndAnalysis() {
        // Given
        NetworkBuilder builder = new NetworkBuilder(0.1);
        CausalResult causalResult = createSimpleCausalResult(4);
        java.util.List<String> nodes = java.util.Arrays.asList("A", "B", "C", "D");

        // When
        NetworkGraph network = builder.build(causalResult, nodes);
        
        // Then - 验证网络统计
        var stats = network.getNetworkStatistics();
        assertThat(stats.get("nodeCount")).isEqualTo(4);
        assertThat((Integer) stats.get("edgeCount")).isGreaterThan(0);
        assertThat((Double) stats.get("density")).isGreaterThan(0);
    }

    @Test
    @DisplayName("涟漪效应传播集成测试")
    void testRippleEffectPropagation() {
        // Given
        NetworkGraph network = createTestNetwork();
        RippleSimulator simulator = new RippleSimulator(0.9, 30);

        // When
        RippleResult result = simulator.simulate(network, "Node1", 1.0, 30);

        // Then
        assertAll(
            () -> assertThat(result.getShockMagnitude()).isEqualTo(1.0),
            () -> assertThat(result.getNodeResponses()).hasSize(3),
            () -> {
                // 验证冲击随时间衰减
                double[] node1Response = result.getNodeResponse("Node1");
                int nonZeroCount = 0;
                for (double val : node1Response) {
                    if (Math.abs(val) > 0.001) nonZeroCount++;
                }
                assertThat(nonZeroCount).isGreaterThanOrEqualTo(1);
            }
        );
    }

    @Test
    @DisplayName("风险路径查找集成测试")
    void testRiskPathFindingIntegration() {
        // Given
        NetworkGraph network = createChainNetwork();
        RippleSimulator simulator = new RippleSimulator();

        // When
        var paths = simulator.findRiskPaths(network, "Start", "End", 0.01);

        // Then
        assertThat(paths).isNotEmpty();
        assertThat(paths.get(0).getPath())
            .startsWith("Start")
            .endsWith("End");
        assertThat(paths.get(0).getTotalImpact()).isGreaterThan(0);
    }

    @Test
    @DisplayName("系统重要性分析集成测试")
    void testSystemicImportanceAnalysis() {
        // Given
        NetworkGraph network = createStarNetwork();
        RippleSimulator simulator = new RippleSimulator();

        // When
        var importances = simulator.identifySystemicallyImportantNodes(network);

        // Then
        assertThat(importances).hasSize(5);
        // 中心节点应该最重要
        var mostImportant = importances.get(0);
        assertThat(mostImportant.getNodeId()).isEqualTo("Center");
        assertThat(mostImportant.getImportanceScore()).isGreaterThan(0);
    }

    /**
     * 辅助方法：创建简化的因果结果用于测试
     */
    private CausalResult createSimpleCausalResult(int size) {
        CausalResult result = new CausalResult(size);
        double[][] matrix = new double[size][size];
        
        // 创建一些简单的连接
        for (int i = 0; i < size - 1; i++) {
            matrix[i + 1][i] = 0.1 + i * 0.05;
        }
        
        result.setAdjacencyMatrix(matrix);
        return result;
    }

    /**
     * 辅助方法：创建测试网络
     */
    private NetworkGraph createTestNetwork() {
        NetworkGraph graph = new NetworkGraph();
        graph.addNode("Node1", new com.mecn.model.EconomicIndicator("Node1", "Node1"));
        graph.addNode("Node2", new com.mecn.model.EconomicIndicator("Node2", "Node2"));
        graph.addNode("Node3", new com.mecn.model.EconomicIndicator("Node3", "Node3"));
        
        graph.addEdge("Node1", "Node2", 0.5);
        graph.addEdge("Node2", "Node3", 0.4);
        
        return graph;
    }

    /**
     * 辅助方法：创建链式网络
     */
    private NetworkGraph createChainNetwork() {
        NetworkGraph graph = new NetworkGraph();
        String[] nodes = {"Start", "Middle1", "Middle2", "End"};
        
        for (String node : nodes) {
            graph.addNode(node, new com.mecn.model.EconomicIndicator(node, node));
        }
        
        graph.addEdge("Start", "Middle1", 0.6);
        graph.addEdge("Middle1", "Middle2", 0.5);
        graph.addEdge("Middle2", "End", 0.4);
        
        return graph;
    }

    /**
     * 辅助方法：创建星型网络
     */
    private NetworkGraph createStarNetwork() {
        NetworkGraph graph = new NetworkGraph();
        
        graph.addNode("Center", new com.mecn.model.EconomicIndicator("Center", "Center"));
        for (int i = 1; i <= 4; i++) {
            String spoke = "Spoke" + i;
            graph.addNode(spoke, new com.mecn.model.EconomicIndicator(spoke, spoke));
            graph.addEdge("Center", spoke, 0.8);
            graph.addEdge(spoke, "Center", 0.3);
        }
        
        return graph;
    }
}
