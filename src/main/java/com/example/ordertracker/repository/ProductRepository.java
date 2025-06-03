package com.example.ordertracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ordertracker.entity.Product;

public interface ProductRepository extends JpaRepository<Product, String> {

}
