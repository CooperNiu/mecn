package com.mecn.causal;

import com.mecn.model.CausalEdge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CausalResult 单元测试
 */
class CausalResultTest {

    private CausalResult result;

    @BeforeEach
    void setUp() {
        result = new CausalResult(3);
    }

    @Test
    void testInitialization() {
        assertNotNull(result.getAdjacencyMatrix());
        assertNotNull(result.getConfidenceMatrix());
        assertNotNull(result.getEdges());
        assertNotNull(result.getMetadata());
        assertEquals(3, result.getAdjacencyMatrix().length);
        assertEquals(3, result.getConfidenceMatrix().length);
    }

    @Test
    void testSetAdjacencyMatrix() {
        double[][] matrix = {{0, 1, 0}, {0, 0, 1}, {1, 0, 0}};
        result.setAdjacencyMatrix(matrix);
        assertArrayEquals(matrix, result.getAdjacencyMatrix());
    }

    @Test
    void testSetConfidenceMatrix() {
        double[][] matrix = {{0, 0.8, 0}, {0, 0, 0.9}, {0.7, 0, 0}};
        result.setConfidenceMatrix(matrix);
        assertArrayEquals(matrix, result.getConfidenceMatrix());
    }

    @Test
    void testEdges() {
        List<CausalEdge> edges = new ArrayList<>();
        edges.add(new CausalEdge("A", "B", 0.5));
        result.setEdges(edges);
        assertEquals(1, result.getEdges().size());
    }

    @Test
    void testMetadata() {
        result.addMetadata("method", "LASSO");
        assertEquals("LASSO", result.getMetadata().get("method"));
    }

    @Test
    void testToNetworkGraph() {
        // 设置邻接矩阵
        double[][] matrix = {{0, 0.5, 0}, {0, 0, 0.8}, {0, 0, 0}};
        result.setAdjacencyMatrix(matrix);

        // 转换为网络图
        List<String> nodeNames = List.of("GDP", "CPI", "PMI");
        var graph = result.toNetworkGraph(nodeNames);

        assertNotNull(graph);
        assertEquals(3, graph.getNodes().size());
        assertTrue(graph.getNodes().contains("GDP"));
        assertTrue(graph.getNodes().contains("CPI"));
        assertTrue(graph.getNodes().contains("PMI"));
    }
}
