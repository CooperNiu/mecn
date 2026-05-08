package com.mecn.network;

import java.util.ArrayList;
import java.util.List;

/**
 * 风险传导路径
 * 
 * 表示从源节点到目标节点的风险传播路径
 */
public class RiskPath {
    
    private String source;                      // 起始节点
    private String target;                      // 终止节点
    private List<String> path;                  // 路径节点序列
    private double totalImpact;                 // 总影响强度
    private List<Double> edgeWeights;           // 边权重序列
    
    public RiskPath(String source, String target) {
        this.source = source;
        this.target = target;
        this.path = new ArrayList<>();
        this.edgeWeights = new ArrayList<>();
        this.totalImpact = 0.0;
    }
    
    /**
     * 添加路径节点
     */
    public void addNode(String nodeId) {
        this.path.add(nodeId);
    }
    
    /**
     * 添加边权重
     */
    public void addEdgeWeight(double weight) {
        this.edgeWeights.add(weight);
        this.totalImpact *= weight; // 累积影响
    }
    
    /**
     * 获取路径长度（边数）
     */
    public int getLength() {
        return path.size() - 1;
    }
    
    // Getters and Setters
    public String getSource() {
        return source;
    }
    
    public String getTarget() {
        return target;
    }
    
    public List<String> getPath() {
        return path;
    }
    
    public double getTotalImpact() {
        return totalImpact;
    }
    
    public void setTotalImpact(double totalImpact) {
        this.totalImpact = totalImpact;
    }
    
    public List<Double> getEdgeWeights() {
        return edgeWeights;
    }
    
    @Override
    public String toString() {
        return "RiskPath{" +
                "source='" + source + '\'' +
                ", target='" + target + '\'' +
                ", path=" + path +
                ", totalImpact=" + String.format("%.4f", totalImpact) +
                '}';
    }
}
