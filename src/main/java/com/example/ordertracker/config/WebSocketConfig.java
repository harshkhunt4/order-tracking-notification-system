package com.example.ordertracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.example.ordertracker.util.OrderStatusHandler;
import com.example.ordertracker.util.WebSocketInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(customOrderStatusHandler(), "/user/topic/order-status").addInterceptors(new WebSocketInterceptor())
        .setAllowedOriginPatterns("*");
  }
  
  @Bean
  public WebSocketHandler customOrderStatusHandler() {
      return new OrderStatusHandler();

  }
}
