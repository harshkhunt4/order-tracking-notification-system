package com.example.ordertracker.service;

import java.util.List;

import org.apache.kafka.common.Uuid;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import com.example.ordertracker.entity.Product;
import com.example.ordertracker.model.SaveProductRequest;
import com.example.ordertracker.repository.ProductRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductService {
  private final ProductRepository productRepo;
  private final RedisService redisService;

  public ProductService(ProductRepository productRepo,RedisService redisService) {
    super();
    this.productRepo = productRepo;
    this.redisService = redisService;
  }

  public Iterable<Product> getAllProduct() {
    List<Product> products = this.redisService.getAllProducts();
    if (!products.isEmpty()) {
      log.info("Products fetch from redis.");
      return products;
    }
    products = productRepo.findAll();
    products.stream().forEach(p -> this.redisService.setProducts(p.getId(), p));

    log.info("All products fetched from DB and cached in Redis");
    return products;
  }

  public Product getProduct(String id) {
    return this.redisService.getProduct(id).orElse(this.productRepo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found.")));
  }

  public Product save(SaveProductRequest saveProduct) {
    Product product = Product.builder() .id(Uuid.randomUuid().toString()).name(saveProduct.getName())
        .price(saveProduct.getPrice()).build();
    this.redisService.setProducts(product.getId(), product);
    return productRepo.save(product);
  }
}
