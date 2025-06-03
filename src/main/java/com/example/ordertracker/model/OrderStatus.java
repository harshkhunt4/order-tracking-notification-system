package com.example.ordertracker.model;

public enum OrderStatus {
  PLACED, PACKED, SHIPPED, OUT_FOR_DELIVERY, DELIVERED;

  public static OrderStatus getNextStatus(OrderStatus current) {
    switch (current) {
    case PLACED:
      return PACKED;
    case PACKED:
      return SHIPPED;
    case SHIPPED:
      return OUT_FOR_DELIVERY;
    case OUT_FOR_DELIVERY:
      return DELIVERED;
    default:
      return null; // Already delivered
    }
  }
}
