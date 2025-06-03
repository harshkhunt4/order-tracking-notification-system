package com.example.ordertracker.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ordertracker.dto.OrderDTO;
import com.example.ordertracker.model.request.OrderRequest;
import com.example.ordertracker.model.request.OrderStatusRequest;
import com.example.ordertracker.model.response.OrderStatusResponse;
import com.example.ordertracker.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

  private final OrderService orderService;

  public OrderController(OrderService orderService) {
    super();
    this.orderService = orderService;
  }

  @PostMapping
  public OrderDTO placeOrder(@RequestBody OrderRequest orderRequest) {
    return this.orderService.placeOrder(orderRequest);
  }

  @GetMapping
  public List<OrderDTO> getOrderDetails() {
    return this.orderService.getOrderDetails();
  }

  @PostMapping("/status")
  public OrderStatusResponse getOrderStatus(@RequestBody OrderStatusRequest request) {
    return new OrderStatusResponse(
        this.orderService.getOrderStatus(request.getOrderId(), request.getProductId()));
  }
}
