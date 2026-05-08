package com.mecn.ai.agent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AgentManager 单元测试
 */
class AgentManagerTest {

    private AgentManager agentManager;
    private MacroAnalystAgent macroAgent;
    private RiskAlertAgent riskAgent;

    @BeforeEach
    void setUp() {
        agentManager = new AgentManager();
        macroAgent = new MacroAnalystAgent();
        riskAgent = new RiskAlertAgent();
    }

    @Test
    void testRegisterAgent() {
        agentManager.registerAgent(macroAgent);
        
        List<Agent> agents = agentManager.getAllAgents();
        assertEquals(1, agents.size());
        assertEquals("Macro Analyst", agents.get(0).getName());
    }

    @Test
    void testGetAgent() {
        agentManager.registerAgent(macroAgent);
        
        Agent agent = agentManager.getAgent("Macro Analyst");
        assertNotNull(agent);
        assertEquals("Macro Analyst", agent.getName());
    }

    @Test
    void testGetAgentNotFound() {
        Agent agent = agentManager.getAgent("Nonexistent Agent");
        assertNull(agent);
    }

    @Test
    void testGetAllAgents() {
        agentManager.registerAgent(macroAgent);
        agentManager.registerAgent(riskAgent);
        
        List<Agent> agents = agentManager.getAllAgents();
        assertEquals(2, agents.size());
    }

    @Test
    void testGetAgentsByType() {
        agentManager.registerAgent(macroAgent);
        agentManager.registerAgent(riskAgent);
        
        List<Agent> macroAgents = agentManager.getAgentsByType(Agent.AgentType.MACRO_ANALYST);
        assertEquals(1, macroAgents.size());
        assertEquals("Macro Analyst", macroAgents.get(0).getName());
        
        List<Agent> riskAgents = agentManager.getAgentsByType(Agent.AgentType.RISK_ALERT);
        assertEquals(1, riskAgents.size());
        assertEquals("Risk Alert", riskAgents.get(0).getName());
    }

    @Test
    void testExecuteAgent() {
        agentManager.registerAgent(macroAgent);
        
        Map<String, Object> data = new HashMap<>();
        data.put("gdp", 3.5);
        
        Agent.AgentResponse response = agentManager.executeAgent("Macro Analyst", data);
        
        assertNotNull(response);
        assertEquals("Macro Analyst", response.agentName());
    }

    @Test
    void testExecuteAgentNotFound() {
        Map<String, Object> data = new HashMap<>();
        
        assertThrows(IllegalArgumentException.class, () -> {
            agentManager.executeAgent("Nonexistent Agent", data);
        });
    }

    @Test
    void testExecuteAllByType() {
        agentManager.registerAgent(macroAgent);
        agentManager.registerAgent(riskAgent);
        
        Map<String, Object> data = new HashMap<>();
        data.put("gdp", 3.5);
        data.put("cpi", 2.0);
        
        List<Agent.AgentResponse> responses = agentManager.executeAllByType(Agent.AgentType.MACRO_ANALYST, data);
        
        assertEquals(1, responses.size());
        assertEquals("Macro Analyst", responses.get(0).agentName());
    }

    @Test
    void testExecuteAll() {
        agentManager.registerAgent(macroAgent);
        agentManager.registerAgent(riskAgent);
        
        Map<String, Object> data = new HashMap<>();
        data.put("gdp", 3.5);
        data.put("cpi", 2.0);
        
        List<Agent.AgentResponse> responses = agentManager.executeAll(data);
        
        assertEquals(2, responses.size());
    }

    @Test
    void testGetAgentStatus() {
        agentManager.registerAgent(macroAgent);
        agentManager.registerAgent(riskAgent);
        
        Map<String, Boolean> status = agentManager.getAgentStatus();
        
        assertEquals(2, status.size());
        assertTrue(status.get("Macro Analyst"));
        assertTrue(status.get("Risk Alert"));
    }
}
