package com.example.ordertracker.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductPK {

  //@JsonBackReference
  @ManyToOne(optional = false,fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id")
  private Order order;
  
  @ManyToOne(optional = false,fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id")
  private Product product;

}
