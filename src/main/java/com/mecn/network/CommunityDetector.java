package com.mecn.network;

import org.jgrapht.Graph;
import org.jgrapht.alg.community.*;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.*;

/**
 * 社区检测器
 * 
 * 使用 Louvain 算法进行社区发现
 * 
 * Louvain 算法是一种基于模块度优化的层次聚类算法，能够高效地识别网络中的社区结构。
 * 
 * @example
 * {@code
 * CommunityDetector detector = new CommunityDetector(network.getGraph());
 * List<List<String>> communities = detector.detectCommunities();
 * 
 * System.out.println("检测到 " + communities.size() + " 个社区");
 * for (int i = 0; i < communities.size(); i++) {
 *     System.out.println("社区 " + (i + 1) + ": " + communities.get(i));
 * }
 * }
 */
public class CommunityDetector {
    
    private final Graph<String, DefaultWeightedEdge> graph;
    private List<List<String>> cachedCommunities;
    private double cachedModularity;
    
    public CommunityDetector(Graph<String, DefaultWeightedEdge> graph) {
        this.graph = graph;
    }
    
    /**
     * 检测社区结构（使用 Louvain 算法）
     * 
     * @return 社区列表，每个社区是一个节点 ID 列表
     */
    public List<List<String>> detectCommunities() {
        if (cachedCommunities != null) {
            return cachedCommunities;
        }
        
        // 使用 Louvain 社区检测算法
        org.jgrapht.alg.community.LouvainModularity<String, DefaultWeightedEdge> louvain = 
            new org.jgrapht.alg.community.LouvainModularity<>(graph);
        
        // 获取社区划分结果
        Map<String, Integer> nodeToCommunity = new HashMap<>();
        Set<String> vertices = graph.vertexSet();
        
        for (String vertex : vertices) {
            int communityId = louvain.getCommunity(vertex);
            nodeToCommunity.put(vertex, communityId);
        }
        
        // 将结果转换为列表形式
        Map<Integer, List<String>> communityMap = new HashMap<>();
        for (Map.Entry<String, Integer> entry : nodeToCommunity.entrySet()) {
            communityMap.computeIfAbsent(entry.getValue(), k -> new ArrayList<>())
                       .add(entry.getKey());
        }
        
        // 按社区大小排序
        List<List<String>> communities = new ArrayList<>(communityMap.values());
        communities.sort((a, b) -> Integer.compare(b.size(), a.size()));
        
        // 计算模块度
        cachedModularity = calculateModularity(communities);
        cachedCommunities = Collections.unmodifiableList(communities);
        
        return cachedCommunities;
    }
    
    /**
     * 获取节点所属的社区
     * 
     * @param nodeId 节点 ID
     * @return 社区索引（从 0 开始）
     */
    public int getNodeCommunity(String nodeId) {
        if (cachedCommunities == null) {
            detectCommunities();
        }
        
        for (int i = 0; i < cachedCommunities.size(); i++) {
            if (cachedCommunities.get(i).contains(nodeId)) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * 获取社区的节点列表
     * 
     * @param communityIndex 社区索引
     * @return 社区中的节点列表
     */
    public List<String> getCommunityNodes(int communityIndex) {
        if (cachedCommunities == null) {
            detectCommunities();
        }
        
        if (communityIndex >= 0 && communityIndex < cachedCommunities.size()) {
            return cachedCommunities.get(communityIndex);
        }
        return Collections.emptyList();
    }
    
    /**
     * 获取社区数量
     */
    public int getNumCommunities() {
        if (cachedCommunities == null) {
            detectCommunities();
        }
        return cachedCommunities.size();
    }
    
    /**
     * 获取模块度得分
     * 
     * 模块度是衡量社区划分质量的指标，范围 [-1, 1]，值越大表示社区结构越明显
     * 
     * @return 模块度得分
     */
    public double getModularity() {
        if (cachedCommunities == null) {
            detectCommunities();
        }
        return cachedModularity;
    }
    
    /**
     * 清除缓存结果
     */
    public void clearCache() {
        cachedCommunities = null;
        cachedModularity = 0.0;
    }
    
    /**
     * 计算模块度
     */
    private double calculateModularity(List<List<String>> communities) {
        int numEdges = graph.edgeSet().size();
        if (numEdges == 0) {
            return 0.0;
        }
        
        // 计算每个节点的度
        Map<String, Integer> degreeMap = new HashMap<>();
        for (String vertex : graph.vertexSet()) {
            degreeMap.put(vertex, graph.degreeOf(vertex));
        }
        
        // 计算模块度 Q = (1/2m) * Σ[Aij - ki*kj/(2m)] * δ(ci, cj)
        double Q = 0.0;
        
        for (List<String> community : communities) {
            Set<String> communitySet = new HashSet<>(community);
            
            for (String i : community) {
                for (String j : community) {
                    // 检查是否有边
                    DefaultWeightedEdge edge = graph.getEdge(i, j);
                    double Aij = (edge != null) ? 1.0 : 0.0;
                    
                    // 如果是无向图，还要检查反向边
                    if (edge == null && !graph.getType().isDirected()) {
                        edge = graph.getEdge(j, i);
                        Aij = (edge != null) ? 1.0 : 0.0;
                    }
                    
                    int ki = degreeMap.get(i);
                    int kj = degreeMap.get(j);
                    
                    double expectedProbability = (ki * kj) / (2.0 * numEdges);
                    Q += (Aij - expectedProbability);
                }
            }
        }
        
        Q /= (2.0 * numEdges);
        return Q;
    }
    
    /**
     * 获取社区统计信息
     */
    public Map<String, Object> getCommunityStatistics() {
        if (cachedCommunities == null) {
            detectCommunities();
        }
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("numCommunities", cachedCommunities.size());
        stats.put("modularity", cachedModularity);
        stats.put("totalNodes", graph.vertexSet().size());
        
        // 社区大小统计
        List<Integer> sizes = new ArrayList<>();
        for (List<String> community : cachedCommunities) {
            sizes.add(community.size());
        }
        
        if (!sizes.isEmpty()) {
            stats.put("avgCommunitySize", sizes.stream().mapToInt(Integer::intValue).average().orElse(0.0));
            stats.put("maxCommunitySize", Collections.max(sizes));
            stats.put("minCommunitySize", Collections.min(sizes));
        }
        
        return stats;
    }
}
