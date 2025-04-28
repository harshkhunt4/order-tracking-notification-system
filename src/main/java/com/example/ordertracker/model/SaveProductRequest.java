package com.example.ordertracker.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveProductRequest {
  @NotEmpty(message = "Product name is required.")
  private String name;
  private Double price;
}
