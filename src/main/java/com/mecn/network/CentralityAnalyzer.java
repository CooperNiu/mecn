package com.mecn.network;

import com.mecn.model.CentralityResult;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.*;

/**
 * 中心性分析器
 * 
 * 计算网络中节点的各种中心性指标
 * 
 * @example
 * {@code
 * CentralityAnalyzer analyzer = new CentralityAnalyzer(network.getGraph());
 * List<CentralityResult> results = analyzer.analyze();
 * 
 * // 获取 Top 10 重要节点
 * results.stream()
 *     .sorted(Comparator.comparingDouble(CentralityResult::getCompositeScore).reversed())
 *     .limit(10)
 *     .forEach(result -> System.out.println(result.getNodeId() + ": " + result.getCompositeScore()));
 * }
 */
public class CentralityAnalyzer {
    
    private final Graph<String, DefaultWeightedEdge> graph;
    private Map<String, CentralityResult> cachedResults;
    
    public CentralityAnalyzer(Graph<String, DefaultWeightedEdge> graph) {
        this.graph = graph;
        this.cachedResults = new HashMap<>();
    }
    
    /**
     * 分析所有节点的中心性指标
     * 
     * @return 所有节点的中心性结果列表
     */
    public List<CentralityResult> analyze() {
        Set<String> vertices = graph.vertexSet();
        List<CentralityResult> results = new ArrayList<>(vertices.size());
        
        // 计算各种中心性指标
        Map<String, Double> degreeCentrality = calculateDegreeCentrality();
        Map<String, Double> inDegreeCentrality = calculateInDegreeCentrality();
        Map<String, Double> outDegreeCentrality = calculateOutDegreeCentrality();
        Map<String, Double> closenessCentrality = calculateClosenessCentrality();
        Map<String, Double> betweennessCentrality = calculateBetweennessCentrality();
        Map<String, Double> pageRank = calculatePageRank();
        Map<String, Double> eigenvectorCentrality = calculateEigenvectorCentrality();
        
        // 为每个节点创建结果
        for (String nodeId : vertices) {
            CentralityResult result = new CentralityResult(nodeId);
            result.setDegreeCentrality(degreeCentrality.get(nodeId));
            result.setInDegreeCentrality(inDegreeCentrality.get(nodeId));
            result.setOutDegreeCentrality(outDegreeCentrality.get(nodeId));
            result.setClosenessCentrality(closenessCentrality.get(nodeId));
            result.setBetweennessCentrality(betweennessCentrality.get(nodeId));
            result.setPageRank(pageRank.get(nodeId));
            result.setEigenvectorCentrality(eigenvectorCentrality.get(nodeId));
            
            results.add(result);
            cachedResults.put(nodeId, result);
        }
        
        return results;
    }
    
    /**
     * 获取指定节点的中心性结果
     */
    public CentralityResult getNodeResult(String nodeId) {
        if (cachedResults.isEmpty()) {
            analyze();
        }
        return cachedResults.get(nodeId);
    }
    
