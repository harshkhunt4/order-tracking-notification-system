package com.example.ordertracker.event;

import java.util.Date;

import com.example.ordertracker.model.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderProductEvent {
  private String orderId;
  private String productId;
  private String userId;
  private OrderStatus status;
  private Date timestamp;
}
