package com.mecn.network;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统重要性机构识别结果
 */
public class SystemicImportance {
    
    private String nodeId;                      // 节点 ID
    private double importanceScore;             // 重要性得分
    private double networkEfficiencyLoss;       // 移除后网络效率损失
    private int affectedNodesCount;             // 影响的节点数量
    private Map<String, Object> metadata;       // 元数据
    
    public SystemicImportance(String nodeId) {
        this.nodeId = nodeId;
        this.metadata = new HashMap<>();
    }
    
    // Getters and Setters
    public String getNodeId() {
        return nodeId;
    }
    
    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }
    
    public double getImportanceScore() {
        return importanceScore;
    }
    
    public void setImportanceScore(double importanceScore) {
        this.importanceScore = importanceScore;
    }
    
    public double getNetworkEfficiencyLoss() {
        return networkEfficiencyLoss;
    }
    
    public void setNetworkEfficiencyLoss(double networkEfficiencyLoss) {
        this.networkEfficiencyLoss = networkEfficiencyLoss;
    }
    
    public int getAffectedNodesCount() {
        return affectedNodesCount;
    }
    
    public void setAffectedNodesCount(int affectedNodesCount) {
        this.affectedNodesCount = affectedNodesCount;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    @Override
    public String toString() {
        return "SystemicImportance{" +
                "nodeId='" + nodeId + '\'' +
                ", score=" + String.format("%.4f", importanceScore) +
                ", efficiencyLoss=" + String.format("%.4f", networkEfficiencyLoss) +
                '}';
    }
}
