package com.mecn.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 中心性分析结果
 * 
 * 存储节点的各种中心性指标计算结果
 */
public class CentralityResult {
    
    private String nodeId;                          // 节点 ID
    private Double degreeCentrality;                // 度中心性
    private Double inDegreeCentrality;              // 入度中心性
    private Double outDegreeCentrality;             // 出度中心性
    private Double closenessCentrality;             // 接近中心性
    private Double betweennessCentrality;           // 中介中心性
    private Double pageRank;                        // PageRank
    private Double eigenvectorCentrality;           // 特征向量中心性
    private Map<String, Double> customMetrics;      // 自定义指标
    
    public CentralityResult() {
        this.customMetrics = new HashMap<>();
    }
    
    public CentralityResult(String nodeId) {
        this();
        this.nodeId = nodeId;
    }
    
    // Getters and Setters
    public String getNodeId() {
        return nodeId;
    }
    
    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }
    
    public Double getDegreeCentrality() {
        return degreeCentrality;
    }
    
    public void setDegreeCentrality(Double degreeCentrality) {
        this.degreeCentrality = degreeCentrality;
    }
    
    public Double getInDegreeCentrality() {
        return inDegreeCentrality;
    }
    
    public void setInDegreeCentrality(Double inDegreeCentrality) {
        this.inDegreeCentrality = inDegreeCentrality;
    }
    
    public Double getOutDegreeCentrality() {
        return outDegreeCentrality;
    }
    
    public void setOutDegreeCentrality(Double outDegreeCentrality) {
        this.outDegreeCentrality = outDegreeCentrality;
    }
    
    public Double getClosenessCentrality() {
        return closenessCentrality;
    }
    
    public void setClosenessCentrality(Double closenessCentrality) {
        this.closenessCentrality = closenessCentrality;
    }
    
    public Double getBetweennessCentrality() {
        return betweennessCentrality;
    }
    
    public void setBetweennessCentrality(Double betweennessCentrality) {
        this.betweennessCentrality = betweennessCentrality;
    }
    
    public Double getPageRank() {
        return pageRank;
    }
    
    public void setPageRank(Double pageRank) {
        this.pageRank = pageRank;
    }
    
    public Double getEigenvectorCentrality() {
        return eigenvectorCentrality;
    }
    
    public void setEigenvectorCentrality(Double eigenvectorCentrality) {
        this.eigenvectorCentrality = eigenvectorCentrality;
    }
    
    public Map<String, Double> getCustomMetrics() {
        return customMetrics;
    }
    
    public void setCustomMetrics(Map<String, Double> customMetrics) {
        this.customMetrics = customMetrics;
    }
    
    public void addCustomMetric(String name, Double value) {
        this.customMetrics.put(name, value);
    }
    
    /**
     * 获取综合重要性得分（简单平均）
     */
    public double getCompositeScore() {
        double sum = 0.0;
        int count = 0;
        
        if (degreeCentrality != null) { sum += degreeCentrality; count++; }
        if (closenessCentrality != null) { sum += closenessCentrality; count++; }
        if (betweennessCentrality != null) { sum += betweennessCentrality; count++; }
        if (pageRank != null) { sum += pageRank; count++; }
        if (eigenvectorCentrality != null) { sum += eigenvectorCentrality; count++; }
        
        return count > 0 ? sum / count : 0.0;
    }
    
    @Override
    public String toString() {
        return "CentralityResult{" +
                "nodeId='" + nodeId + '\'' +
                ", degree=" + degreeCentrality +
                ", closeness=" + closenessCentrality +
                ", betweenness=" + betweennessCentrality +
                ", pageRank=" + pageRank +
                ", composite=" + String.format("%.4f", getCompositeScore()) +
                '}';
    }
}
