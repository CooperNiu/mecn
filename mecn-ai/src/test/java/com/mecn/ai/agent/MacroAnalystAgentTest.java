package com.mecn.ai.agent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MacroAnalystAgent 单元测试
 */
class MacroAnalystAgentTest {

    private MacroAnalystAgent agent;

    @BeforeEach
    void setUp() {
        agent = new MacroAnalystAgent();
    }

    @Test
    void testGetName() {
        assertEquals("Macro Analyst", agent.getName());
    }

    @Test
    void testGetDescription() {
        assertNotNull(agent.getDescription());
        assertTrue(agent.getDescription().contains("macro-economic"));
    }

    @Test
    void testGetType() {
        assertEquals(Agent.AgentType.MACRO_ANALYST, agent.getType());
    }

    @Test
    void testIsAvailable() {
        assertTrue(agent.isAvailable());
    }

    @Test
    void testAnalyzeWithGDP() {
        Map<String, Object> data = new HashMap<>();
        data.put("gdp", 3.5);
        
        Agent.AgentResponse response = agent.analyze(data);
        
        assertNotNull(response);
        assertEquals("Macro Analyst", response.agentName());
        assertTrue(response.analysis().contains("经济增长强劲"));
        assertTrue(response.analysis().contains("3%"));
        assertEquals(0.85, response.confidence(), 0.001);
    }

    @Test
    void testAnalyzeWithLowGDP() {
        Map<String, Object> data = new HashMap<>();
        data.put("gdp", 1.5);
        
        Agent.AgentResponse response = agent.analyze(data);
        
        assertTrue(response.analysis().contains("经济温和增长"));
    }

    @Test
    void testAnalyzeWithNegativeGDP() {
        Map<String, Object> data = new HashMap<>();
        data.put("gdp", -0.5);
        
        Agent.AgentResponse response = agent.analyze(data);
        
        assertTrue(response.analysis().contains("负增长"));
    }

    @Test
    void testAnalyzeWithCPI() {
        Map<String, Object> data = new HashMap<>();
        data.put("cpi", 4.0);
        
        Agent.AgentResponse response = agent.analyze(data);
        
        assertTrue(response.analysis().contains("通胀压力较大"));
    }

    @Test
    void testAnalyzeWithLowCPI() {
        Map<String, Object> data = new HashMap<>();
        data.put("cpi", 1.0);
        
        Agent.AgentResponse response = agent.analyze(data);
        
        assertTrue(response.analysis().contains("通胀较低"));
    }

    @Test
    void testAnalyzeWithPMI() {
        Map<String, Object> data = new HashMap<>();
        data.put("pmi", 52.0);
        
        Agent.AgentResponse response = agent.analyze(data);
        
        assertTrue(response.analysis().contains("扩张区间"));
    }

    @Test
    void testAnalyzeWithLowPMI() {
        Map<String, Object> data = new HashMap<>();
        data.put("pmi", 48.0);
        
        Agent.AgentResponse response = agent.analyze(data);
        
        assertTrue(response.analysis().contains("收缩区间"));
    }

    @Test
    void testAnalyzeWithAllIndicators() {
        Map<String, Object> data = new HashMap<>();
        data.put("gdp", 3.5);
        data.put("cpi", 2.5);
        data.put("pmi", 52.0);
        
        Agent.AgentResponse response = agent.analyze(data);
        
        assertNotNull(response.metadata());
        assertTrue(response.metadata().containsKey("gdpAnalysis"));
        assertTrue(response.metadata().containsKey("cpiAnalysis"));
        assertTrue(response.metadata().containsKey("pmiAnalysis"));
        assertTrue(response.metadata().containsKey("agentType"));
        assertTrue(response.metadata().containsKey("timestamp"));
    }

    @Test
    void testAnalyzeWithEmptyData() {
        Map<String, Object> data = new HashMap<>();
        
        Agent.AgentResponse response = agent.analyze(data);
        
        assertNotNull(response);
        assertTrue(response.analysis().contains("综合判断"));
    }
}
