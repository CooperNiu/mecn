package com.mecn.network;

import com.mecn.MECNTools;
import com.mecn.causal.CausalResult;
import com.mecn.data.generator.EnhancedDataGenerator;
import com.mecn.model.CentralityResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 中心性分析器测试
 */
public class CentralityAnalyzerTest {
    
    @Test
    public void testAnalyze() {
        // 准备测试数据
        EnhancedDataGenerator generator = new EnhancedDataGenerator(42);
        double[][] data = generator.generateDataForSampleSize(100);
        
        CausalResult causalResult = MECNTools.discoverCausalStructure(data, 0.05);
        var network = MECNTools.buildNetwork(causalResult, generateCodes(40), 0.08);
        
        // 创建分析器并执行分析
        CentralityAnalyzer analyzer = new CentralityAnalyzer(network.getGraph());
        List<CentralityResult> results = analyzer.analyze();
        
        // 验证结果
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(network.getNodes().size(), results.size());
        
        // 验证每个节点都有中心性值
        for (CentralityResult result : results) {
            assertNotNull(result.getNodeId());
            assertNotNull(result.getDegreeCentrality());
            assertNotNull(result.getBetweennessCentrality());
            assertNotNull(result.getClosenessCentrality());
            assertNotNull(result.getPageRank());
            assertNotNull(result.getEigenvectorCentrality());
        }
    }
    
    @Test
    public void testGetTopKNodes() {
        // 准备测试数据
        EnhancedDataGenerator generator = new EnhancedDataGenerator(42);
        double[][] data = generator.generateDataForSampleSize(100);
        
        CausalResult causalResult = MECNTools.discoverCausalStructure(data, 0.05);
        var network = MECNTools.buildNetwork(causalResult, generateCodes(40), 0.08);
        
        CentralityAnalyzer analyzer = new CentralityAnalyzer(network.getGraph());
        
        // 测试不同的排序方式
        List<CentralityResult> topByDegree = analyzer.getTopKNodes(5, "degree");
        List<CentralityResult> topByBetweenness = analyzer.getTopKNodes(5, "betweenness");
        List<CentralityResult> topByComposite = analyzer.getTopKNodes(5, "composite");
        
        assertNotNull(topByDegree);
        assertEquals(5, topByDegree.size());
        
        assertNotNull(topByBetweenness);
        assertEquals(5, topByBetweenness.size());
        
        assertNotNull(topByComposite);
        assertEquals(5, topByComposite.size());
        
        // 验证排序正确性（降序）
        for (int i = 0; i < topByComposite.size() - 1; i++) {
            assertTrue(topByComposite.get(i).getCompositeScore() >= 
                      topByComposite.get(i + 1).getCompositeScore());
        }
    }
    
    @Test
    public void testGetNodeResult() {
        // 准备测试数据
        EnhancedDataGenerator generator = new EnhancedDataGenerator(42);
        double[][] data = generator.generateDataForSampleSize(100);
        
        CausalResult causalResult = MECNTools.discoverCausalStructure(data, 0.05);
        var network = MECNTools.buildNetwork(causalResult, generateCodes(40), 0.08);
        
        CentralityAnalyzer analyzer = new CentralityAnalyzer(network.getGraph());
        analyzer.analyze();
        
        String nodeId = network.getNodes().iterator().next();
        CentralityResult result = analyzer.getNodeResult(nodeId);
        
        assertNotNull(result);
        assertEquals(nodeId, result.getNodeId());
    }
    
    private java.util.List<String> generateCodes(int n) {
        java.util.List<String> codes = new java.util.ArrayList<>();
        for (int i = 0; i < n; i++) {
            codes.add("ECO_" + i);
        }
        return codes;
    }
}
