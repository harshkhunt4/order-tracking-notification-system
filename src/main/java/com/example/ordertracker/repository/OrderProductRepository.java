package com.example.ordertracker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.ordertracker.entity.OrderProduct;
import com.example.ordertracker.entity.OrderProductPK;
import com.example.ordertracker.model.OrderStatus;

import jakarta.transaction.Transactional;

public interface OrderProductRepository extends JpaRepository<OrderProduct, OrderProductPK> {
  @Query("SELECT op FROM OrderProduct op WHERE op.pk.product.id = :productId AND op.pk.order.id = :orderId")
  Optional<OrderProduct> findByProductIdAndOrderId(@Param("productId") String productId,
      @Param("orderId") String orderId);
  
  @Modifying
  @Transactional
  @Query("UPDATE OrderProduct op SET op.status = :status WHERE op.pk.product.id = :productId AND op.pk.order.id = :orderId")
  int updateProductStatusByProductIdAndOrderId(@Param("productId") String productId,
      @Param("orderId") String orderId,@Param("status") OrderStatus status);
}
