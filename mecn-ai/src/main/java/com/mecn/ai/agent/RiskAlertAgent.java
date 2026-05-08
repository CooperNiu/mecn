package com.mecn.ai.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Risk Alert Agent
 * 
 * Monitors and identifies systemic risks including:
 * - Economic bubble detection
 * - Financial stress assessment
 * - Market volatility analysis
 * - Crisis early warning signals
 */
@Component
public class RiskAlertAgent implements Agent {

    private static final Logger log = LoggerFactory.getLogger(RiskAlertAgent.class);

    @Override
    public String getName() {
        return "Risk Alert";
    }

    @Override
    public String getDescription() {
        return "Monitors systemic risks and provides early warning signals for economic and financial threats.";
    }

    @Override
    public AgentType getType() {
        return AgentType.RISK_ALERT;
    }

    @Override
    public AgentResponse analyze(Map<String, Object> data) {
        log.info("Risk Alert Agent analyzing data...");
        
        StringBuilder analysis = new StringBuilder();
        Map<String, Object> metadata = new HashMap<>();
        double confidence = 0.80;
        int riskLevel = 0; // 0: Low, 1: Medium, 2: High, 3: Critical

        // Check for economic overheating
        if (data.containsKey("gdp") && data.containsKey("cpi")) {
            double gdp = ((Number) data.get("gdp")).doubleValue();
            double cpi = ((Number) data.get("cpi")).doubleValue();
            
            if (gdp > 5 && cpi > 4) {
                analysis.append("⚠️ 经济过热风险：GDP和CPI双双高位运行，可能存在通胀失控风险。");
                riskLevel = Math.max(riskLevel, 3);
            } else if (gdp > 3 && cpi > 3) {
                analysis.append("⚡ 温和通胀风险：经济增长伴随通胀上升，需关注物价压力。");
                riskLevel = Math.max(riskLevel, 1);
            }
        }

        // Check for recession risk
        if (data.containsKey("gdp")) {
            double gdp = ((Number) data.get("gdp")).doubleValue();
            if (gdp < -1) {
                analysis.append("🚨 衰退风险：GDP负增长超过1%，经济可能进入衰退。");
                riskLevel = Math.max(riskLevel, 3);
            } else if (gdp < 0) {
                analysis.append("⚠️ 增长放缓：GDP出现负增长，需关注经济下行风险。");
                riskLevel = Math.max(riskLevel, 2);
            }
        }

        // Check for deflation risk
        if (data.containsKey("cpi")) {
            double cpi = ((Number) data.get("cpi")).doubleValue();
            if (cpi < 0) {
                analysis.append("🚨 通缩风险：CPI为负值，存在通缩螺旋风险。");
                riskLevel = Math.max(riskLevel, 3);
            } else if (cpi < 1) {
                analysis.append("⚠️ 低通胀风险：CPI低于1%，需关注通缩压力。");
                riskLevel = Math.max(riskLevel, 1);
            }
        }

        // Check PMI for manufacturing stress
        if (data.containsKey("pmi")) {
            double pmi = ((Number) data.get("pmi")).doubleValue();
            if (pmi < 45) {
                analysis.append("🚨 制造业严重收缩：PMI低于45，制造业面临严重压力。");
                riskLevel = Math.max(riskLevel, 2);
            } else if (pmi < 50) {
                analysis.append("⚠️ 制造业收缩：PMI低于50，制造业处于收缩区间。");
                riskLevel = Math.max(riskLevel, 1);
            }
        }

        // Generate risk level summary
        String riskLevelStr;
        switch (riskLevel) {
            case 0: riskLevelStr = "低风险"; break;
            case 1: riskLevelStr = "中等风险"; break;
            case 2: riskLevelStr = "高风险"; break;
            case 3: riskLevelStr = "极高风险"; break;
            default: riskLevelStr = "未知";
        }
        
        if (analysis.length() == 0) {
            analysis.append("✅ 当前未发现显著系统性风险。");
        }

        metadata.put("riskLevel", riskLevel);
        metadata.put("riskLevelStr", riskLevelStr);
        metadata.put("agentType", getType().name());
        metadata.put("timestamp", System.currentTimeMillis());

        return new AgentResponse(
            getName(),
            analysis.toString(),
            confidence,
            metadata
        );
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}
