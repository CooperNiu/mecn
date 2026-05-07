package com.mecn.api;

import com.mecn.MECNTools;
import com.mecn.causal.CausalResult;
import com.mecn.data.generator.EnhancedDataGenerator;
import com.mecn.model.EconomicIndicator;
import com.mecn.model.NetworkGraph;
import com.mecn.model.CentralityResult;
import com.mecn.network.CentralityAnalyzer;
import com.mecn.network.RippleResult;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 网络分析 REST API 控制器
 */
@RestController
@RequestMapping("/api/network")
@CrossOrigin(origins = "${mecn.cors.origins:http://localhost:8080}")
public class NetworkController {

    private static final Logger log = LoggerFactory.getLogger(NetworkController.class);

    // 轻量级结果缓存（避免每次请求都重跑全链路）
    private final Map<String, CachedResult> networkCache = new ConcurrentHashMap<>();
    private static final int MAX_CACHE_SIZE = 20;
    private static final long CACHE_TTL_MS = 5 * 60_000; // 5 分钟

    private record CachedResult(CausalResult causalResult, NetworkGraph network, long timestamp) {
        boolean isValid() {
            return System.currentTimeMillis() - timestamp < CACHE_TTL_MS;
        }
    }

    private String buildCacheKey(int numPeriods, double edgeThreshold, double significanceLevel) {
        return numPeriods + "|" + edgeThreshold + "|" + significanceLevel;
    }

    private CachedResult getOrBuildNetwork(int numPeriods, double edgeThreshold, double significanceLevel) {
        String key = buildCacheKey(numPeriods, edgeThreshold, significanceLevel);

        // 清理过期缓存
        if (networkCache.size() > MAX_CACHE_SIZE) {
            networkCache.values().removeIf(v -> !v.isValid());
        }

        CachedResult cached = networkCache.get(key);
        if (cached != null && cached.isValid()) {
            return cached;
        }

        // 构建
        EnhancedDataGenerator generator = new EnhancedDataGenerator();
        double[][] data = generator.generateDataForSampleSize(numPeriods);
        int numIndicators = data[0].length;
        List<String> codes = generator.getSupportedIndicators().stream()
            .map(EconomicIndicator::getCode)
            .limit(numIndicators)
            .collect(java.util.stream.Collectors.toList());

        CausalResult causalResult = MECNTools.discoverCausalStructure(data, significanceLevel);
        NetworkGraph network = MECNTools.buildNetwork(causalResult, codes, edgeThreshold);

        CachedResult result = new CachedResult(causalResult, network, System.currentTimeMillis());
        networkCache.put(key, result);
        return result;
    }

