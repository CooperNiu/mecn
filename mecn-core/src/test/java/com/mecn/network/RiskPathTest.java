package com.mecn.network;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RiskPath 单元测试
 */
class RiskPathTest {

    private RiskPath path;

    @BeforeEach
    void setUp() {
        path = new RiskPath("GDP", "CPI");
    }

    @Test
    void testInitialization() {
        assertEquals("GDP", path.getSource());
        assertEquals("CPI", path.getTarget());
        assertNotNull(path.getPath());
        assertNotNull(path.getEdgeWeights());
        assertEquals(0.0, path.getTotalImpact(), 0.001);
    }

    @Test
    void testAddNode() {
        path.addNode("GDP");
        path.addNode("PMI");
        path.addNode("CPI");
        
        assertEquals(3, path.getPath().size());
        assertEquals("GDP", path.getPath().get(0));
        assertEquals("PMI", path.getPath().get(1));
        assertEquals("CPI", path.getPath().get(2));
    }

    @Test
    void testAddEdgeWeight() {
        path.setTotalImpact(1.0);
        path.addEdgeWeight(0.8);
        
        assertEquals(1, path.getEdgeWeights().size());
        assertEquals(0.8, path.getEdgeWeights().get(0), 0.001);
        assertEquals(0.8, path.getTotalImpact(), 0.001);
    }

    @Test
    void testGetLength() {
        path.addNode("GDP");
        path.addNode("PMI");
        path.addNode("CPI");
        
        assertEquals(2, path.getLength());
    }

    @Test
    void testSetTotalImpact() {
        path.setTotalImpact(0.5);
        assertEquals(0.5, path.getTotalImpact(), 0.001);
    }

    @Test
    void testToString() {
        path.addNode("GDP");
        path.addNode("CPI");
        
        String str = path.toString();
        assertNotNull(str);
        assertTrue(str.contains("GDP"));
        assertTrue(str.contains("CPI"));
    }
}
