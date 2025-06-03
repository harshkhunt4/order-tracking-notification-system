package com.example.ordertracker.dto;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
  private String orderId;
  private List<OrderProductDTO> products;
  private Date orderPlacedOn;

}
