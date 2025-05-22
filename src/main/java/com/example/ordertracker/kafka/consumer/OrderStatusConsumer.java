package com.example.ordertracker.kafka.consumer;

import java.util.Optional;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.ordertracker.entity.OrderProduct;
import com.example.ordertracker.event.OrderProductEvent;
import com.example.ordertracker.repository.OrderProductRepository;
import com.example.ordertracker.service.RedisService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderStatusConsumer {

  private final OrderProductRepository orderProductRepo;
  private final RedisService redisService;

  public OrderStatusConsumer(OrderProductRepository orderProductRepo, RedisService redisService) {
    super();
    this.orderProductRepo = orderProductRepo;
    this.redisService = redisService;
  }

  @KafkaListener(topics = "order-product-status-events", groupId = "order-consumer-group")
  public void consumeOrderEvent(OrderProductEvent opEvent) {

    log.info("Received order event: {}", opEvent);

    String productId = opEvent.getProductId();
    String orderId = opEvent.getOrderId();
    Optional<OrderProduct> orderProductFromRepo = orderProductRepo
        .findByProductIdAndOrderId(productId, orderId);

    if (orderProductFromRepo.isEmpty()) {
      log.warn("Order not found: {}", opEvent.getOrderId());
      return;
    }

    orderProductRepo.updateProductStatusByProductIdAndOrderId(productId, orderId,
        opEvent.getStatus());

    this.redisService.setOrderProductStatus(orderId+productId,opEvent.getStatus());
  }
}
