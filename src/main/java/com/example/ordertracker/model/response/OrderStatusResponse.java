package com.example.ordertracker.model.response;

import com.example.ordertracker.model.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusResponse {
  private OrderStatus status;
}
