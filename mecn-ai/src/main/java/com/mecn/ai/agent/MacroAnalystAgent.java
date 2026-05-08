package com.mecn.ai.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Macro Economic Analyst Agent
 * 
 * Provides intelligent analysis of macro-economic trends including:
 * - GDP growth analysis
 * - Inflation trend assessment
 * - Monetary policy evaluation
 * - Economic cycle identification
 */
@Component
public class MacroAnalystAgent implements Agent {

    private static final Logger log = LoggerFactory.getLogger(MacroAnalystAgent.class);

    @Override
    public String getName() {
        return "Macro Analyst";
    }

    @Override
    public String getDescription() {
        return "Analyzes macro-economic trends and provides insights on GDP, inflation, monetary policy, and economic cycles.";
    }

    @Override
    public AgentType getType() {
        return AgentType.MACRO_ANALYST;
    }

    @Override
    public AgentResponse analyze(Map<String, Object> data) {
        log.info("Macro Analyst Agent analyzing data...");
        
        StringBuilder analysis = new StringBuilder();
        Map<String, Object> metadata = new HashMap<>();
        double confidence = 0.85;

        // Analyze GDP trend
        if (data.containsKey("gdp")) {
            double gdp = ((Number) data.get("gdp")).doubleValue();
            if (gdp > 3.0) {
                analysis.append("经济增长强劲，GDP增速超过3%。");
            } else if (gdp > 0) {
                analysis.append("经济温和增长，GDP增速为正但低于3%。");
            } else {
                analysis.append("经济出现负增长，需要关注衰退风险。");
            }
            metadata.put("gdpAnalysis", "completed");
        }

        // Analyze CPI trend
        if (data.containsKey("cpi")) {
            double cpi = ((Number) data.get("cpi")).doubleValue();
            if (cpi > 3.0) {
                analysis.append("通胀压力较大，CPI超过3%警戒线。");
            } else if (cpi > 2.0) {
                analysis.append("通胀温和，处于2-3%区间。");
            } else {
                analysis.append("通胀较低，存在通缩风险。");
            }
            metadata.put("cpiAnalysis", "completed");
        }

        // Analyze PMI
        if (data.containsKey("pmi")) {
            double pmi = ((Number) data.get("pmi")).doubleValue();
            if (pmi > 50) {
                analysis.append("制造业处于扩张区间，经济景气。");
            } else {
                analysis.append("制造业处于收缩区间，经济承压。");
            }
            metadata.put("pmiAnalysis", "completed");
        }

        // Economic cycle assessment
        analysis.append("综合判断：当前经济周期处于");
        if (data.containsKey("gdp") && data.containsKey("cpi")) {
            double gdp = ((Number) data.get("gdp")).doubleValue();
            double cpi = ((Number) data.get("cpi")).doubleValue();
            if (gdp > 2 && cpi < 3) {
                analysis.append("复苏/扩张阶段。");
            } else if (gdp < 0) {
                analysis.append("衰退阶段。");
            } else {
                analysis.append("调整阶段。");
            }
        }

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
