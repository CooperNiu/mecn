package com.mecn.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CentralityResult 单元测试
 */
class CentralityResultTest {

    private CentralityResult result;

    @BeforeEach
    void setUp() {
        result = new CentralityResult("GDP");
    }

    @Test
    void testDefaultConstructor() {
        CentralityResult r = new CentralityResult();
        assertNull(r.getNodeId());
        assertNotNull(r.getCustomMetrics());
    }

    @Test
    void testParameterizedConstructor() {
        assertEquals("GDP", result.getNodeId());
        assertNotNull(result.getCustomMetrics());
    }

    @Test
    void testSetNodeId() {
        result.setNodeId("CPI");
        assertEquals("CPI", result.getNodeId());
    }

    @Test
    void testSetDegreeCentrality() {
        result.setDegreeCentrality(0.75);
        assertEquals(0.75, result.getDegreeCentrality(), 0.001);
    }

    @Test
    void testSetInDegreeCentrality() {
        result.setInDegreeCentrality(0.6);
        assertEquals(0.6, result.getInDegreeCentrality(), 0.001);
    }

    @Test
    void testSetOutDegreeCentrality() {
        result.setOutDegreeCentrality(0.8);
        assertEquals(0.8, result.getOutDegreeCentrality(), 0.001);
    }

    @Test
    void testSetClosenessCentrality() {
        result.setClosenessCentrality(0.9);
        assertEquals(0.9, result.getClosenessCentrality(), 0.001);
    }

    @Test
    void testSetBetweennessCentrality() {
        result.setBetweennessCentrality(0.5);
        assertEquals(0.5, result.getBetweennessCentrality(), 0.001);
    }

    @Test
    void testSetPageRank() {
        result.setPageRank(0.85);
        assertEquals(0.85, result.getPageRank(), 0.001);
    }

    @Test
    void testSetEigenvectorCentrality() {
        result.setEigenvectorCentrality(0.7);
        assertEquals(0.7, result.getEigenvectorCentrality(), 0.001);
    }

    @Test
    void testCustomMetrics() {
        result.addCustomMetric("metric1", 1.0);
        assertEquals(1.0, result.getCustomMetrics().get("metric1"), 0.001);
    }

    @Test
    void testGetCustomMetricNotFound() {
        assertNull(result.getCustomMetrics().get("nonexistent"));
    }

    @Test
    void testSetCustomMetrics() {
        var metrics = new java.util.HashMap<String, Double>();
        metrics.put("m1", 1.0);
        result.setCustomMetrics(metrics);
        assertEquals(metrics, result.getCustomMetrics());
    }

    @Test
    void testGetCompositeScore() {
        result.setDegreeCentrality(0.8);
        result.setPageRank(0.7);
        
        double score = result.getCompositeScore();
        assertTrue(score > 0);
    }

    @Test
    void testToString() {
        String str = result.toString();
        assertNotNull(str);
        assertTrue(str.contains("GDP"));
    }
}
