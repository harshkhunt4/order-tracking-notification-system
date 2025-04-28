package com.example.ordertracker.service;

import org.apache.kafka.common.Uuid;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import com.example.ordertracker.entity.Product;
import com.example.ordertracker.model.SaveProductRequest;
import com.example.ordertracker.repository.ProductRepository;

@Service
public class ProductService {

  private final ProductRepository productRepo;

  public ProductService(ProductRepository productRepo) {
    super();
    this.productRepo = productRepo;
  }

  public Iterable<Product> getAllProduct() {
    return productRepo.findAll();
  }

  public Product getProduct(String id) {
    return productRepo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found."));
  }

  public Product save(SaveProductRequest saveProduct) {
    Product product = Product.builder().id(Uuid.randomUuid().toString()).name(saveProduct.getName())
        .price(saveProduct.getPrice()).build();
    return productRepo.save(product);
  }
}
