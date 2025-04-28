package com.example.ordertracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ordertracker.entity.Order;
import java.util.List;


public interface OrderRepository extends JpaRepository<Order, String> {

  List<Order> findByUserEmail(String userEmail);
}
