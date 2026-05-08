package com.mecn.network;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RippleResult 单元测试
 */
class RippleResultTest {

    private RippleResult result;

    @BeforeEach
    void setUp() {
        result = new RippleResult("GDP", 10.0, 5);
    }

    @Test
    void testInitialization() {
        assertEquals("GDP", result.getShockNode());
        assertEquals(10.0, result.getShockMagnitude(), 0.001);
        assertEquals(5, result.getTimeSteps());
        assertNotNull(result.getNodeResponses());
        assertNotNull(result.getTotalImpactPerStep());
        assertNotNull(result.getMetadata());
    }

    @Test
    void testAddNodeResponse() {
        double[] response = {0.0, 0.5, 0.8, 0.6, 0.3};
        result.addNodeResponse("CPI", response);
        
        assertEquals(1, result.getNodeResponses().size());
        assertArrayEquals(response, result.getNodeResponse("CPI"));
    }

    @Test
    void testGetNodeResponseNotFound() {
        assertNull(result.getNodeResponse("NONEXISTENT"));
    }

    @Test
    void testGetResponseAtTime() {
        double[] response = {0.0, 0.5, 0.8, 0.6, 0.3};
        result.addNodeResponse("CPI", response);
        
        assertEquals(0.0, result.getResponseAtTime("CPI", 0), 0.001);
        assertEquals(0.5, result.getResponseAtTime("CPI", 1), 0.001);
        assertEquals(0.8, result.getResponseAtTime("CPI", 2), 0.001);
    }

    @Test
    void testGetResponseAtTimeInvalid() {
        double[] response = {0.0, 0.5, 0.8};
        result.addNodeResponse("CPI", response);
        
        assertEquals(0.0, result.getResponseAtTime("CPI", -1), 0.001);
        assertEquals(0.0, result.getResponseAtTime("CPI", 10), 0.001);
        assertEquals(0.0, result.getResponseAtTime("NONEXISTENT", 0), 0.001);
    }

    @Test
    void testGetMostAffectedNodes() {
        result.addNodeResponse("CPI", new double[]{0.0, 0.5, 0.8});
        result.addNodeResponse("PMI", new double[]{0.0, 0.3, 0.6});
        
        List<Map.Entry<String, Double>> affected = result.getMostAffectedNodes(2);
        
        assertEquals(2, affected.size());
        assertEquals("CPI", affected.get(0).getKey());
        assertEquals("PMI", affected.get(1).getKey());
    }

    @Test
    void testGetMostAffectedNodesExcludesShockNode() {
        result.addNodeResponse("GDP", new double[]{0.0, 1.0, 2.0});
        result.addNodeResponse("CPI", new double[]{0.0, 0.5, 0.8});
        
        List<Map.Entry<String, Double>> affected = result.getMostAffectedNodes(10);
        
        assertFalse(affected.stream().anyMatch(e -> e.getKey().equals("GDP")));
    }

    @Test
    void testGetPeakImpactTime() {
        double[] response = {0.0, 0.5, 0.8, 0.6, 0.3};
        result.addNodeResponse("CPI", response);
        
        assertEquals(2, result.getPeakImpactTime("CPI"));
    }

    @Test
    void testGetPeakImpactTimeNotFound() {
        assertEquals(-1, result.getPeakImpactTime("NONEXISTENT"));
    }

    @Test
    void testAddMetadata() {
        result.addMetadata("method", "RIPPLE");
        assertEquals("RIPPLE", result.getMetadata().get("method"));
    }

    @Test
    void testGetAllNodes() {
        result.addNodeResponse("CPI", new double[]{0.0, 0.5});
        result.addNodeResponse("PMI", new double[]{0.0, 0.3});
        
        assertEquals(2, result.getAllNodes().size());
    }
}
