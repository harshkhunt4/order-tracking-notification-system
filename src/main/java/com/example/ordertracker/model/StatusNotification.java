package com.example.ordertracker.model;

import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusNotification {
  private final static ObjectMapper mapper = new ObjectMapper();
  
  private String orderId;
  private String productId;
  private OrderStatus newStatus;
  private Date updatedAt;
  
  public String convertToString() {
    try {
      return mapper.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return "Issue from server side.";
  }
}