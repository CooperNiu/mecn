package com.mecn.network;

import com.mecn.MECNTools;
import com.mecn.causal.CausalResult;
import com.mecn.data.generator.EnhancedDataGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 社区检测器测试
 */
public class CommunityDetectorTest {
    
    @Test
    public void testDetectCommunities() {
        // 准备测试数据
        EnhancedDataGenerator generator = new EnhancedDataGenerator(42);
        double[][] data = generator.generateDataForSampleSize(100);
        
        CausalResult causalResult = MECNTools.discoverCausalStructure(data, 0.05);
        var network = MECNTools.buildNetwork(causalResult, generateCodes(40), 0.08);
        
        // 创建社区检测器
        CommunityDetector detector = new CommunityDetector(network.getGraph());
        List<List<String>> communities = detector.detectCommunities();
        
        // 验证结果
        assertNotNull(communities);
        assertFalse(communities.isEmpty());
        assertTrue(communities.size() >= 1);
        
        // 验证所有节点都被分配到某个社区
        int totalNodes = 0;
        for (List<String> community : communities) {
            assertNotNull(community);
            assertFalse(community.isEmpty());
            totalNodes += community.size();
        }
        assertEquals(network.getNodes().size(), totalNodes);
    }
    
    @Test
    public void testGetNodeCommunity() {
        // 准备测试数据
        EnhancedDataGenerator generator = new EnhancedDataGenerator(42);
        double[][] data = generator.generateDataForSampleSize(100);
        
        CausalResult causalResult = MECNTools.discoverCausalStructure(data, 0.05);
        var network = MECNTools.buildNetwork(causalResult, generateCodes(40), 0.08);
        
        CommunityDetector detector = new CommunityDetector(network.getGraph());
        detector.detectCommunities();
        
        String nodeId = network.getNodes().iterator().next();
        int communityIndex = detector.getNodeCommunity(nodeId);
        
        assertTrue(communityIndex >= 0);
        
        // 验证节点确实在该社区中
        List<String> communityNodes = detector.getCommunityNodes(communityIndex);
        assertTrue(communityNodes.contains(nodeId));
    }
    
    @Test
    public void testGetNumCommunities() {
        // 准备测试数据
        EnhancedDataGenerator generator = new EnhancedDataGenerator(42);
        double[][] data = generator.generateDataForSampleSize(100);
        
        CausalResult causalResult = MECNTools.discoverCausalStructure(data, 0.05);
        var network = MECNTools.buildNetwork(causalResult, generateCodes(40), 0.08);
        
        CommunityDetector detector = new CommunityDetector(network.getGraph());
        detector.detectCommunities();
        
        int numCommunities = detector.getNumCommunities();
        assertTrue(numCommunities >= 1);
        assertTrue(numCommunities <= network.getNodes().size());
    }
    
    @Test
    public void testGetModularity() {
        // 准备测试数据
        EnhancedDataGenerator generator = new EnhancedDataGenerator(42);
        double[][] data = generator.generateDataForSampleSize(100);
        
        CausalResult causalResult = MECNTools.discoverCausalStructure(data, 0.05);
        var network = MECNTools.buildNetwork(causalResult, generateCodes(40), 0.08);
        
        CommunityDetector detector = new CommunityDetector(network.getGraph());
        detector.detectCommunities();
        
        double modularity = detector.getModularity();
        
        // 模块度应该在 [-1, 1] 范围内，通常为正数
        assertTrue(modularity >= -1.0 && modularity <= 1.0);
    }
    
    @Test
    public void testGetCommunityStatistics() {
        // 准备测试数据
        EnhancedDataGenerator generator = new EnhancedDataGenerator(42);
        double[][] data = generator.generateDataForSampleSize(100);
        
        CausalResult causalResult = MECNTools.discoverCausalStructure(data, 0.05);
        var network = MECNTools.buildNetwork(causalResult, generateCodes(40), 0.08);
        
        CommunityDetector detector = new CommunityDetector(network.getGraph());
        Map<String, Object> stats = detector.getCommunityStatistics();
        
        assertNotNull(stats);
        assertTrue(stats.containsKey("numCommunities"));
        assertTrue(stats.containsKey("modularity"));
        assertTrue(stats.containsKey("totalNodes"));
        assertTrue(stats.containsKey("avgCommunitySize"));
        
        Integer numCommunities = (Integer) stats.get("numCommunities");
        assertNotNull(numCommunities);
        assertTrue(numCommunities > 0);
    }
    
    private java.util.List<String> generateCodes(int n) {
        java.util.List<String> codes = new java.util.ArrayList<>();
        for (int i = 0; i < n; i++) {
            codes.add("ECO_" + i);
        }
        return codes;
    }
}
