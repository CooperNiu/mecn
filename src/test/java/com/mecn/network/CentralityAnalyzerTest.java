package com.mecn.network;

import com.mecn.model.CentralityResult;
import com.mecn.model.NetworkGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 中心性分析器测试
 * 
 * 使用预构建有向加权图替代全链路计算，更快、更确定。
 */
public class CentralityAnalyzerTest {

    private NetworkGraph network;
    private CentralityAnalyzer analyzer;

    @BeforeEach
    void setUp() {
        var graph = new DefaultDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        for (int i = 0; i < 5; i++) graph.addVertex("N" + i);
        graph.setEdgeWeight(graph.addEdge("N0", "N1"), 0.8);
        graph.setEdgeWeight(graph.addEdge("N1", "N2"), 0.6);
        graph.setEdgeWeight(graph.addEdge("N2", "N3"), 0.4);
        graph.setEdgeWeight(graph.addEdge("N0", "N3"), 0.3);
        graph.setEdgeWeight(graph.addEdge("N3", "N0"), 0.2);
        graph.setEdgeWeight(graph.addEdge("N1", "N4"), 0.1);
        network = new NetworkGraph(graph);
        analyzer = new CentralityAnalyzer(network.getGraph());
    }

    @Test
    void testAnalyze() {
        List<CentralityResult> results = analyzer.analyze();
        assertNotNull(results);
        assertEquals(5, results.size());
        for (CentralityResult r : results) {
            assertNotNull(r.getNodeId());
            assertNotNull(r.getDegreeCentrality());
            assertNotNull(r.getBetweennessCentrality());
            assertNotNull(r.getClosenessCentrality());
            assertNotNull(r.getPageRank());
            assertNotNull(r.getEigenvectorCentrality());
        }
    }

    @Test
    void testTopKByComposite() {
        analyzer.analyze();
        var top = analyzer.getTopKNodes(3, "composite");
        assertEquals(3, top.size());
        for (int i = 0; i < top.size() - 1; i++)
            assertTrue(top.get(i).getCompositeScore() >= top.get(i + 1).getCompositeScore());
    }

    @Test
    void testGetNodeResult() {
        analyzer.analyze();
        CentralityResult r = analyzer.getNodeResult("N0");
        assertNotNull(r);
        assertEquals("N0", r.getNodeId());
        assertTrue(r.getDegreeCentrality() > 0);
    }

    @Test
    void testSingleNodeGraph() {
        var g = new DefaultDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        g.addVertex("X");
        var a = new CentralityAnalyzer(g);
        var r = a.analyze();
        assertEquals(1, r.size());
        assertEquals(0.0, r.get(0).getDegreeCentrality(), 0.0);
    }

    @Test
    void testEmptyGraph() {
        var g = new DefaultDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        var a = new CentralityAnalyzer(g);
        assertTrue(a.analyze().isEmpty());
    }
}
