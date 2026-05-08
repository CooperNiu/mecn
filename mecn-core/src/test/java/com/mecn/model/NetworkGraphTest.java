package com.mecn.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * NetworkGraph 单元测试
 */
class NetworkGraphTest {

    private NetworkGraph graph;

    @BeforeEach
    void setUp() {
        graph = new NetworkGraph();
    }

    @Test
    void testInitialization() {
        assertNotNull(graph.getNodes());
        assertNotNull(graph.getEdges());
        assertTrue(graph.getNodes().isEmpty());
        assertTrue(graph.getEdges().isEmpty());
    }

    @Test
    void testAddNode() {
        graph.addNode("GDP", null);
        assertEquals(1, graph.getNodes().size());
        assertTrue(graph.getNodes().contains("GDP"));
    }

    @Test
    void testAddNodeDuplicate() {
        graph.addNode("GDP", null);
        graph.addNode("GDP", null);
        assertEquals(1, graph.getNodes().size());
    }

    @Test
    void testAddEdge() {
        graph.addNode("GDP", null);
        graph.addNode("CPI", null);
        graph.addEdge("GDP", "CPI", 0.75);

        assertEquals(1, graph.getEdges().size());
        assertEquals(0.75, graph.getEdgeWeight("GDP", "CPI"), 0.001);
    }

    @Test
    void testAddEdgeNonexistentNode() {
        graph.addEdge("GDP", "CPI", 0.75);
        assertTrue(graph.getEdges().isEmpty());
    }

    @Test
    void testGetIncomingNeighbors() {
        graph.addNode("GDP", null);
        graph.addNode("CPI", null);
        graph.addNode("PMI", null);
        graph.addEdge("GDP", "CPI", 0.5);
        graph.addEdge("PMI", "CPI", 0.8);

        Set<String> neighbors = graph.getIncomingNeighbors("CPI");
        assertEquals(2, neighbors.size());
        assertTrue(neighbors.contains("GDP"));
        assertTrue(neighbors.contains("PMI"));
    }

    @Test
    void testGetOutgoingNeighbors() {
        graph.addNode("GDP", null);
        graph.addNode("CPI", null);
        graph.addNode("PMI", null);
        graph.addEdge("GDP", "CPI", 0.5);
        graph.addEdge("GDP", "PMI", 0.6);

        Set<String> neighbors = graph.getOutgoingNeighbors("GDP");
        assertEquals(2, neighbors.size());
        assertTrue(neighbors.contains("CPI"));
        assertTrue(neighbors.contains("PMI"));
    }

    @Test
    void testGetIndegree() {
        graph.addNode("GDP", null);
        graph.addNode("CPI", null);
        graph.addNode("PMI", null);
        graph.addEdge("GDP", "CPI", 0.5);
        graph.addEdge("PMI", "CPI", 0.8);

        assertEquals(2, graph.getInDegree("CPI"));
        assertEquals(0, graph.getInDegree("GDP"));
    }

    @Test
    void testGetOutdegree() {
        graph.addNode("GDP", null);
        graph.addNode("CPI", null);
        graph.addNode("PMI", null);
        graph.addEdge("GDP", "CPI", 0.5);
        graph.addEdge("GDP", "PMI", 0.6);

        assertEquals(2, graph.getOutDegree("GDP"));
        assertEquals(0, graph.getOutDegree("CPI"));
    }

    @Test
    void testMetadata() {
        graph.addMetadata("method", "LASSO");
        assertEquals("LASSO", graph.getMetadata().get("method"));
    }

    @Test
    void testGetNetworkStatistics() {
        graph.addNode("GDP", null);
        graph.addNode("CPI", null);
        graph.addEdge("GDP", "CPI", 0.5);
        
        var stats = graph.getNetworkStatistics();
        assertNotNull(stats);
        assertEquals(2, stats.get("nodeCount"));
        assertEquals(1, stats.get("edgeCount"));
    }

    @Test
    void testGetGraph() {
        assertNotNull(graph.getGraph());
    }
}