    @PostMapping("/build")
    public ResponseEntity<Map<String, Object>> buildNetwork(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unused")
            String dataSource = (String) request.getOrDefault("dataSource", "simulated");
            int numPeriods = ((Number) request.getOrDefault("numPeriods", 150)).intValue();
            double edgeThreshold = ((Number) request.getOrDefault("edgeThreshold", 0.08)).doubleValue();
            double significanceLevel = ((Number) request.getOrDefault("significanceLevel", 0.05)).doubleValue();

            var cached = getOrBuildNetwork(numPeriods, edgeThreshold, significanceLevel);
            NetworkGraph network = cached.network();

            Map<String, Object> vizData = convertToVizData(network);

            Map<String, Object> stats = new HashMap<>();
            int nodeCount = (int) vizData.get("nodes");
            int edgeCount = ((List<?>) vizData.get("links")).size();
            stats.put("nodeCount", nodeCount);
            stats.put("edgeCount", edgeCount);
            stats.put("density", calculateDensity(nodeCount, edgeCount));
            stats.put("avgDegree", calculateAvgDegree(nodeCount, edgeCount));

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", vizData);
            response.put("statistics", stats);
            response.put("numNodes", nodeCount);
            response.put("numEdges", edgeCount);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/ripple")
    public ResponseEntity<Map<String, Object>> simulateRipple(@RequestBody Map<String, Object> request) {
        try {
            String shockNode = (String) request.get("shockNode");
            double magnitude = ((Number) request.getOrDefault("magnitude", -0.1)).doubleValue();
            int timeSteps = ((Number) request.getOrDefault("timeSteps", 20)).intValue();
            double decayFactor = ((Number) request.getOrDefault("decayFactor", 0.9)).doubleValue();

            if (shockNode == null || shockNode.isEmpty()) {
                throw new IllegalArgumentException("shockNode 不能为空");
            }

            var cached = getOrBuildNetwork(100, 0.08, 0.05);
            NetworkGraph network = cached.network();

            if (!network.getNodes().contains(shockNode)) {
                throw new IllegalArgumentException("冲击节点 " + shockNode + " 不在网络中。可用节点: " + network.getNodes());
            }

            RippleResult result = MECNTools.simulateShock(network, shockNode, magnitude);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("affectedNodes", getAffectedNodes(result));
            response.put("totalImpact", calculateTotalImpact(result));
            response.put("peakTime", getPeakTime(result));
            response.put("timeSeries", convertTimeSeries(result));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/systemic-importance")
    public ResponseEntity<Map<String, Object>> getSystemicImportance() {
        try {
            var cached = getOrBuildNetwork(100, 0.08, 0.05);
            NetworkGraph network = cached.network();

            // 使用真实 CentralityAnalyzer 计算，替换之前的 Math.random() 占位符
            CentralityAnalyzer analyzer = new CentralityAnalyzer(network.getGraph());
            List<CentralityResult> centralityResults = analyzer.analyze();

            List<Map<String, Object>> nodes = new ArrayList<>();
            int rank = 1;
            for (CentralityResult result : centralityResults) {
                Map<String, Object> node = new HashMap<>();
                node.put("id", result.getNodeId());
                node.put("name", result.getNodeId());
                node.put("degreeCentrality", result.getDegreeCentrality());
                node.put("betweennessCentrality", result.getBetweennessCentrality());
                node.put("closenessCentrality", result.getClosenessCentrality());
                node.put("eigenvectorCentrality", result.getEigenvectorCentrality());
                node.put("pageRank", result.getPageRank());
                node.put("compositeScore", result.getCompositeScore());
                node.put("rank", rank++);
                nodes.add(node);
            }

            // 按综合得分排序
            nodes.sort((a, b) -> Double.compare(
                ((Number) b.get("compositeScore")).doubleValue(),
                ((Number) a.get("compositeScore")).doubleValue()));

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("nodes", nodes);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Systemic importance analysis failed", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    private Map<String, Object> convertToVizData(NetworkGraph network) {
        Map<String, Object> data = new HashMap<>();
        List<Map<String, Object>> nodes = new ArrayList<>();
        Map<String, String> categories = getCategoryMapping();
        Graph<String, DefaultWeightedEdge> graph = network.getGraph();
        
        // 节点大小基于度中心性（真实值），替换 Math.random() 占位符
        int maxDegree = graph.vertexSet().stream()
            .mapToInt(v -> graph.degreeOf(v)).max().orElse(1);
        for (String nodeId : graph.vertexSet()) {
            int degree = graph.degreeOf(nodeId);
            Map<String, Object> node = new HashMap<>();
            node.put("id", nodeId);
            node.put("name", nodeId);
            node.put("category", categories.getOrDefault(nodeId, "other"));
            node.put("size", 8 + 16.0 * degree / Math.max(1, maxDegree));
            nodes.add(node);
        }

        List<Map<String, Object>> links = new ArrayList<>();
        for (DefaultWeightedEdge edge : graph.edgeSet()) {
            String source = graph.getEdgeSource(edge);
            String target = graph.getEdgeTarget(edge);
            double weight = graph.getEdgeWeight(edge);

            Map<String, Object> link = new HashMap<>();
            link.put("source", source);
            link.put("target", target);
            link.put("weight", weight);
            links.add(link);
        }

        data.put("nodes", nodes.size());
        data.put("nodeList", nodes);
        data.put("links", links);

        return data;
    }

    private Map<String, String> getCategoryMapping() {
        Map<String, String> categories = new HashMap<>();
        categories.put("CRUDE", "commodity");
        categories.put("GOLD", "commodity");
        categories.put("COPPER", "commodity");
        categories.put("CMD_0", "commodity");
        categories.put("GDP", "macro");
        categories.put("CPI", "macro");
        categories.put("PPI", "macro");
        categories.put("M2", "macro");
        categories.put("ECO_0", "macro");
        categories.put("ECO_1", "macro");
        categories.put("SP500", "financial");
        categories.put("BOND", "financial");
        categories.put("EXRATE", "financial");
        categories.put("FIN_0", "financial");
        categories.put("UNEMP", "employment");
        categories.put("PAYROLL", "employment");
        categories.put("EMP_0", "employment");
        return categories;
    }

    private double calculateDensity(int nodes, int edges) {
        if (nodes <= 1) return 0.0;
        int maxEdges = nodes * (nodes - 1);
        return (double) edges / maxEdges;
    }

    private double calculateAvgDegree(int nodes, int edges) {
        if (nodes == 0) return 0.0;
        return (2.0 * edges) / nodes;
    }

    private List<Map<String, Object>> convertTimeSeries(RippleResult result) {
        List<Map<String, Object>> series = new ArrayList<>();
        if (!result.getNodeResponses().isEmpty()) {
            String firstNode = result.getNodeResponses().keySet().iterator().next();
            double[] response = result.getNodeResponse(firstNode);
            for (int t = 0; t < response.length; t++) {
                Map<String, Object> point = new HashMap<>();
                point.put("time", t);
                point.put("impact", response[t]);
                series.add(point);
            }
        }
        return series;
    }
    
    private List<String> getAffectedNodes(RippleResult result) {
        return new ArrayList<>(result.getNodeResponses().keySet());
    }
    
    private double calculateTotalImpact(RippleResult result) {
        double total = 0.0;
        for (double[] response : result.getNodeResponses().values()) {
            for (double val : response) {
                total += Math.abs(val);
            }
        }
        return total;
    }
    
    private int getPeakTime(RippleResult result) {
        if (result.getTotalImpactPerStep() == null || result.getTotalImpactPerStep().isEmpty()) {
            return 0;
        }
        
        int peakTime = 0;
        double maxImpact = 0.0;
        
        for (int t = 0; t < result.getTotalImpactPerStep().size(); t++) {
            double impact = Math.abs(result.getTotalImpactPerStep().get(t));
            if (impact > maxImpact) {
                maxImpact = impact;
                peakTime = t;
            }
        }
        
        return peakTime;
    }
}
