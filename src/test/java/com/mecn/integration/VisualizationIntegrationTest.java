package com.mecn.integration;

import com.mecn.causal.CausalResult;
import com.mecn.model.NetworkGraph;
import com.mecn.network.NetworkBuilder;
import com.mecn.visualization.NetworkVisualizer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * 可视化导出集成测试
 */
@DisplayName("可视化导出集成测试")
class VisualizationIntegrationTest {
    
    @Test
    @DisplayName("完整分析流程到D3.js导出")
    void testCompleteAnalysisToD3Export() {
        // Given: 构建网络
        NetworkGraph network = buildTestNetwork();
        
        // When: 导出为D3.js格式
        NetworkVisualizer visualizer = new NetworkVisualizer();
        String d3Json = visualizer.exportToD3ForceGraph(network);
        
        // Then: 验证JSON格式正确
        assertThat(d3Json).isNotNull();
        assertThat(d3Json).contains("\"nodes\"");
        assertThat(d3Json).contains("\"links\"");
        assertThat(d3Json).contains("\"GDP\"");
        assertThat(d3Json).contains("\"UNRATE\"");
        assertThat(d3Json).contains("\"CPI\"");
    }
    
    @Test
    @DisplayName("因果结果到热力图数据转换")
    void testCausalResultToHeatmapConversion() {
        // Given: 创建因果结果
        CausalResult causalResult = createCausalResultWithConfidence(4);
        List<String> nodeNames = Arrays.asList("GDP", "UNRATE", "CPI", "PCE");
        
        // When: 生成热力图数据
        NetworkVisualizer visualizer = new NetworkVisualizer();
        String heatmapJson = visualizer.generateHeatmapData(causalResult, nodeNames);
        
        // Then: 验证热力图数据
        assertThat(heatmapJson).isNotNull();
        assertThat(heatmapJson).contains("\"xLabels\"");
        assertThat(heatmapJson).contains("\"yLabels\"");
        assertThat(heatmapJson).contains("\"data\"");
        assertThat(heatmapJson).contains("\"GDP\"");
        assertThat(heatmapJson).contains("\"UNRATE\"");
        assertThat(heatmapJson).contains("\"CPI\"");
        assertThat(heatmapJson).contains("\"PCE\"");
    }
    
    @Test
    @DisplayName("网络统计信息导出完整性")
    void testNetworkStatisticsExportCompleteness() {
        // Given
        NetworkGraph network = buildComplexNetwork();
        
        // When
        NetworkVisualizer visualizer = new NetworkVisualizer();
        String statsJson = visualizer.exportNetworkStatistics(network);
        
        // Then
        assertThat(statsJson).contains("\"nodeCount\": 5");
        assertThat(statsJson).contains("\"edgeCount\"");
        assertThat(statsJson).contains("\"averageDegree\"");
        assertThat(statsJson).contains("\"density\"");
    }
    
    @Test
    @DisplayName("多步骤分析流程集成")
    void testMultiStepAnalysisPipeline() {
        // Step 1: 构建网络
        NetworkGraph network = buildTestNetwork();
        assertThat(network.getNodes()).hasSize(3);
        
        // Step 2: 导出D3.js格式
        NetworkVisualizer visualizer = new NetworkVisualizer();
        String d3Json = visualizer.exportToD3ForceGraph(network);
        assertThat(d3Json).isNotEmpty();
        
        // Step 3: 导出统计信息
        String statsJson = visualizer.exportNetworkStatistics(network);
        assertThat(statsJson).contains("\"nodeCount\": 3");
        
        // Step 4: 验证两种输出的一致性
        assertThat(d3Json).contains("\"id\": \"GDP\"");
        assertThat(statsJson).contains("\"nodeCount\": 3");
    }
    
    @Test
    @DisplayName("大规模网络可视化导出性能")
    void testLargeScaleNetworkExportPerformance() {
        // Given: 创建大型网络
        NetworkGraph largeNetwork = buildLargeNetwork(50);
        
        // When
        long startTime = System.currentTimeMillis();
        NetworkVisualizer visualizer = new NetworkVisualizer();
        String d3Json = visualizer.exportToD3ForceGraph(largeNetwork);
        long endTime = System.currentTimeMillis();
        
        // Then
        assertThat(d3Json).isNotEmpty();
        assertThat(d3Json).contains("\"nodes\"");
        // 应该在合理时间内完成（< 2秒）
        assertThat(endTime - startTime).isLessThan(2000);
    }
    
    @Test
    @DisplayName("节点属性嵌入到D3.js输出")
    void testNodeAttributesEmbedding() {
        // Given: 创建带属性的网络
        NetworkGraph network = new NetworkGraph();
        com.mecn.model.EconomicIndicator indicator = 
            new com.mecn.model.EconomicIndicator("GDP", "Gross Domestic Product");
        network.addNode("GDP", indicator);
        network.addNode("UNRATE", null);
        
        // When
        NetworkVisualizer visualizer = new NetworkVisualizer();
        String d3Json = visualizer.exportToD3ForceGraph(network);
        
        // Then
        assertThat(d3Json).contains("\"id\": \"GDP\"");
        assertThat(d3Json).contains("\"id\": \"UNRATE\"");
    }
    
    /**
     * 辅助方法：构建测试网络
     */
    private NetworkGraph buildTestNetwork() {
        NetworkGraph network = new NetworkGraph();
        network.addNode("GDP", new com.mecn.model.EconomicIndicator("GDP", "GDP"));
        network.addNode("UNRATE", new com.mecn.model.EconomicIndicator("UNRATE", "Unemployment Rate"));
        network.addNode("CPI", new com.mecn.model.EconomicIndicator("CPI", "Consumer Price Index"));
        
        network.addEdge("GDP", "UNRATE", 0.8);
        network.addEdge("CPI", "GDP", 0.6);
        network.addEdge("UNRATE", "CPI", 0.4);
        
        return network;
    }
    
    /**
     * 辅助方法：构建复杂网络
     */
    private NetworkGraph buildComplexNetwork() {
        NetworkGraph network = new NetworkGraph();
        String[] nodes = {"A", "B", "C", "D", "E"};
        
        for (String node : nodes) {
            network.addNode(node, new com.mecn.model.EconomicIndicator(node, node));
        }
        
        network.addEdge("A", "B", 0.9);
        network.addEdge("A", "C", 0.7);
        network.addEdge("B", "D", 0.6);
        network.addEdge("C", "D", 0.5);
        network.addEdge("D", "E", 0.8);
        
        return network;
    }
    
    /**
     * 辅助方法：构建大型网络
     */
    private NetworkGraph buildLargeNetwork(int size) {
        NetworkGraph network = new NetworkGraph();
        
        for (int i = 0; i < size; i++) {
            String nodeId = "Node_" + i;
            network.addNode(nodeId, new com.mecn.model.EconomicIndicator(nodeId, nodeId));
        }
        
        // 添加随机边
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                if (Math.random() > 0.8) {
                    network.addEdge("Node_" + i, "Node_" + j, Math.random());
                }
            }
        }
        
        return network;
    }
    
    /**
     * 辅助方法：创建带置信度的因果结果
     */
    private CausalResult createCausalResultWithConfidence(int size) {
        CausalResult result = new CausalResult(size);
        double[][] confidenceMatrix = new double[size][size];
        
        // 填充置信度矩阵
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i != j) {
                    confidenceMatrix[i][j] = Math.random();
                }
            }
        }
        
        result.setConfidenceMatrix(confidenceMatrix);
        return result;
    }
}
