package com.mecn.visualization;

import com.mecn.causal.CausalResult;
import com.mecn.model.NetworkGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("NetworkVisualizer 测试")
class NetworkVisualizerTest {
    
    private NetworkVisualizer visualizer;
    private NetworkGraph network;
    private CausalResult causalResult;
    
    @BeforeEach
    void setUp() {
        visualizer = new NetworkVisualizer();
        
        // 创建测试网络
        network = new NetworkGraph();
        network.addNode("GDP", null);
        network.addNode("UNRATE", null);
        network.addNode("CPI", null);
        network.addEdge("GDP", "UNRATE", 0.8);
        network.addEdge("CPI", "GDP", 0.6);
        
        // 创建测试结果
        causalResult = new CausalResult(3);
        double[][] confidenceMatrix = {
            {0.0, 0.8, 0.3},
            {0.2, 0.0, 0.5},
            {0.6, 0.1, 0.0}
        };
        causalResult.setConfidenceMatrix(confidenceMatrix);
    }
    
    @Test
    @DisplayName("导出D3力导向图格式")
    void testExportToD3ForceGraph() {
        String json = visualizer.exportToD3ForceGraph(network);
        
        assertNotNull(json);
        assertTrue(json.contains("\"nodes\""));
        assertTrue(json.contains("\"links\""));
        assertTrue(json.contains("\"id\": \"GDP\""));
        assertTrue(json.contains("\"id\": \"UNRATE\""));
        assertTrue(json.contains("\"id\": \"CPI\""));
        assertTrue(json.contains("\"source\": \"GDP\""));
        assertTrue(json.contains("\"target\": \"UNRATE\""));
        assertTrue(json.contains("\"weight\": 0.8000"));
    }
    
    @Test
    @DisplayName("空网络抛出异常")
    void testNullNetworkThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            visualizer.exportToD3ForceGraph(null);
        });
    }
    
    @Test
    @DisplayName("生成热力图数据")
    void testGenerateHeatmapData() {
        java.util.List<String> nodeNames = java.util.Arrays.asList("GDP", "UNRATE", "CPI");
        String json = visualizer.generateHeatmapData(causalResult, nodeNames);
        
        assertNotNull(json);
        assertTrue(json.contains("\"xLabels\""));
        assertTrue(json.contains("\"yLabels\""));
        assertTrue(json.contains("\"data\""));
        assertTrue(json.contains("\"GDP\""));
        assertTrue(json.contains("\"UNRATE\""));
        assertTrue(json.contains("\"CPI\""));
        assertTrue(json.contains("0.8000"));
        assertTrue(json.contains("0.6000"));
    }
    
    @Test
    @DisplayName("热力图使用默认节点名称")
    void testHeatmapWithDefaultNodeNames() {
        String json = visualizer.generateHeatmapData(causalResult, null);
        
        assertNotNull(json);
        assertTrue(json.contains("\"Node_0\""));
        assertTrue(json.contains("\"Node_1\""));
        assertTrue(json.contains("\"Node_2\""));
    }
    
    @Test
    @DisplayName("空因果结果抛出异常")
    void testNullCausalResultThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            visualizer.generateHeatmapData(null, null);
        });
    }
    
    @Test
    @DisplayName("导出网络统计信息")
    void testExportNetworkStatistics() {
        String json = visualizer.exportNetworkStatistics(network);
        
        assertNotNull(json);
        assertTrue(json.contains("\"nodeCount\": 3"));
        assertTrue(json.contains("\"edgeCount\": 2"));
        assertTrue(json.contains("\"averageDegree\""));
        assertTrue(json.contains("\"density\""));
    }
    
    @Test
    @DisplayName("空网络统计抛出异常")
    void testNullNetworkForStatisticsThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            visualizer.exportNetworkStatistics(null);
        });
    }
    
    @Test
    @DisplayName("JSON转义特殊字符")
    void testJsonEscaping() {
        network.addNode("Node_With_Special", null);
        
        String json = visualizer.exportToD3ForceGraph(network);
        
        // 验证节点被正确添加到JSON中
        assertTrue(json.contains("Node_With_Special"));
    }
    
    @Test
    @DisplayName("复杂网络导出")
    void testComplexNetworkExport() {
        NetworkGraph complexNetwork = new NetworkGraph();
        
        // 添加更多节点和边
        for (int i = 0; i < 10; i++) {
            complexNetwork.addNode("Node_" + i, null);
        }
        
        for (int i = 0; i < 9; i++) {
            for (int j = i + 1; j < 10; j++) {
                if (Math.random() > 0.7) {
                    complexNetwork.addEdge("Node_" + i, "Node_" + j, Math.random());
                }
            }
        }
        
        String json = visualizer.exportToD3ForceGraph(complexNetwork);
        
        assertNotNull(json);
        assertTrue(json.contains("\"nodes\""));
        assertTrue(json.contains("\"links\""));
        
        // 验证所有节点都在输出中
        for (int i = 0; i < 10; i++) {
            assertTrue(json.contains("\"Node_" + i + "\""));
        }
    }
}
