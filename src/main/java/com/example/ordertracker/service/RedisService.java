package com.example.ordertracker.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.ordertracker.entity.Product;
import com.example.ordertracker.model.OrderStatus;

@Service
public class RedisService {
  private static final String PRODUCT_KEY = "product_";

  private final RedisTemplate<String, Product> redisOrderTemplate;
  private final RedisTemplate<String, String> redisOrderProductEventTemplate;

  public RedisService(@Qualifier("product") RedisTemplate<String, Product> redisTemplate,
      @Qualifier("orderproductstatus") RedisTemplate<String, String> redisOrderProductEventTemplate) {
    this.redisOrderTemplate = redisTemplate;
    this.redisOrderProductEventTemplate = redisOrderProductEventTemplate;
  }

  public Optional<Product> getProduct(String key) {
    return Optional.ofNullable(redisOrderTemplate.opsForValue().get(PRODUCT_KEY + key));
  }

  public List<Product> getAllProducts() {
    Set<String> keys = redisOrderTemplate.keys(PRODUCT_KEY + "*");
    if (keys == null || keys.isEmpty()) {
      return Collections.emptyList();
    }

    List<Product> products = new ArrayList<>();
    for (String key : keys) {
      Product product = this.redisOrderTemplate.opsForValue().get(key);
      if (product != null) {
        products.add(product);
      }
    }
    return products;
  }

  public void setProducts(String key, Product products) {
    this.redisOrderTemplate.opsForValue().set(PRODUCT_KEY + key, products, Duration.ofMinutes(10));
  }

  public OrderStatus getOrderProductStatus(String key) {
    String statusStr = this.redisOrderProductEventTemplate.opsForValue().get(key);
    return statusStr == null ? null : OrderStatus.valueOf(statusStr);
  }

  public void setOrderProductStatus(String key,OrderStatus status) {
    this.redisOrderProductEventTemplate.opsForValue().set(key, status.name(), Duration.ofMinutes(10));
  }
}
