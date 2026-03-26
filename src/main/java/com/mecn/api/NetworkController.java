package com.mecn.api;

import com.mecn.MECNTools;
import com.mecn.causal.CausalResult;
import com.mecn.data.generator.EnhancedDataGenerator;
import com.mecn.model.EconomicIndicator;
import com.mecn.model.NetworkGraph;
import com.mecn.network.RippleResult;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 网络分析 REST API 控制器
 */
@RestController
@RequestMapping("/api/network")
@CrossOrigin(origins = "*")
public class NetworkController {

    @PostMapping("/build")
    public ResponseEntity<Map<String, Object>> buildNetwork(@RequestBody Map<String, Object> request) {
        try {
            String dataSource = (String) request.getOrDefault("dataSource", "simulated");
            int numPeriods = ((Number) request.getOrDefault("numPeriods", 150)).intValue();
            double edgeThreshold = ((Number) request.getOrDefault("edgeThreshold", 0.08)).doubleValue();
            double significanceLevel = ((Number) request.getOrDefault("significanceLevel", 0.05)).doubleValue();

            EnhancedDataGenerator generator = new EnhancedDataGenerator();
            double[][] data = generator.generateDataForSampleSize(numPeriods);
            
            List<String> codes = Arrays.asList("ECO_0");

            CausalResult causalResult = MECNTools.discoverCausalStructure(data, significanceLevel);
            NetworkGraph network = MECNTools.buildNetwork(causalResult, codes, edgeThreshold);

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

            EnhancedDataGenerator generator = new EnhancedDataGenerator();
            double[][] data = generator.generateDataForSampleSize(100);
            List<String> codes = Arrays.asList("ECO_0");
            
            CausalResult causalResult = MECNTools.discoverCausalStructure(data, 0.05);
            NetworkGraph network = MECNTools.buildNetwork(causalResult, codes, 0.08);

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
            EnhancedDataGenerator generator = new EnhancedDataGenerator();
            double[][] data = generator.generateDataForSampleSize(100);
            List<String> codes = Arrays.asList("ECO_0");
            
            CausalResult causalResult = MECNTools.discoverCausalStructure(data, 0.05);
            NetworkGraph network = MECNTools.buildNetwork(causalResult, codes, 0.08);

            List<Map<String, Object>> nodes = new ArrayList<>();
            Graph<String, DefaultWeightedEdge> graph = network.getGraph();
            int rank = 1;
            
            for (String nodeId : graph.vertexSet()) {
                Map<String, Object> node = new HashMap<>();
                node.put("id", nodeId);
                node.put("name", nodeId);
                
                double degreeCentrality = (double) (graph.inDegreeOf(nodeId) + graph.outDegreeOf(nodeId)) / Math.max(1, graph.vertexSet().size() - 1);
                node.put("degreeCentrality", degreeCentrality);
                node.put("betweennessCentrality", Math.random());
                node.put("closenessCentrality", Math.random());
                node.put("eigenvectorCentrality", Math.random());
                node.put("compositeScore", degreeCentrality);
                node.put("rank", rank++);
                nodes.add(node);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("nodes", nodes);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
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
        
        for (String nodeId : graph.vertexSet()) {
            Map<String, Object> node = new HashMap<>();
            node.put("id", nodeId);
            node.put("name", nodeId);
            node.put("category", categories.getOrDefault(nodeId, "other"));
            node.put("size", 12 + Math.random() * 8);
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
