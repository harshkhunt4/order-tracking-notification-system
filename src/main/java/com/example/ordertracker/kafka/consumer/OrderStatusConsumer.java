package com.example.ordertracker.kafka.consumer;

import java.util.Optional;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.ordertracker.entity.OrderProduct;
import com.example.ordertracker.event.OrderProductEvent;
import com.example.ordertracker.model.OrderStatus;
import com.example.ordertracker.repository.OrderProductRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderStatusConsumer {

  private final OrderProductRepository orderProductRepo;

  public OrderStatusConsumer(OrderProductRepository orderProductRepo) {
    super();
    this.orderProductRepo = orderProductRepo;
  }

  @KafkaListener(topics = "order-product-status-events", groupId = "order-consumer-group")
  public void consumeOrderEvent(OrderProductEvent orderProductEvent) {
    
    log.info("Received order event: {}", orderProductEvent);

    String productId = orderProductEvent.getProductId();
    String orderId = orderProductEvent.getOrderId();
    Optional<OrderProduct> orderProduct = orderProductRepo.findByProductIdAndOrderId(
        productId, orderId);

    if (orderProduct.isEmpty())
      return;

    OrderProduct order_product = orderProduct.get();
   
    switch (order_product.getStatus()) {
    case PLACED:
      order_product.setStatus(OrderStatus.PACKED);
      break;
      
    case PACKED:
      order_product.setStatus(OrderStatus.SHIPPED);
      break;
    
    case SHIPPED:
      order_product.setStatus(OrderStatus.DELIVERED);
      break;

    default:
      throw new IllegalArgumentException("Unexpected value: " + order_product.getStatus());
    }
    
    orderProductRepo.updateProductStatusByProductIdAndOrderId(productId,orderId, order_product.getStatus());
  }
}
