package com.example.ordertracker.mapper;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.stereotype.Component;

import com.example.ordertracker.dto.OrderDTO;
import com.example.ordertracker.dto.OrderProductDTO;
import com.example.ordertracker.entity.Order;
import com.example.ordertracker.entity.OrderProduct;

@Component
public class OrderModelMapper {

    private final ModelMapper mapper = new ModelMapper();

    public OrderModelMapper() {
        // Define mapping for OrderProduct -> OrderProductDTO
        TypeMap<OrderProduct, OrderProductDTO> orderProductTypeMap = mapper.createTypeMap(OrderProduct.class, OrderProductDTO.class);
        orderProductTypeMap.addMapping(src -> src.getProduct().getName(), OrderProductDTO::setName);
        orderProductTypeMap.addMapping(src -> src.getProduct().getPrice(), OrderProductDTO::setPrice);
        orderProductTypeMap.addMapping(OrderProduct::getQuantity, OrderProductDTO::setQuantity);
        orderProductTypeMap.addMapping(OrderProduct::getStatus, OrderProductDTO::setStatus);

        // Define mapping for Order -> OrderDTO
        TypeMap<Order, OrderDTO> orderTypeMap = mapper.createTypeMap(Order.class, OrderDTO.class);
        orderTypeMap.addMapping(Order::getId, OrderDTO::setOrderId);
        orderTypeMap.addMapping(Order::getCreateAt, OrderDTO::setOrderPlacedOn);
        // products mapping is manual (because need to convert list)
    }

    public OrderProductDTO orderProductToDto(OrderProduct orderProduct) {
        return mapper.map(orderProduct, OrderProductDTO.class);
    }

    public OrderDTO orderToDto(Order order) {
        OrderDTO dto = mapper.map(order, OrderDTO.class);
        if (order.getOrderProducts() != null) {
            List<OrderProductDTO> products = order.getOrderProducts()
                    .stream()
                    .map(this::orderProductToDto)
                    .toList();
            dto.setProducts(products);
        }
        return dto;
    }

    public List<OrderDTO> ordersToDto(List<Order> orders) {
        return orders.stream()
                .map(this::orderToDto)
                .toList();
    }
}

