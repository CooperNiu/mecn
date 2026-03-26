package com.mecn.causal;

import com.mecn.model.CausalEdge;
import com.mecn.model.NetworkGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 因果发现结果类
 */
public class CausalResult {
    
    private double[][] adjacencyMatrix;         // 邻接矩阵 [N][N]
    private double[][] confidenceMatrix;        // 置信度矩阵 [N][N]
    private List<CausalEdge> edges;             // 因果边列表
    private Map<String, Object> metadata;       // 元数据
    
    public CausalResult(int numNodes) {
        this.adjacencyMatrix = new double[numNodes][numNodes];
        this.confidenceMatrix = new double[numNodes][numNodes];
        this.edges = new ArrayList<>();
        this.metadata = new HashMap<>();
    }
    
    // Getters and Setters
    public double[][] getAdjacencyMatrix() {
        return adjacencyMatrix;
    }
    
    public void setAdjacencyMatrix(double[][] adjacencyMatrix) {
        this.adjacencyMatrix = adjacencyMatrix;
    }
    
    public double[][] getConfidenceMatrix() {
        return confidenceMatrix;
    }
    
    public void setConfidenceMatrix(double[][] confidenceMatrix) {
        this.confidenceMatrix = confidenceMatrix;
    }
    
    public List<CausalEdge> getEdges() {
        return edges;
    }
    
    public void setEdges(List<CausalEdge> edges) {
        this.edges = edges;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    public void addMetadata(String key, Object value) {
        this.metadata.put(key, value);
    }
    
    /**
     * 转换为网络图
     */
    public NetworkGraph toNetworkGraph(List<String> nodeNames) {
        NetworkGraph graph = new NetworkGraph();
        
        // 添加节点
        for (String name : nodeNames) {
            graph.addNode(name, null);
        }
        
        // 添加边
        int n = adjacencyMatrix.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (adjacencyMatrix[i][j] != 0) {
                    graph.addEdge(nodeNames.get(j), nodeNames.get(i), 
                                  Math.abs(adjacencyMatrix[i][j]));
                }
            }
        }
        
        graph.addMetadata("confidenceMatrix", confidenceMatrix);
        return graph;
    }
}
