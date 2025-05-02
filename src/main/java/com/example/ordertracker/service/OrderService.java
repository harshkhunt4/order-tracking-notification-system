package com.example.ordertracker.service;

import java.util.List;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.ordertracker.dto.OrderDTO;
import com.example.ordertracker.entity.Order;
import com.example.ordertracker.entity.OrderProduct;
import com.example.ordertracker.entity.Product;
import com.example.ordertracker.event.OrderProductEvent;
import com.example.ordertracker.mapper.OrderModelMapper;
import com.example.ordertracker.model.OrderStatus;
import com.example.ordertracker.model.request.OrderRequest;
import com.example.ordertracker.repository.OrderRepository;
import com.example.ordertracker.repository.ProductRepository;

@Service
public class OrderService {

  private final ProductRepository productRepo;
  private final OrderRepository orderRepo;
  private final OrderModelMapper orderMapper;
  private final KafkaTemplate<String, OrderProductEvent> kafkaTemplate;
  //private final ObjectMapper objMapper = new ObjectMapper();
  
  public OrderService(ProductRepository productRepo, OrderRepository orderRepo,
      OrderModelMapper orderMapper, KafkaTemplate<String, OrderProductEvent> kafkaTemplate) {
    super();
    this.productRepo = productRepo;
    this.orderRepo = orderRepo;
    this.orderMapper = orderMapper;
    this.kafkaTemplate = kafkaTemplate;
  }

  @PreAuthorize("hasAuthority('SCOPE_READ')")
  public OrderDTO placeOrder(OrderRequest orderRequest) {
    String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
    Order order = new Order();
    orderRequest.getProducts().stream().forEach(productDTO -> {
      Product product = this.productRepo.findById(productDTO.getProductId())
          .orElseThrow(() -> new RuntimeException("Product not found.."));

      OrderProduct orderProduct = new OrderProduct(order, product, productDTO.getQuantity());
      order.getOrderProducts().add(orderProduct);
    });
    order.setUserEmail(userEmail);
    Order savedOrder = this.orderRepo.save(order);

    savedOrder.getOrderProducts().stream().forEach(orderProduct -> {
      OrderProductEvent event = new OrderProductEvent(savedOrder.getId(),
          orderProduct.getProduct().getId(), userEmail, OrderStatus.PLACED,
          savedOrder.getCreateAt());
      this.kafkaTemplate.send("order-product-status-events", event);
    });

    return this.orderMapper.orderToDto(savedOrder);
  }

  @PreAuthorize("hasAuthority('SCOPE_READ')")
  public List<OrderDTO> getOrderDetails() {
    String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

    return this.orderMapper.ordersToDto(orderRepo.findByUserEmail(userEmail));
  }
}
