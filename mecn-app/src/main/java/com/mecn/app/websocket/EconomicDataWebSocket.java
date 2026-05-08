package com.mecn.app.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * WebSocket Handler for Real-time Economic Data
 * 
 * Features:
 * - Real-time economic indicator updates
 * - Causal network change notifications
 * - System status broadcasts
 */
@Component
@EnableWebSocket
public class EconomicDataWebSocket extends TextWebSocketHandler implements WebSocketConfigurer {

    private static final Logger log = LoggerFactory.getLogger(EconomicDataWebSocket.class);
    
    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(this, "/ws/economic-data")
                .setAllowedOrigins("*");
    }
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        log.info("WebSocket connection established: {}", session.getId());
        
        // Send welcome message
        sendMessage(session, createMessage("CONNECTED", "Connected to MECN Real-time Data Stream"));
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        log.info("WebSocket connection closed: {} - {}", session.getId(), status);
    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        log.debug("Received message from {}: {}", session.getId(), message.getPayload());
        
        // Handle client subscriptions
        String payload = message.getPayload();
        if (payload.startsWith("SUBSCRIBE:")) {
            String topic = payload.substring(10);
            log.info("Client {} subscribed to: {}", session.getId(), topic);
            sendMessage(session, createMessage("SUBSCRIBED", topic));
        }
    }
    
    /**
     * Broadcast indicator update to all connected clients
     * @param indicator Indicator name
     * @param value New value
     * @param timestamp Update timestamp
     */
    public void broadcastIndicatorUpdate(String indicator, Object value, String timestamp) {
        String message = createIndicatorUpdateMessage(indicator, value, timestamp);
        broadcast(message);
    }
    
    /**
     * Broadcast network change to all connected clients
     * @param networkId Network identifier
     * @param changeType Type of change
     */
    public void broadcastNetworkChange(String networkId, String changeType) {
        String message = createNetworkChangeMessage(networkId, changeType);
        broadcast(message);
    }
    
    /**
     * Broadcast system status
     * @param status Status message
     */
    public void broadcastSystemStatus(String status) {
        String message = createMessage("SYSTEM_STATUS", status);
        broadcast(message);
    }
    
    /**
     * Send message to specific session
     */
    private void sendMessage(WebSocketSession session, String message) {
        try {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(message));
            }
        } catch (IOException e) {
            log.error("Failed to send message to session {}: {}", session.getId(), e.getMessage());
        }
    }
    
    /**
     * Broadcast message to all connected sessions
     */
    private void broadcast(String message) {
        for (WebSocketSession session : sessions) {
            sendMessage(session, message);
        }
    }
    
    private String createMessage(String type, String content) {
        return String.format("{\"type\":\"%s\",\"content\":\"%s\"}", type, content);
    }
    
    private String createIndicatorUpdateMessage(String indicator, Object value, String timestamp) {
        return String.format(
            "{\"type\":\"INDICATOR_UPDATE\",\"indicator\":\"%s\",\"value\":%s,\"timestamp\":\"%s\"}",
            indicator, value, timestamp
        );
    }
    
    private String createNetworkChangeMessage(String networkId, String changeType) {
        return String.format(
            "{\"type\":\"NETWORK_CHANGE\",\"networkId\":\"%s\",\"changeType\":\"%s\"}",
            networkId, changeType
        );
    }
}