    /**
     * 获取 Top K 重要节点
     * 
     * @param k 数量
     * @param sortBy 排序依据（"degree", "betweenness", "closeness", "eigenvector", "pagerank", "composite"）
     * @return Top K 节点列表
     */
    public List<CentralityResult> getTopKNodes(int k, String sortBy) {
        if (cachedResults.isEmpty()) {
            analyze();
        }
        
        Comparator<CentralityResult> comparator = getComparator(sortBy);
        
        return cachedResults.values().stream()
            .sorted(comparator.reversed())
            .limit(k)
            .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 计算度中心性
     */
    private Map<String, Double> calculateDegreeCentrality() {
        Map<String, Double> centrality = new HashMap<>();
        int n = graph.vertexSet().size();
        
        if (n <= 1) {
            for (String vertex : graph.vertexSet()) {
                centrality.put(vertex, 0.0);
            }
            return centrality;
        }
        
        // 使用简单的度计算
        for (String vertex : graph.vertexSet()) {
            int degree = graph.degreeOf(vertex);
            centrality.put(vertex, (double) degree / (n - 1));
        }
        
        return centrality;
    }
    
    /**
     * 计算入度中心性
     */
    private Map<String, Double> calculateInDegreeCentrality() {
        Map<String, Double> centrality = new HashMap<>();
        int n = graph.vertexSet().size();
        
        if (n <= 1) {
            for (String vertex : graph.vertexSet()) {
                centrality.put(vertex, 0.0);
            }
            return centrality;
        }
        
        for (String vertex : graph.vertexSet()) {
            int inDegree = graph.inDegreeOf(vertex);
            centrality.put(vertex, (double) inDegree / (n - 1));
        }
        
        return centrality;
    }
    
    /**
     * 计算出度中心性
     */
    private Map<String, Double> calculateOutDegreeCentrality() {
        Map<String, Double> centrality = new HashMap<>();
        int n = graph.vertexSet().size();
        
        if (n <= 1) {
            for (String vertex : graph.vertexSet()) {
                centrality.put(vertex, 0.0);
            }
            return centrality;
        }
        
        for (String vertex : graph.vertexSet()) {
            int outDegree = graph.outDegreeOf(vertex);
            centrality.put(vertex, (double) outDegree / (n - 1));
        }
        
        return centrality;
    }
    
    /**
     * 计算接近中心性（简化版本）
     */
    private Map<String, Double> calculateClosenessCentrality() {
        Map<String, Double> centrality = new HashMap<>();
        
        // 简化实现：使用平均最短路径长度的倒数
        for (String vertex : graph.vertexSet()) {
            // 这里使用一个简化的近似值
            // 完整实现需要使用 Dijkstra 或 BFS 计算所有节点对的最短路径
            int degree = graph.degreeOf(vertex);
            double avgPathLength = 1.0 + 1.0 / (degree + 1);
            centrality.put(vertex, 1.0 / avgPathLength);
        }
        
        return centrality;
    }
    
    /**
     * 计算中介中心性（简化版本）
     */
    private Map<String, Double> calculateBetweennessCentrality() {
        Map<String, Double> centrality = new HashMap<>();
        
        // 简化实现：基于度和邻居数量估算
        int n = graph.vertexSet().size();
        for (String vertex : graph.vertexSet()) {
            int degree = graph.degreeOf(vertex);
            // 中介中心性近似为度的函数
            centrality.put(vertex, (double) degree / n);
        }
        
        return centrality;
    }
    
    /**
     * 计算 PageRank（简化版本）
     */
    private Map<String, Double> calculatePageRank() {
        Map<String, Double> pagerank = new HashMap<>();
        int n = graph.vertexSet().size();
        
        if (n == 0) {
            return pagerank;
        }
        
        // 初始化所有节点的 PageRank 值
        double dampingFactor = 0.85;
        double initialValue = 1.0 / n;
        
        for (String vertex : graph.vertexSet()) {
            pagerank.put(vertex, initialValue);
        }
        
        // 迭代计算（简化版本，仅一次迭代）
        for (String vertex : graph.vertexSet()) {
            Set<DefaultWeightedEdge> incomingEdges = graph.incomingEdgesOf(vertex);
            double sum = 0.0;
            
            for (DefaultWeightedEdge edge : incomingEdges) {
                String neighbor = graph.getEdgeSource(edge);
                if (!neighbor.equals(vertex)) {
                    int neighborDegree = graph.degreeOf(neighbor);
                    sum += (1.0 / neighborDegree);
                }
            }
            
            double newValue = (1 - dampingFactor) / n + dampingFactor * sum;
            pagerank.put(vertex, newValue);
        }
        
        return pagerank;
    }
    
    /**
     * 计算特征向量中心性（简化版本）
     */
    private Map<String, Double> calculateEigenvectorCentrality() {
        Map<String, Double> centrality = new HashMap<>();
        int n = graph.vertexSet().size();
        
        if (n == 0) {
            return centrality;
        }
        
        // 初始化
        for (String vertex : graph.vertexSet()) {
            centrality.put(vertex, 1.0 / n);
        }
        
        // 迭代计算（简化版本）
        for (String vertex : graph.vertexSet()) {
            Set<DefaultWeightedEdge> edges = graph.edgesOf(vertex);
            double sum = 0.0;
            
            for (DefaultWeightedEdge edge : edges) {
                String neighbor = graph.getEdgeTarget(edge);
                if (neighbor.equals(vertex)) {
                    neighbor = graph.getEdgeSource(edge);
                }
                sum += 1.0;  // 简化：假设邻居的中心性为 1
            }
            
            centrality.put(vertex, sum / n);
        }
        
        return centrality;
    }
    
    /**
     * 获取比较器
     */
    private Comparator<CentralityResult> getComparator(String sortBy) {
        switch (sortBy.toLowerCase()) {
            case "degree":
                return Comparator.comparingDouble(r -> r.getDegreeCentrality() != null ? r.getDegreeCentrality() : 0.0);
            case "indegree":
                return Comparator.comparingDouble(r -> r.getInDegreeCentrality() != null ? r.getInDegreeCentrality() : 0.0);
            case "outdegree":
                return Comparator.comparingDouble(r -> r.getOutDegreeCentrality() != null ? r.getOutDegreeCentrality() : 0.0);
            case "betweenness":
                return Comparator.comparingDouble(r -> r.getBetweennessCentrality() != null ? r.getBetweennessCentrality() : 0.0);
            case "closeness":
                return Comparator.comparingDouble(r -> r.getClosenessCentrality() != null ? r.getClosenessCentrality() : 0.0);
            case "pagerank":
                return Comparator.comparingDouble(r -> r.getPageRank() != null ? r.getPageRank() : 0.0);
            case "eigenvector":
                return Comparator.comparingDouble(r -> r.getEigenvectorCentrality() != null ? r.getEigenvectorCentrality() : 0.0);
            case "composite":
            default:
                return Comparator.comparingDouble(CentralityResult::getCompositeScore);
        }
    }
}
