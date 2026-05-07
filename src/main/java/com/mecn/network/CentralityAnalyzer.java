package com.mecn.network;

import com.mecn.model.CentralityResult;
import org.jgrapht.Graph;
import org.jgrapht.alg.scoring.*;
import org.jgrapht.alg.shortestpath.GraphMeasurer;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 中心性分析器
 * 
 * 使用 JGraphT 原生算法计算网络中节点的各种中心性指标
 * 替换了之前的简化占位符实现
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
        
        // 使用 JGraphT 原生算法计算各种中心性
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
     * @param sortBy 排序依据
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
            .collect(Collectors.toList());
    }
    
    /**
     * 计算度中心性（归一化）
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
        
        for (String vertex : graph.vertexSet()) {
            int degree = graph.degreeOf(vertex);
            centrality.put(vertex, (double) degree / (n - 1));
        }
        
        return centrality;
    }
    
    /**
     * 计算入度中心性（归一化）
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
     * 计算出度中心性（归一化）
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
     * 使用 JGraphT 的 HarmonicCentrality 计算接近中心性。
     * 
     * Harmonic Centrality 是 Closeness Centrality 的变体，能很好地处理有向图
     * 中不可达节点的情况（不连通分量中的节点不会导致值为 0）。
     */
    private Map<String, Double> calculateClosenessCentrality() {
        Map<String, Double> centrality = new HashMap<>();
        int n = graph.vertexSet().size();
        
        if (n <= 1) {
            for (String vertex : graph.vertexSet()) {
                centrality.put(vertex, 0.0);
            }
            return centrality;
        }
        
        try {
            // 使用 JGraphT 的 HarmonicCentrality（适用于有向加权图）
            HarmonicCentrality<String, DefaultWeightedEdge> algo = 
                new HarmonicCentrality<>(graph);
            centrality = algo.getScores();
        } catch (Exception e) {
            // 回退：使用度作为近似
            for (String vertex : graph.vertexSet()) {
                int degree = graph.degreeOf(vertex);
                centrality.put(vertex, (double) degree / (n - 1));
            }
        }
        
        return centrality;
    }
    
    /**
     * 使用 JGraphT 的 BetweennessCentrality 计算中介中心性。
     * 对于有向加权图，使用 Brandes 算法。
     */
    private Map<String, Double> calculateBetweennessCentrality() {
        Map<String, Double> centrality = new HashMap<>();
        int n = graph.vertexSet().size();
        
        if (n <= 2) {
            for (String vertex : graph.vertexSet()) {
                centrality.put(vertex, 0.0);
            }
            return centrality;
        }
        
        try {
            // JGraphT BetweennessCentrality 支持有向图
            BetweennessCentrality<String, DefaultWeightedEdge> algo = 
                new BetweennessCentrality<>(graph);
            centrality = algo.getScores();
        } catch (Exception e) {
            // 回退方案
            for (String vertex : graph.vertexSet()) {
                centrality.put(vertex, 0.0);
            }
        }
        
        return centrality;
    }
    
    /**
     * 使用 JGraphT 的 PageRank 算法计算 PageRank 值。
     * 默认阻尼系数 0.85，最大迭代次数 100。
     */
    private Map<String, Double> calculatePageRank() {
        Map<String, Double> centrality = new HashMap<>();
        int n = graph.vertexSet().size();
        
        if (n == 0) {
            return centrality;
        }
        
        try {
            PageRank<String, DefaultWeightedEdge> algo = 
                new PageRank<>(graph, 0.85, 100, 1e-4);
            centrality = algo.getScores();
        } catch (Exception e) {
            // 回退：均匀分布
            double uniform = 1.0 / n;
            for (String vertex : graph.vertexSet()) {
                centrality.put(vertex, uniform);
            }
        }
        
        return centrality;
    }
    
    /**
     * 使用 JGraphT 的 EigenvectorCentrality 计算特征向量中心性。
     * 默认最大迭代次数 100，收敛容差 1e-4。
     */
    private Map<String, Double> calculateEigenvectorCentrality() {
        Map<String, Double> centrality = new HashMap<>();
        int n = graph.vertexSet().size();
        
        if (n == 0) {
            return centrality;
        }
        
        try {
            // JGraphT 1.5.x EigenvectorCentrality 通过构造参数控制迭代
            EigenvectorCentrality<String, DefaultWeightedEdge> algo = 
                new EigenvectorCentrality<>(graph, 200, 1e-6);
            centrality = algo.getScores();
        } catch (Exception e) {
            // 特征向量中心性可能不收敛，回退到度中心性
            for (String vertex : graph.vertexSet()) {
                centrality.put(vertex, (double) graph.degreeOf(vertex) / Math.max(1, n - 1));
            }
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
