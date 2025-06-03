package com.example.ordertracker.util;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.example.ordertracker.model.StatusNotification;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OrderStatusHandler extends TextWebSocketHandler {
  private static final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    // Extract user ID from the WebSocket session (e.g., from query parameters or headers)
    String userId = getUserIdFromSession(session);
    if (userId != null) {
      userSessions.put(userId, session);
      log.info("WebSocket connection established for user: {}", userId);
    } else {
      log.warn("WebSocket connection established without user ID. Closing session.");
      session.close(CloseStatus.BAD_DATA);
    }
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    log.info("Received message: {}", message.getPayload());
    // Handle incoming messages if needed

  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    String userId = getUserIdFromSession(session);
    if (userId != null) {
      userSessions.remove(userId);
      log.info("WebSocket connection closed for user: {}", userId);
    }
  }

  public void sendOrderStatusUpdate(String userId, StatusNotification orderStatus) {
    WebSocketSession session = userSessions.get(userId);
    if (session != null && session.isOpen()) {
      try {
        session.sendMessage(new TextMessage(orderStatus.convertToString()));
        log.info("Order status update sent to user: {}", userId);
      } catch (IOException e) {
        log.error("Error sending WebSocket message to user: {}", userId, e);
      }
    } else {
      log.warn("No active WebSocket session found for user: {}", userId);
    }
  }

  private String getUserIdFromSession(WebSocketSession session) {
    return (String) session.getAttributes().get("userId");
  }
}
