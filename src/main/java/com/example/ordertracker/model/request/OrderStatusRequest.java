package com.example.ordertracker.model.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusRequest {

  @NotEmpty(message = "orderId cannot be null.")
  private String orderId;
  
  @NotEmpty(message = "productId cannot be null.")
  private String productId;
}
