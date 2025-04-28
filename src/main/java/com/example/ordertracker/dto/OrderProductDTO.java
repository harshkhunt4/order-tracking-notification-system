package com.example.ordertracker.dto;

import com.example.ordertracker.model.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductDTO {
  private String name;
  private Double price;
  private Integer quantity;
  private OrderStatus status;

}
