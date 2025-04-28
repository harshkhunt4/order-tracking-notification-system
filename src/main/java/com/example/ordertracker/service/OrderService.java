package com.example.ordertracker.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.ordertracker.dto.OrderDTO;
import com.example.ordertracker.entity.Order;
import com.example.ordertracker.entity.OrderProduct;
import com.example.ordertracker.entity.Product;
import com.example.ordertracker.mapper.OrderModelMapper;
import com.example.ordertracker.model.request.OrderRequest;
import com.example.ordertracker.repository.OrderRepository;
import com.example.ordertracker.repository.ProductRepository;

@Service
public class OrderService {

  private final ProductRepository productRepo;
  private final OrderRepository orderRepo;
  private final OrderModelMapper orderMapper;

  public OrderService(ProductRepository productRepo, OrderRepository orderRepo,
      OrderModelMapper orderMapper) {
    super();
    this.productRepo = productRepo;
    this.orderRepo = orderRepo;
    this.orderMapper = orderMapper;
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
    return this.orderMapper.orderToDto(savedOrder);
  }
  
  @PreAuthorize("hasAuthority('SCOPE_READ')")
  public List<OrderDTO> getOrderDetails() {
    String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

    return this.orderMapper.ordersToDto(orderRepo.findByUserEmail(userEmail));
  }
}
