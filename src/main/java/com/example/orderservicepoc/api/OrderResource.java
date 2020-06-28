package com.example.orderservicepoc.api;

import com.example.orderservicepoc.model.Order;
import com.example.orderservicepoc.model.OrderItem;
import com.example.orderservicepoc.model.OrderStatus;
import com.example.orderservicepoc.util.DateUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class OrderResource {

    private String id;
    private String customerId;
    private List<OrderItemResource> items;
    private String orderDate;
    private String cancelDate;
    private String completeDate;
    private OrderStatus status;

    public OrderResource(Order order) {
        id = order.getId();
        customerId = order.getCustomerId();
        items = constructItems(order.getItems());
        orderDate = DateUtil.toString(order.getOrderDate());
        cancelDate = DateUtil.toString(order.getCancelDate());
        status = order.getStatus();
    }

    private List<OrderItemResource> constructItems(List<OrderItem> items) {
        if (items != null) {
            return items.stream().map(OrderItemResource::new).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
