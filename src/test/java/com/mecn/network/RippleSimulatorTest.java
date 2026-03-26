package com.mecn.network;

import com.mecn.model.EconomicIndicator;
import com.mecn.model.NetworkGraph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

/**
 * RippleSimulator 单元测试
 */
@DisplayName("RippleSimulator 测试")
class RippleSimulatorTest {

    @Test
    @DisplayName("默认构造器测试")
    void testDefaultConstructor() {
        // When
        RippleSimulator simulator = new RippleSimulator();

        // Then
        assertThat(simulator.getDecayFactor()).isEqualTo(0.9);
        assertThat(simulator.getDefaultTimeSteps()).isEqualTo(20);
    }

    @Test
    @DisplayName("指定参数构造器测试")
    void testConstructorWithParameters() {
        // When
        RippleSimulator simulator = new RippleSimulator(0.85, 30);

        // Then
        assertAll(
            () -> assertThat(simulator.getDecayFactor()).isEqualTo(0.85),
            () -> assertThat(simulator.getDefaultTimeSteps()).isEqualTo(30)
        );
    }

    @Test
    @DisplayName("涟漪模拟基础测试")
    void testBasicRippleSimulation() {
        // Given
        RippleSimulator simulator = new RippleSimulator(0.9, 10);
        NetworkGraph network = createSimpleNetwork();

        // When
        RippleResult result = simulator.simulate(network, "A", 1.0, 10);

        // Then
        assertAll(
            () -> assertThat(result.getShockNode()).isEqualTo("A"),
            () -> assertThat(result.getShockMagnitude()).isEqualTo(1.0),
            () -> assertThat(result.getTimeSteps()).isEqualTo(10),
            () -> assertThat(result.getNodeResponses()).containsKey("A"),
            () -> assertThat(result.getNodeResponses()).containsKey("B")
        );
    }

    @Test
    @DisplayName("初始冲击验证测试")
    void testInitialShockValidation() {
        // Given
        RippleSimulator simulator = new RippleSimulator(0.9, 5);
        NetworkGraph network = createSimpleNetwork();

        // When
        RippleResult result = simulator.simulate(network, "A", 2.0, 5);

        // Then - t=0 时，只有节点 A 有冲击
        double[] responseA = result.getNodeResponse("A");
        double[] responseB = result.getNodeResponse("B");
        
        assertThat(responseA[0]).isEqualTo(2.0);
        assertThat(responseB[0]).isEqualTo(0.0);
    }

    @Test
    @DisplayName("冲击传播验证测试")
    void testShockPropagation() {
        // Given
        RippleSimulator simulator = new RippleSimulator(1.0, 5); // 无衰减
        NetworkGraph network = createSimpleNetwork(); // A -> B (weight=0.5)

        // When
        RippleResult result = simulator.simulate(network, "A", 1.0, 5);

        // Then - B 在 t=1 时应该受到 A 的影响
        double[] responseB = result.getNodeResponse("B");
        assertThat(responseB[1]).isCloseTo(0.5, within(0.001)); // A->B 权重 0.5
    }

    @Test
    @DisplayName("衰减因子效果测试")
    void testDecayFactorEffect() {
        // Given
        RippleSimulator simulator1 = new RippleSimulator(1.0, 5); // 无衰减
        RippleSimulator simulator2 = new RippleSimulator(0.5, 5); // 强衰减
        NetworkGraph network = createSimpleNetwork();

        // When
        RippleResult result1 = simulator1.simulate(network, "A", 1.0, 5);
        RippleResult result2 = simulator2.simulate(network, "A", 1.0, 5);

        // Then - 有衰减的总影响应该更小
        double totalImpact1 = result1.getTotalImpactPerStep().stream()
            .mapToDouble(Double::doubleValue).sum();
        double totalImpact2 = result2.getTotalImpactPerStep().stream()
            .mapToDouble(Double::doubleValue).sum();
        
        assertThat(totalImpact2).isLessThan(totalImpact1);
    }

    @Test
    @DisplayName("风险传导路径查找测试")
    void testRiskPathFinding() {
        // Given
        RippleSimulator simulator = new RippleSimulator();
        NetworkGraph network = createChainNetwork(); // A -> B -> C

        // When
        var paths = simulator.findRiskPaths(network, "A", "C", 0.01);

        // Then
        assertThat(paths).isNotEmpty();
        assertThat(paths.get(0).getPath()).containsExactly("A", "B", "C");
    }

    @Test
    @DisplayName("系统重要性节点识别测试")
    void testSystemicImportanceIdentification() {
        // Given
        RippleSimulator simulator = new RippleSimulator();
        NetworkGraph network = createHubNetwork(); // HUB 连接到所有节点

        // When
        var importances = simulator.identifySystemicallyImportantNodes(network);

        // Then
        assertThat(importances).isNotEmpty();
        // HUB 节点应该最重要
        assertThat(importances.get(0).getNodeId()).isEqualTo("HUB");
    }

    @Test
    @DisplayName("结果元数据验证测试")
    void testResultMetadataValidation() {
        // Given
        RippleSimulator simulator = new RippleSimulator(0.85, 15);
        NetworkGraph network = createSimpleNetwork();

        // When
        RippleResult result = simulator.simulate(network, "A", 1.0, 15);

        // Then
        assertThat(result.getMetadata())
            .containsEntry("decayFactor", 0.85)
            .containsKey("algorithm");
    }

    @Test
    @DisplayName("最受影响节点排序测试")
    void testMostAffectedNodesRanking() {
        // Given
        RippleSimulator simulator = new RippleSimulator(0.9, 20);
        NetworkGraph network = createSimpleNetwork();

        // When
        RippleResult result = simulator.simulate(network, "A", 1.0, 20);
        var mostAffected = result.getMostAffectedNodes(2);

        // Then
        assertThat(mostAffected).hasSize(1); // 只有 B 受影响
    }

    /**
     * 辅助方法：创建简单网络 A -> B
     */
    private NetworkGraph createSimpleNetwork() {
        NetworkGraph graph = new NetworkGraph();
        graph.addNode("A", new EconomicIndicator("A", "A"));
        graph.addNode("B", new EconomicIndicator("B", "B"));
        graph.addEdge("A", "B", 0.5);
        return graph;
    }

    /**
     * 辅助方法：创建链式网络 A -> B -> C
     */
    private NetworkGraph createChainNetwork() {
        NetworkGraph graph = new NetworkGraph();
        graph.addNode("A", new EconomicIndicator("A", "A"));
        graph.addNode("B", new EconomicIndicator("B", "B"));
        graph.addNode("C", new EconomicIndicator("C", "C"));
        graph.addEdge("A", "B", 0.5);
        graph.addEdge("B", "C", 0.4);
        return graph;
    }

    /**
     * 辅助方法：创建中心辐射网络
     */
    private NetworkGraph createHubNetwork() {
        NetworkGraph graph = new NetworkGraph();
        graph.addNode("HUB", new EconomicIndicator("HUB", "HUB"));
        graph.addNode("SPOKE1", new EconomicIndicator("SPOKE1", "SPOKE1"));
        graph.addNode("SPOKE2", new EconomicIndicator("SPOKE2", "SPOKE2"));
        
        graph.addEdge("HUB", "SPOKE1", 0.8);
        graph.addEdge("HUB", "SPOKE2", 0.8);
        graph.addEdge("SPOKE1", "HUB", 0.3);
        graph.addEdge("SPOKE2", "HUB", 0.3);
        
        return graph;
    }
}
