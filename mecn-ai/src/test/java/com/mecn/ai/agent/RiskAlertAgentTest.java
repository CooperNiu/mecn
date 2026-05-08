package com.mecn.ai.agent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RiskAlertAgent 单元测试
 */
class RiskAlertAgentTest {

    private RiskAlertAgent agent;

    @BeforeEach
    void setUp() {
        agent = new RiskAlertAgent();
    }

    @Test
    void testGetName() {
        assertEquals("Risk Alert", agent.getName());
    }

    @Test
    void testGetDescription() {
        assertNotNull(agent.getDescription());
        assertTrue(agent.getDescription().contains("systemic risks"));
    }

    @Test
    void testGetType() {
        assertEquals(Agent.AgentType.RISK_ALERT, agent.getType());
    }

    @Test
    void testIsAvailable() {
        assertTrue(agent.isAvailable());
    }

    @Test
    void testAnalyzeNoRisk() {
        Map<String, Object> data = new HashMap<>();
        data.put("gdp", 2.5);
        data.put("cpi", 2.0);
        data.put("pmi", 51.0);
        
        Agent.AgentResponse response = agent.analyze(data);
        
        assertNotNull(response);
        assertEquals("Risk Alert", response.agentName());
        assertTrue(response.analysis().contains("未发现显著系统性风险"));
        assertEquals(0, response.metadata().get("riskLevel"));
    }

    @Test
    void testAnalyzeHighInflationRisk() {
        Map<String, Object> data = new HashMap<>();
        data.put("gdp", 6.0);
        data.put("cpi", 5.0);
        
        Agent.AgentResponse response = agent.analyze(data);
        
        assertTrue(response.analysis().contains("经济过热风险"));
        assertEquals(3, response.metadata().get("riskLevel"));
    }

    @Test
    void testAnalyzeModerateInflationRisk() {
        Map<String, Object> data = new HashMap<>();
        data.put("gdp", 3.5);
        data.put("cpi", 3.5);
        
        Agent.AgentResponse response = agent.analyze(data);
        
        assertTrue(response.analysis().contains("温和通胀风险"));
        assertEquals(1, response.metadata().get("riskLevel"));
    }

    @Test
    void testAnalyzeRecessionRisk() {
        Map<String, Object> data = new HashMap<>();
        data.put("gdp", -2.0);
        
        Agent.AgentResponse response = agent.analyze(data);
        
        assertTrue(response.analysis().contains("衰退风险"));
        assertEquals(3, response.metadata().get("riskLevel"));
    }

    @Test
    void testAnalyzeSlowdownRisk() {
        Map<String, Object> data = new HashMap<>();
        data.put("gdp", -0.5);
        
        Agent.AgentResponse response = agent.analyze(data);
        
        assertTrue(response.analysis().contains("增长放缓"));
        assertEquals(2, response.metadata().get("riskLevel"));
    }

    @Test
    void testAnalyzeDeflationRisk() {
        Map<String, Object> data = new HashMap<>();
        data.put("cpi", -1.0);
        
        Agent.AgentResponse response = agent.analyze(data);
        
        assertTrue(response.analysis().contains("通缩风险"));
        assertEquals(3, response.metadata().get("riskLevel"));
    }

    @Test
    void testAnalyzeLowInflationRisk() {
        Map<String, Object> data = new HashMap<>();
        data.put("cpi", 0.5);
        
        Agent.AgentResponse response = agent.analyze(data);
        
        assertTrue(response.analysis().contains("低通胀风险"));
        assertEquals(1, response.metadata().get("riskLevel"));
    }

    @Test
    void testAnalyzeManufacturingSevereContraction() {
        Map<String, Object> data = new HashMap<>();
        data.put("pmi", 43.0);
        
        Agent.AgentResponse response = agent.analyze(data);
        
        assertTrue(response.analysis().contains("制造业严重收缩"));
        assertEquals(2, response.metadata().get("riskLevel"));
    }

    @Test
    void testAnalyzeManufacturingContraction() {
        Map<String, Object> data = new HashMap<>();
        data.put("pmi", 48.0);
        
        Agent.AgentResponse response = agent.analyze(data);
        
        assertTrue(response.analysis().contains("制造业收缩"));
        assertEquals(1, response.metadata().get("riskLevel"));
    }

    @Test
    void testAnalyzeMetadata() {
        Map<String, Object> data = new HashMap<>();
        data.put("gdp", 2.5);
        
        Agent.AgentResponse response = agent.analyze(data);
        
        assertNotNull(response.metadata());
        assertTrue(response.metadata().containsKey("riskLevel"));
        assertTrue(response.metadata().containsKey("riskLevelStr"));
        assertTrue(response.metadata().containsKey("agentType"));
        assertTrue(response.metadata().containsKey("timestamp"));
    }

    @Test
    void testRiskLevelString() {
        Map<String, Object> data = new HashMap<>();
        data.put("gdp", 2.5);
        data.put("cpi", 2.0);
        
        Agent.AgentResponse response = agent.analyze(data);
        
        assertEquals("低风险", response.metadata().get("riskLevelStr"));
    }
}
