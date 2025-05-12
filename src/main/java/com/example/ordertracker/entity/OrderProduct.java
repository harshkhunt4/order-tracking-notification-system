package com.example.ordertracker.entity;

import com.example.ordertracker.model.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderProduct {

  @EmbeddedId
  @JsonIgnore
  private OrderProductPK pk;
  
  @Column(nullable = false)
  private Integer quantity;
  
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OrderStatus status;
  
  public OrderProduct(Order order,Product product,Integer quantity) {
    pk = new OrderProductPK();
    pk.setOrder(order);
    pk.setProduct(product);
    this.quantity = quantity;
    this.status = OrderStatus.PLACED;
  }
  
  @Transient
  public Product getProduct() {
      return this.pk.getProduct();
  }

  @Transient
  public Double getTotalPrice() {
      return getProduct().getPrice() * getQuantity();
  }

}
