package com.mecn.network;

import com.mecn.model.NetworkGraph;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.util.*;

/**
 * 涟漪效应模拟器
 * 
 * 模拟某个指标波动如何通过网络传导，捕捉整个网络的连锁反应
 */
public class RippleSimulator {
    
    private double decayFactor = 0.9;  // 衰减因子
    private int defaultTimeSteps = 20;  // 默认时间步数
    
    public RippleSimulator() {
    }
    
    public RippleSimulator(double decayFactor, int timeSteps) {
        this.decayFactor = decayFactor;
        this.defaultTimeSteps = timeSteps;
    }
    
    /**
     * 模拟冲击传播
     * 
     * @param network 经济网络
     * @param shockNode 受冲击节点 ID
     * @param shockMagnitude 冲击幅度
     * @return 涟漪效应结果
     */
    public RippleResult simulate(NetworkGraph network, String shockNode, double shockMagnitude) {
        return simulate(network, shockNode, shockMagnitude, defaultTimeSteps);
    }
    
    /**
     * 模拟冲击传播（指定时间步数）
     */
    public RippleResult simulate(NetworkGraph network, String shockNode, 
                                  double shockMagnitude, int timeSteps) {
        
        Graph<String, DefaultWeightedEdge> graph = network.getGraph();
        Set<String> nodes = graph.vertexSet();
        
        // 初始化结果对象
        RippleResult result = new RippleResult(shockNode, shockMagnitude, timeSteps);
        
        // 为每个节点初始化响应数组
        Map<String, double[]> responses = new HashMap<>();
        for (String node : nodes) {
            responses.put(node, new double[timeSteps]);
        }
        
        // 初始冲击（t=0）
        if (nodes.contains(shockNode)) {
            responses.get(shockNode)[0] = shockMagnitude;
        }
        
        // 迭代传播
        List<Double> totalImpactPerStep = new ArrayList<>();
        
        for (int t = 1; t < timeSteps; t++) {
            double totalImpact = 0.0;
            
            // 计算每个节点在时间 t 的响应
            for (String targetNode : nodes) {
                double impact = 0.0;
                
                // 收集所有传入边的影响
                Set<DefaultWeightedEdge> incomingEdges = graph.incomingEdgesOf(targetNode);
                for (DefaultWeightedEdge edge : incomingEdges) {
                    String sourceNode = graph.getEdgeSource(edge);
                    double weight = graph.getEdgeWeight(edge);
                    double prevImpact = responses.get(sourceNode)[t - 1];
                    impact += weight * prevImpact;
                }
                
                // 应用衰减因子
                impact *= decayFactor;
                responses.get(targetNode)[t] = impact;
                totalImpact += Math.abs(impact);
            }
            
            totalImpactPerStep.add(totalImpact);
        }
        
        // 设置结果
        for (Map.Entry<String, double[]> entry : responses.entrySet()) {
            result.addNodeResponse(entry.getKey(), entry.getValue());
        }
        
        result.setTotalImpactPerStep(totalImpactPerStep);
        result.addMetadata("decayFactor", decayFactor);
        result.addMetadata("algorithm", "linear_propagation");
        
        return result;
    }
    
