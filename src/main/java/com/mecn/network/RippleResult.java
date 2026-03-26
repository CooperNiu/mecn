package com.mecn.network;

import java.util.*;

/**
 * 涟漪效应模拟结果
 * 
 * 存储冲击传播的时间序列数据
 */
public class RippleResult {
    
    private String shockNode;                     // 受冲击节点 ID
    private double shockMagnitude;                // 冲击幅度
    private int timeSteps;                        // 时间步数
    private Map<String, double[]> nodeResponses;  // 各节点的响应时间序列
    private List<Double> totalImpactPerStep;      // 每个时间步的总影响
    private Map<String, Object> metadata;         // 元数据
    
    public RippleResult(String shockNode, double shockMagnitude, int timeSteps) {
        this.shockNode = shockNode;
        this.shockMagnitude = shockMagnitude;
        this.timeSteps = timeSteps;
        this.nodeResponses = new HashMap<>();
        this.totalImpactPerStep = new ArrayList<>(timeSteps);
        this.metadata = new HashMap<>();
    }
    
    /**
     * 添加节点响应
     */
    public void addNodeResponse(String nodeId, double[] response) {
        this.nodeResponses.put(nodeId, response);
    }
    
    /**
     * 获取某节点的完整响应时间序列
     */
    public double[] getNodeResponse(String nodeId) {
        return nodeResponses.get(nodeId);
    }
    
    /**
     * 获取某节点在特定时间步的响应
     */
    public double getResponseAtTime(String nodeId, int timeStep) {
        double[] response = nodeResponses.get(nodeId);
        if (response != null && timeStep >= 0 && timeStep < response.length) {
            return response[timeStep];
        }
        return 0.0;
    }
    
    /**
     * 获取受影响最大的节点（按累积影响）
     */
    public List<Map.Entry<String, Double>> getMostAffectedNodes(int topN) {
        List<Map.Entry<String, Double>> impacts = new ArrayList<>();
        
        for (Map.Entry<String, double[]> entry : nodeResponses.entrySet()) {
            if (!entry.getKey().equals(shockNode)) {
                double cumulativeImpact = 0.0;
                for (double val : entry.getValue()) {
                    cumulativeImpact += Math.abs(val);
                }
                impacts.add(new AbstractMap.SimpleEntry<>(entry.getKey(), cumulativeImpact));
            }
        }
        
        // 按累积影响排序
        impacts.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        
        return impacts.subList(0, Math.min(topN, impacts.size()));
    }
    
    /**
     * 获取峰值影响时间
     */
    public int getPeakImpactTime(String nodeId) {
        double[] response = nodeResponses.get(nodeId);
        if (response == null) return -1;
        
        int peakTime = 0;
        double maxImpact = 0.0;
        
        for (int t = 0; t < response.length; t++) {
            double impact = Math.abs(response[t]);
            if (impact > maxImpact) {
                maxImpact = impact;
                peakTime = t;
            }
        }
        
        return peakTime;
    }
    
    // Getters and Setters
    public String getShockNode() {
        return shockNode;
    }
    
    public double getShockMagnitude() {
        return shockMagnitude;
    }
    
    public int getTimeSteps() {
        return timeSteps;
    }
    
    public Map<String, double[]> getNodeResponses() {
        return nodeResponses;
    }
    
    public List<Double> getTotalImpactPerStep() {
        return totalImpactPerStep;
    }
    
    public void setTotalImpactPerStep(List<Double> totalImpactPerStep) {
        this.totalImpactPerStep = totalImpactPerStep;
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
     * 获取网络中所有节点的列表
     */
    public Set<String> getAllNodes() {
        return nodeResponses.keySet();
    }
}
