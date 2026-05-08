package com.mecn.ai.agent;

import java.util.Map;

/**
 * Base interface for all MECN AI Agents
 * 
 * Agents provide intelligent analysis capabilities for:
 * - Macro-economic trend analysis
 * - Risk assessment and early warning
 * - Policy impact simulation
 * - Network insight generation
 */
public interface Agent {
    
    /**
     * Get agent name
     * @return Agent name
     */
    String getName();
    
    /**
     * Get agent description
     * @return Agent description
     */
    String getDescription();
    
    /**
     * Get agent type
     * @return Agent type
     */
    AgentType getType();
    
    /**
     * Analyze economic data and provide insights
     * @param data Input data for analysis
     * @return Analysis results
     */
    AgentResponse analyze(Map<String, Object> data);
    
    /**
     * Check if agent is available
     * @return true if agent can process requests
     */
    boolean isAvailable();
    
    /**
     * Agent types enumeration
     */
    enum AgentType {
        MACRO_ANALYST,      // 宏观分析师
        RISK_ALERT,         // 风险预警
        POLICY_ADVISOR,     // 政策顾问
        NETWORK_INSIGHT,    // 网络洞察
        DATA_ENRICHMENT     // 数据增强
    }
    
    /**
     * Agent response record
     */
    record AgentResponse(
        String agentName,
        String analysis,
        double confidence,
        Map<String, Object> metadata
    ) {}
}
