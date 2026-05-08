package com.mecn.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CausalEdge 单元测试
 */
class CausalEdgeTest {

    private CausalEdge edge;

    @BeforeEach
    void setUp() {
        edge = new CausalEdge("GDP", "CPI", 0.75, 0.95);
    }

    @Test
    void testDefaultConstructor() {
        CausalEdge e = new CausalEdge();
        assertNull(e.getSource());
        assertNull(e.getTarget());
        assertEquals(0.0, e.getStrength());
        assertEquals(0.0, e.getConfidence());
    }

    @Test
    void testParameterizedConstructor() {
        assertEquals("GDP", edge.getSource());
        assertEquals("CPI", edge.getTarget());
        assertEquals(0.75, edge.getStrength(), 0.001);
        assertEquals(0.95, edge.getConfidence(), 0.001);
    }

    @Test
    void testThreeArgConstructor() {
        CausalEdge e = new CausalEdge("A", "B", 0.5);
        assertEquals(1.0, e.getConfidence());
    }

    @Test
    void testSetters() {
        edge.setSource("PMI");
        edge.setTarget("M2");
        edge.setStrength(0.6);
        edge.setConfidence(0.8);
        edge.setMethod("Granger");
        edge.setPValue(0.03);

        assertEquals("PMI", edge.getSource());
        assertEquals("M2", edge.getTarget());
        assertEquals(0.6, edge.getStrength(), 0.001);
        assertEquals(0.8, edge.getConfidence(), 0.001);
        assertEquals("Granger", edge.getMethod());
        assertEquals(0.03, edge.getPValue(), 0.001);
    }

    @Test
    void testIsSignificantWithPValue() {
        edge.setPValue(0.03);
        assertTrue(edge.isSignificant(0.05));
        assertFalse(edge.isSignificant(0.01));
    }

    @Test
    void testIsSignificantWithConfidence() {
        edge.setConfidence(0.97);
        assertTrue(edge.isSignificant(0.05));
        assertFalse(edge.isSignificant(0.02));
    }

    @Test
    void testEquals() {
        CausalEdge edge2 = new CausalEdge("GDP", "CPI", 0.75, 0.95);
        assertEquals(edge, edge2);
    }

    @Test
    void testEqualsSameObject() {
        assertEquals(edge, edge);
    }

    @Test
    void testEqualsNull() {
        assertNotEquals(edge, null);
    }

    @Test
    void testEqualsDifferentType() {
        assertNotEquals(edge, "string");
    }

    @Test
    void testHashCode() {
        CausalEdge edge2 = new CausalEdge("GDP", "CPI", 0.75, 0.95);
        assertEquals(edge.hashCode(), edge2.hashCode());
    }

    @Test
    void testToString() {
        String str = edge.toString();
        assertNotNull(str);
        assertTrue(str.contains("GDP"));
        assertTrue(str.contains("CPI"));
    }
}
