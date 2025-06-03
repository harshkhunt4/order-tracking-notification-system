package com.example.ordertracker.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ordertracker.entity.Product;
import com.example.ordertracker.service.ProductService;

@RestController
@RequestMapping("api/products")
public class ProductController {

  private final ProductService productService;

  public ProductController(ProductService productService) {
    super();
    this.productService = productService;
  }

  @GetMapping
  public Iterable<Product> getProduct() {
    return productService.getAllProduct();
  }
}
