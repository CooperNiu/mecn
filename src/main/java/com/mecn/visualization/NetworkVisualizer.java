package com.mecn.visualization;

import com.mecn.causal.CausalResult;
import com.mecn.model.NetworkGraph;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.*;

/**
 * 网络可视化工具
 * 
 * 将因果网络转换为可视化格式（D3.js力导向图、热力图等）
 */
public class NetworkVisualizer {
    
    /**
     * 导出为 D3.js 力导向图 JSON 格式
     * 
     * @param network 网络图
     * @return D3.js格式的JSON字符串
     */
    public String exportToD3ForceGraph(NetworkGraph network) {
        if (network == null || network.getGraph() == null) {
            throw new IllegalArgumentException("Network cannot be null");
        }
        
        Graph<String, DefaultWeightedEdge> graph = network.getGraph();
        
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        
        // 节点数据
        json.append("  \"nodes\": [\n");
        List<String> nodes = new ArrayList<>(graph.vertexSet());
        for (int i = 0; i < nodes.size(); i++) {
            String node = nodes.get(i);
            json.append("    {\"id\": \"").append(escapeJson(node)).append("\"");
            
            // 添加节点属性（如果有）
            Map<String, Object> nodeAttrs = network.getNodeAttributes(node);
            if (nodeAttrs != null && !nodeAttrs.isEmpty()) {
                json.append(", \"attributes\": {");
                boolean first = true;
                for (Map.Entry<String, Object> entry : nodeAttrs.entrySet()) {
                    if (!first) json.append(", ");
                    json.append("\"").append(escapeJson(entry.getKey())).append("\": ");
                    json.append(formatJsonValue(entry.getValue()));
                    first = false;
                }
                json.append("}");
            }
            
            json.append("}");
            if (i < nodes.size() - 1) json.append(",");
            json.append("\n");
        }
        json.append("  ],\n");
        
        // 边数据
        json.append("  \"links\": [\n");
        List<DefaultWeightedEdge> edges = new ArrayList<>(graph.edgeSet());
        for (int i = 0; i < edges.size(); i++) {
            DefaultWeightedEdge edge = edges.get(i);
            String source = graph.getEdgeSource(edge);
            String target = graph.getEdgeTarget(edge);
            double weight = graph.getEdgeWeight(edge);
            
            json.append("    {");
            json.append("\"source\": \"").append(escapeJson(source)).append("\", ");
            json.append("\"target\": \"").append(escapeJson(target)).append("\", ");
            json.append("\"weight\": ").append(String.format("%.4f", weight));
            json.append("}");
            
            if (i < edges.size() - 1) json.append(",");
            json.append("\n");
        }
        json.append("  ]\n");
        
        json.append("}");
        
        return json.toString();
    }
    
    /**
     * 生成因果强度热力图数据
     * 
     * @param causalResult 因果发现结果
     * @param nodeNames 节点名称列表
     * @return 热力图数据JSON字符串
     */
    public String generateHeatmapData(CausalResult causalResult, List<String> nodeNames) {
        if (causalResult == null || causalResult.getConfidenceMatrix() == null) {
            throw new IllegalArgumentException("CausalResult cannot be null");
        }
        
        double[][] confidenceMatrix = causalResult.getConfidenceMatrix();
        int n = confidenceMatrix.length;
        
        if (nodeNames == null || nodeNames.size() != n) {
            // 如果没有提供节点名称，使用默认名称
            nodeNames = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                nodeNames.add("Node_" + i);
            }
        }
        
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        
        // 轴标签
        json.append("  \"xLabels\": [");
        for (int i = 0; i < n; i++) {
            json.append("\"").append(escapeJson(nodeNames.get(i))).append("\"");
            if (i < n - 1) json.append(", ");
        }
        json.append("],\n");
        
        json.append("  \"yLabels\": [");
        for (int i = 0; i < n; i++) {
            json.append("\"").append(escapeJson(nodeNames.get(i))).append("\"");
            if (i < n - 1) json.append(", ");
        }
        json.append("],\n");
        
        // 热力图数据
        json.append("  \"data\": [\n");
        for (int i = 0; i < n; i++) {
            json.append("    [");
            for (int j = 0; j < n; j++) {
                json.append(String.format("%.4f", confidenceMatrix[i][j]));
                if (j < n - 1) json.append(", ");
            }
            json.append("]");
            if (i < n - 1) json.append(",");
            json.append("\n");
        }
        json.append("  ]\n");
        
        json.append("}");
        
        return json.toString();
    }
    
    /**
     * 导出网络统计信息
     * 
     * @param network 网络图
     * @return 统计信息JSON
     */
    public String exportNetworkStatistics(NetworkGraph network) {
        if (network == null || network.getGraph() == null) {
            throw new IllegalArgumentException("Network cannot be null");
        }
        
        Graph<String, DefaultWeightedEdge> graph = network.getGraph();
        
        int nodeCount = graph.vertexSet().size();
        int edgeCount = graph.edgeSet().size();
        
        // 计算平均度
        double totalDegree = 0;
        for (String node : graph.vertexSet()) {
            totalDegree += graph.degreeOf(node);
        }
        double avgDegree = nodeCount > 0 ? totalDegree / nodeCount : 0;
        
        // 计算密度
        double density = nodeCount > 1 ? 
            (2.0 * edgeCount) / (nodeCount * (nodeCount - 1)) : 0;
        
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"nodeCount\": ").append(nodeCount).append(",\n");
        json.append("  \"edgeCount\": ").append(edgeCount).append(",\n");
        json.append("  \"averageDegree\": ").append(String.format("%.4f", avgDegree)).append(",\n");
        json.append("  \"density\": ").append(String.format("%.6f", density)).append(",\n");
        
        // 添加元数据
        Map<String, Object> metadata = network.getMetadata();
        if (metadata != null && !metadata.isEmpty()) {
            json.append("  \"metadata\": {\n");
            boolean first = true;
            for (Map.Entry<String, Object> entry : metadata.entrySet()) {
                if (!first) json.append(",\n");
                json.append("    \"").append(escapeJson(entry.getKey())).append("\": ");
                json.append(formatJsonValue(entry.getValue()));
                first = false;
            }
            json.append("\n  }\n");
        }
        
        json.append("}");
        
        return json.toString();
    }
    
    /**
     * 转义JSON字符串中的特殊字符
     */
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    /**
     * 格式化JSON值
     */
    private String formatJsonValue(Object value) {
        if (value == null) {
            return "null";
        } else if (value instanceof Number) {
            return value.toString();
        } else if (value instanceof Boolean) {
            return value.toString();
        } else {
            return "\"" + escapeJson(value.toString()) + "\"";
        }
    }
}
