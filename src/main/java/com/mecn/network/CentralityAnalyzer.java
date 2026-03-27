package com.mecn.network;

import com.mecn.model.CentralityResult;
import org.jgrapht.Graph;
import org.jgrapht.alg.scoring.*;
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
        
        org.jgrapht.alg.scoring.DegreeCentrality<String, DefaultWeightedEdge> measure = 
            new org.jgrapht.alg.scoring.DegreeCentrality<>(graph);
        for (String vertex : graph.vertexSet()) {
            double score = measure.getScore(vertex);
            // 归一化到 [0, 1]
            centrality.put(vertex, score / (n - 1));
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
        
        org.jgrapht.alg.scoring.InDegreeCentrality<String, DefaultWeightedEdge> measure = 
            new org.jgrapht.alg.scoring.InDegreeCentrality<>(graph);
        for (String vertex : graph.vertexSet()) {
            double score = measure.getScore(vertex);
            centrality.put(vertex, score / (n - 1));
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
        
        org.jgrapht.alg.scoring.OutDegreeCentrality<String, DefaultWeightedEdge> measure = 
            new org.jgrapht.alg.scoring.OutDegreeCentrality<>(graph);
        for (String vertex : graph.vertexSet()) {
            double score = measure.getScore(vertex);
            centrality.put(vertex, score / (n - 1));
        }
        
        return centrality;
    }
    
    /**
     * 计算接近中心性
     */
    private Map<String, Double> calculateClosenessCentrality() {
        Map<String, Double> centrality = new HashMap<>();
        
        org.jgrapht.alg.scoring.ClosenessCentrality<String, DefaultWeightedEdge> measure = 
            new org.jgrapht.alg.scoring.ClosenessCentrality<>(graph);
        for (String vertex : graph.vertexSet()) {
            centrality.put(vertex, measure.getScore(vertex));
        }
        
        return centrality;
    }
    
    /**
     * 计算中介中心性
     */
    private Map<String, Double> calculateBetweennessCentrality() {
        Map<String, Double> centrality = new HashMap<>();
        
        org.jgrapht.alg.scoring.BetweennessCentrality<String, DefaultWeightedEdge> measure = 
            new org.jgrapht.alg.scoring.BetweennessCentrality<>(graph);
        for (String vertex : graph.vertexSet()) {
            centrality.put(vertex, measure.getScore(vertex));
        }
        
        return centrality;
    }
    
    /**
     * 计算 PageRank
     */
    private Map<String, Double> calculatePageRank() {
        Map<String, Double> pagerank = new HashMap<>();
        
        org.jgrapht.alg.scoring.PageRank<String, DefaultWeightedEdge> measure = 
            new org.jgrapht.alg.scoring.PageRank<>(graph);
        for (String vertex : graph.vertexSet()) {
            pagerank.put(vertex, measure.getScore(vertex));
        }
        
        return pagerank;
    }
    
    /**
     * 计算特征向量中心性
     */
    private Map<String, Double> calculateEigenvectorCentrality() {
        Map<String, Double> centrality = new HashMap<>();
        
        org.jgrapht.alg.scoring.EigenvectorCentrality<String, DefaultWeightedEdge> measure = 
            new org.jgrapht.alg.scoring.EigenvectorCentrality<>(graph);
        for (String vertex : graph.vertexSet()) {
            centrality.put(vertex, measure.getScore(vertex));
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
