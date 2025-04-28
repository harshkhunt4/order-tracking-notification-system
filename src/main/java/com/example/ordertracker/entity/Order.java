package com.example.ordertracker.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(name = "userId", nullable = false)
  private String userEmail;

  @CreationTimestamp
  @Column(updatable = false, name = "created_at")
  private Date createAt;

  // @JsonManagedReference
  @OneToMany(mappedBy = "pk.order", cascade = CascadeType.ALL)
  private List<OrderProduct> orderProducts = new ArrayList<>();

  @Transient
  public Double getTotalOrderPrice() {
    double sum = 0D;
    List<OrderProduct> orderProductList = getOrderProducts();
    for (OrderProduct op : orderProductList) {
      sum += op.getTotalPrice();
    }
    return sum;
  }

  @Transient
  public int getNumberOfProducts() {
    return this.orderProducts.size();
  }

}
