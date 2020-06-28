package com.example.orderservicepoc.api;

import com.example.orderservicepoc.model.OrderItem;
import lombok.Data;

@Data
public class OrderItemResource {

    private String id;
    private String productCode;
    private Integer quantity;

    public OrderItemResource(OrderItem orderItem) {
        id = orderItem.getId();
        productCode = orderItem.getProductCode();
        quantity = orderItem.getQuantity();
    }
}
