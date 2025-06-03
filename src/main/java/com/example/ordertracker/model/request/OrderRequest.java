package com.example.ordertracker.model.request;

import java.util.List;

import com.example.ordertracker.dto.ProductDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

  private List<ProductDTO> products;
}
