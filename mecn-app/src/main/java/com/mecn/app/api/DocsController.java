package com.mecn.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * API 文档控制器
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class DocsController {

    @GetMapping("/docs")
    public ResponseEntity<Map<String, Object>> getApiDocs() {
        Map<String, Object> docs = new HashMap<>();
        
        docs.put("title", "MECN REST API");
        docs.put("version", "1.0.0");
        docs.put("description", "高维宏观经济因果网络联动模型 API");
        
        Map<String, Object> endpoints = new HashMap<>();
        
        Map<String, Object> buildEndpoint = new HashMap<>();
        buildEndpoint.put("method", "POST");
        buildEndpoint.put("path", "/api/network/build");
        buildEndpoint.put("description", "构建因果网络");
        Map<String, Object> buildParams = new HashMap<>();
        buildParams.put("dataSource", "数据源 (simulated/fred/worldbank)");
        buildParams.put("numPeriods", "历史期数（默认 150）");
        buildParams.put("edgeThreshold", "边阈值（默认 0.08）");
        buildParams.put("significanceLevel", "显著性水平（默认 0.05）");
        buildEndpoint.put("parameters", buildParams);
        endpoints.put("build", buildEndpoint);
        
        Map<String, Object> rippleEndpoint = new HashMap<>();
        rippleEndpoint.put("method", "POST");
        rippleEndpoint.put("path", "/api/network/ripple");
        rippleEndpoint.put("description", "执行涟漪效应模拟");
        Map<String, Object> rippleParams = new HashMap<>();
        rippleParams.put("shockNode", "冲击节点 ID（必需）");
        rippleParams.put("magnitude", "冲击幅度（默认 -0.1）");
        rippleParams.put("timeSteps", "时间步数（默认 20）");
        rippleParams.put("decayFactor", "衰减因子（默认 0.9）");
        rippleEndpoint.put("parameters", rippleParams);
        endpoints.put("ripple", rippleEndpoint);
        
        Map<String, Object> systemicEndpoint = new HashMap<>();
        systemicEndpoint.put("method", "GET");
        systemicEndpoint.put("path", "/api/network/systemic-importance");
        systemicEndpoint.put("description", "获取系统重要性节点排名");
        endpoints.put("systemic-importance", systemicEndpoint);
        
        docs.put("endpoints", endpoints);
        
        Map<String, Object> responseExample = new HashMap<>();
        responseExample.put("success", true);
        responseExample.put("data", "{ nodes, links }");
        responseExample.put("statistics", "{ nodeCount, edgeCount, density, avgDegree }");
        docs.put("responseFormat", responseExample);
        
        return ResponseEntity.ok(docs);
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "MECN API");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.ok(response);
    }
}
