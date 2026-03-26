package com.mecn.network;

import com.mecn.causal.CausalResult;
import com.mecn.model.NetworkGraph;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.List;

/**
 * 网络构建器
 * 
 * 从因果矩阵构建 JGraphT 网络拓扑
 */
public class NetworkBuilder {
    
    private double edgeThreshold = 0.08;  // 边权重阈值
    
    public NetworkBuilder() {
    }
    
    public NetworkBuilder(double edgeThreshold) {
        this.edgeThreshold = edgeThreshold;
    }
    
    /**
     * 从因果结果构建网络图
     */
    public NetworkGraph build(CausalResult causalResult, List<String> nodeNames) {
        double[][] adjacencyMatrix = causalResult.getAdjacencyMatrix();
        int N = adjacencyMatrix.length;
        
        Graph<String, DefaultWeightedEdge> graph = 
            new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        
        for (String name : nodeNames) {
            graph.addVertex(name);
        }
        
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                double weight = adjacencyMatrix[i][j];
                if (Math.abs(weight) >= edgeThreshold) {
                    DefaultWeightedEdge edge = graph.addEdge(nodeNames.get(i), nodeNames.get(j));
                    if (edge != null) {
                        graph.setEdgeWeight(edge, Math.abs(weight));
                    }
                }
            }
        }
        
        NetworkGraph networkGraph = new NetworkGraph(graph);
        networkGraph.addMetadata("edgeThreshold", edgeThreshold);
        networkGraph.addMetadata("numNodes", N);
        networkGraph.addMetadata("numEdges", graph.edgeSet().size());
        
        return networkGraph;
    }
    
    /**
     * 从邻接矩阵直接构建网络
     */
    public NetworkGraph build(double[][] adjacencyMatrix, List<String> nodeNames) {
        int N = adjacencyMatrix.length;
        
        Graph<String, DefaultWeightedEdge> graph = 
            new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        
        for (String name : nodeNames) {
            graph.addVertex(name);
        }
        
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                double weight = adjacencyMatrix[i][j];
                if (Math.abs(weight) >= edgeThreshold) {
                    DefaultWeightedEdge edge = graph.addEdge(nodeNames.get(i), nodeNames.get(j));
                    if (edge != null) {
                        graph.setEdgeWeight(edge, Math.abs(weight));
                    }
                }
            }
        }
        
        return new NetworkGraph(graph);
    }
    
    /**
     * 过滤弱连接
     */
    public void pruneWeakEdges(Graph<String, DefaultWeightedEdge> graph, double threshold) {
        // 收集需要删除的边，避免并发修改异常
        java.util.List<DefaultWeightedEdge> edgesToRemove = new java.util.ArrayList<>();
        for (DefaultWeightedEdge edge : graph.edgeSet()) {
            if (graph.getEdgeWeight(edge) < threshold) {
                edgesToRemove.add(edge);
            }
        }
        
        for (DefaultWeightedEdge edge : edgesToRemove) {
            graph.removeEdge(edge);
        }
    }
    
    public double getEdgeThreshold() {
        return edgeThreshold;
    }
    
    public void setEdgeThreshold(double edgeThreshold) {
        this.edgeThreshold = edgeThreshold;
    }
}
