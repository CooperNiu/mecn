package com.mecn.model;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.*;

/**
 * 网络图封装类
 * 
 * 封装 JGraphT 图结构，提供高级操作接口
 */
public class NetworkGraph {
    
    private Graph<String, DefaultWeightedEdge> graph;
    private List<EconomicIndicator> indicators;
    private Map<String, Object> metadata;
    
    public NetworkGraph() {
        this.graph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        this.indicators = new ArrayList<>();
        this.metadata = new HashMap<>();
    }
    
    public NetworkGraph(Graph<String, DefaultWeightedEdge> graph) {
        this.graph = graph;
        this.indicators = new ArrayList<>();
        this.metadata = new HashMap<>();
    }
    
    /**
     * 添加节点（经济指标）
     */
    public void addNode(String nodeId, EconomicIndicator indicator) {
        if (!graph.containsVertex(nodeId)) {
            graph.addVertex(nodeId);
            indicators.add(indicator);
        }
    }
    
    /**
     * 添加因果边
     */
    public void addEdge(String source, String target, double weight) {
        if (graph.containsVertex(source) && graph.containsVertex(target)) {
            DefaultWeightedEdge edge = graph.addEdge(source, target);
            if (edge != null) {
                graph.setEdgeWeight(edge, Math.abs(weight));
            }
        }
    }
    
    /**
     * 获取所有节点
     */
    public Set<String> getNodes() {
        return graph.vertexSet();
    }
    
    /**
     * 获取所有边
     */
    public Set<DefaultWeightedEdge> getEdges() {
        return graph.edgeSet();
    }
    
    /**
     * 获取边的权重
     */
    public double getEdgeWeight(String source, String target) {
        DefaultWeightedEdge edge = graph.getEdge(source, target);
        return edge != null ? graph.getEdgeWeight(edge) : 0.0;
    }
    
    /**
     * 获取节点的入度邻居（影响该节点的节点）
     */
    public Set<String> getIncomingNeighbors(String nodeId) {
        Set<String> neighbors = new HashSet<>();
        Set<DefaultWeightedEdge> incomingEdges = graph.incomingEdgesOf(nodeId);
        for (DefaultWeightedEdge edge : incomingEdges) {
            neighbors.add(graph.getEdgeSource(edge));
        }
        return neighbors;
    }
    
    /**
     * 获取节点的出度邻居（该节点影响的节点）
     */
    public Set<String> getOutgoingNeighbors(String nodeId) {
        Set<String> neighbors = new HashSet<>();
        Set<DefaultWeightedEdge> outgoingEdges = graph.outgoingEdgesOf(nodeId);
        for (DefaultWeightedEdge edge : outgoingEdges) {
            neighbors.add(graph.getEdgeTarget(edge));
        }
        return neighbors;
    }
    
    /**
     * 获取节点的入度
     */
    public int getInDegree(String nodeId) {
        return graph.inDegreeOf(nodeId);
    }
    
    /**
     * 获取节点的出度
     */
    public int getOutDegree(String nodeId) {
        return graph.outDegreeOf(nodeId);
    }
    
    /**
     * 获取网络统计信息
     */
    public Map<String, Object> getNetworkStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("nodeCount", graph.vertexSet().size());
        stats.put("edgeCount", graph.edgeSet().size());
        
        // 计算网络密度
        int n = graph.vertexSet().size();
        int maxEdges = n * (n - 1);
        stats.put("density", maxEdges > 0 ? (double) graph.edgeSet().size() / maxEdges : 0.0);
        
        stats.put("metadata", metadata);
        return stats;
    }
    
    /**
     * 获取底层 JGraphT 图
     */
    public Graph<String, DefaultWeightedEdge> getGraph() {
        return graph;
    }
    
    /**
     * 设置元数据
     */
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void addMetadata(String key, Object value) {
        this.metadata.put(key, value);
    }
    
    public List<EconomicIndicator> getIndicators() {
        return indicators;
    }
    
    @Override
    public String toString() {
        return "NetworkGraph{" +
                "nodes=" + graph.vertexSet().size() +
                ", edges=" + graph.edgeSet().size() +
                '}';
    }
}
