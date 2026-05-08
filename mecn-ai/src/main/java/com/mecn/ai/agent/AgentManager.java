package com.mecn.ai.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Agent Manager
 * 
 * Manages and coordinates all AI agents in the MECN system.
 * Provides:
 * - Agent registration and discovery
 * - Agent execution orchestration
 * - Response aggregation
 */
@Service
public class AgentManager {

    private static final Logger log = LoggerFactory.getLogger(AgentManager.class);
    
    private final Map<String, Agent> agents = new ConcurrentHashMap<>();
    
    /**
     * Register an agent
     * @param agent Agent to register
     */
    public void registerAgent(Agent agent) {
        agents.put(agent.getName(), agent);
        log.info("Registered agent: {} ({})", agent.getName(), agent.getType());
    }
    
    /**
     * Get agent by name
     * @param name Agent name
     * @return Agent or null
     */
    public Agent getAgent(String name) {
        return agents.get(name);
    }
    
    /**
     * Get all registered agents
     * @return List of agents
     */
    public List<Agent> getAllAgents() {
        return new ArrayList<>(agents.values());
    }
    
    /**
     * Get agents by type
     * @param type Agent type
     * @return List of agents of specified type
     */
    public List<Agent> getAgentsByType(Agent.AgentType type) {
        return agents.values().stream()
            .filter(agent -> agent.getType() == type)
            .toList();
    }
    
    /**
     * Execute analysis using a specific agent
     * @param agentName Agent name
     * @param data Input data
     * @return Agent response
     */
    public Agent.AgentResponse executeAgent(String agentName, Map<String, Object> data) {
        Agent agent = agents.get(agentName);
        if (agent == null) {
            throw new IllegalArgumentException("Agent not found: " + agentName);
        }
        
        if (!agent.isAvailable()) {
            throw new IllegalStateException("Agent is not available: " + agentName);
        }
        
        log.info("Executing agent: {} with data: {}", agentName, data.keySet());
        return agent.analyze(data);
    }
    
    /**
     * Execute all agents of a specific type
     * @param type Agent type
     * @param data Input data
     * @return List of responses
     */
    public List<Agent.AgentResponse> executeAllByType(Agent.AgentType type, Map<String, Object> data) {
        return getAgentsByType(type).stream()
            .filter(Agent::isAvailable)
            .map(agent -> agent.analyze(data))
            .toList();
    }
    
    /**
     * Execute all available agents
     * @param data Input data
     * @return List of responses
     */
    public List<Agent.AgentResponse> executeAll(Map<String, Object> data) {
        return agents.values().stream()
            .filter(Agent::isAvailable)
            .map(agent -> agent.analyze(data))
            .toList();
    }
    
    /**
     * Get agent status
     * @return Map of agent names to availability
     */
    public Map<String, Boolean> getAgentStatus() {
        Map<String, Boolean> status = new HashMap<>();
        agents.forEach((name, agent) -> status.put(name, agent.isAvailable()));
        return status;
    }
}