    /**
     * 识别风险传导路径
     */
    public List<RiskPath> findRiskPaths(NetworkGraph network, String source, 
                                         String target, double minImpact) {
        Graph<String, DefaultWeightedEdge> graph = network.getGraph();
        List<RiskPath> paths = new ArrayList<>();
        
        // 使用 BFS 查找所有路径
        Queue<List<String>> queue = new LinkedList<>();
        List<String> initialPath = new ArrayList<>();
        initialPath.add(source);
        queue.offer(initialPath);
        
        while (!queue.isEmpty()) {
            List<String> currentPath = queue.poll();
            String currentNode = currentPath.get(currentPath.size() - 1);
            
            if (currentNode.equals(target)) {
                RiskPath path = new RiskPath(source, target);
                double totalImpact = 1.0;
                
                for (String node : currentPath) {
                    path.addNode(node);
                }
                
                for (int i = 0; i < currentPath.size() - 1; i++) {
                    String from = currentPath.get(i);
                    String to = currentPath.get(i + 1);
                    DefaultWeightedEdge edge = graph.getEdge(from, to);
                    
                    if (edge != null) {
                        double weight = graph.getEdgeWeight(edge);
                        path.addEdgeWeight(weight);
                        totalImpact *= weight;
                    } else {
                        totalImpact = 0;
                        break;
                    }
                }
                
                if (totalImpact >= minImpact) {
                    path.setTotalImpact(totalImpact);
                    paths.add(path);
                }
                
                continue;
            }
            
            Set<String> visited = new HashSet<>(currentPath);
            Set<DefaultWeightedEdge> outgoingEdges = graph.outgoingEdgesOf(currentNode);
            for (DefaultWeightedEdge edge : outgoingEdges) {
                String neighbor = graph.getEdgeTarget(edge);
                if (!visited.contains(neighbor)) {
                    List<String> newPath = new ArrayList<>(currentPath);
                    newPath.add(neighbor);
                    if (newPath.size() <= 10) {
                        queue.offer(newPath);
                    }
                }
            }
        }
        
        paths.sort((a, b) -> Double.compare(b.getTotalImpact(), a.getTotalImpact()));
        return paths;
    }
    
    /**
     * 识别系统重要性节点
     */
    public List<SystemicImportance> identifySystemicallyImportantNodes(NetworkGraph network) {
        Graph<String, DefaultWeightedEdge> originalGraph = network.getGraph();
        List<SystemicImportance> importances = new ArrayList<>();
        
        double originalEfficiency = calculateNetworkEfficiency(originalGraph);
        
        for (String node : originalGraph.vertexSet()) {
            Graph<String, DefaultWeightedEdge> reducedGraph = 
                new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
            
            for (String n : originalGraph.vertexSet()) {
                if (!n.equals(node)) {
                    reducedGraph.addVertex(n);
                }
            }
            
            for (DefaultWeightedEdge edge : originalGraph.edgeSet()) {
                String source = originalGraph.getEdgeSource(edge);
                String target = originalGraph.getEdgeTarget(edge);
                
                if (!source.equals(node) && !target.equals(node)) {
                    reducedGraph.addEdge(source, target);
                    DefaultWeightedEdge newEdge = reducedGraph.getEdge(source, target);
                    if (newEdge != null) {
                        reducedGraph.setEdgeWeight(newEdge, originalGraph.getEdgeWeight(edge));
                    }
                }
            }
            
            double reducedEfficiency = calculateNetworkEfficiency(reducedGraph);
            double efficiencyLoss = originalEfficiency > 0 
                ? (originalEfficiency - reducedEfficiency) / originalEfficiency 
                : 0.0;
            
            SystemicImportance importance = new SystemicImportance(node);
            importance.setImportanceScore(efficiencyLoss);
            importance.setNetworkEfficiencyLoss(efficiencyLoss);
            importance.setAffectedNodesCount(reducedGraph.vertexSet().size());
            
            importances.add(importance);
        }
        
        importances.sort((a, b) -> Double.compare(b.getImportanceScore(), a.getImportanceScore()));
        return importances;
    }
    
    /**
     * 计算网络效率（简化版本）
     */
    private double calculateNetworkEfficiency(Graph<String, DefaultWeightedEdge> graph) {
        int numNodes = graph.vertexSet().size();
        if (numNodes == 0) return 0.0;
        
        int numEdges = graph.edgeSet().size();
        int maxEdges = numNodes * (numNodes - 1);
        
        double avgWeight = 0.0;
        if (!graph.edgeSet().isEmpty()) {
            for (DefaultWeightedEdge edge : graph.edgeSet()) {
                avgWeight += graph.getEdgeWeight(edge);
            }
            avgWeight /= graph.edgeSet().size();
        }
        
        double density = maxEdges > 0 ? (double) numEdges / maxEdges : 0.0;
        return density * avgWeight;
    }
    
    public double getDecayFactor() {
        return decayFactor;
    }
    
    public void setDecayFactor(double decayFactor) {
        this.decayFactor = decayFactor;
    }
    
    public int getDefaultTimeSteps() {
        return defaultTimeSteps;
    }
    
    public void setDefaultTimeSteps(int defaultTimeSteps) {
        this.defaultTimeSteps = defaultTimeSteps;
    }
}
