package com.example.ordertracker.scheduler;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.ordertracker.entity.OrderProduct;
import com.example.ordertracker.event.OrderProductEvent;
import com.example.ordertracker.model.OrderStatus;
import com.example.ordertracker.repository.OrderProductRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderStatusSimulator {

  private final OrderProductRepository orderProductRepository;
  private final KafkaTemplate<String, OrderProductEvent> kafkaTemplate;

  public OrderStatusSimulator(OrderProductRepository orderProductRepository,
      KafkaTemplate<String, OrderProductEvent> kafkaTemplate) {
    super();
    this.orderProductRepository = orderProductRepository;
    this.kafkaTemplate = kafkaTemplate;
  }

  @Transactional
  @Scheduled(fixedRate = 50000) // Every 50 seconds
  public void simulate() {
    Pageable topFive = PageRequest.of(0, 5);
    List<OrderProduct> orders = orderProductRepository.findTop5StatusIsNot(OrderStatus.DELIVERED,topFive);

    for (OrderProduct order : orders) {
      log.info("Sending updating order event for" + order.getPk().toString() + " to comsumer.");

      OrderStatus currentStatus = OrderStatus.valueOf(order.getStatus().name());
      OrderStatus nextStatus = OrderStatus.getNextStatus(currentStatus);

      if (nextStatus != null) {
        order.setStatus(nextStatus);
        String orderId = order.getPk().getOrder().getId();
        String productId = order.getPk().getProduct().getId();

        OrderProductEvent event = new OrderProductEvent(orderId, productId,
            order.getPk().getOrder().getUserEmail(), nextStatus,
            order.getPk().getOrder().getCreateAt());

        kafkaTemplate.send("order-product-status-events", event);
      }
    }
  }
}
