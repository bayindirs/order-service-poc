package com.example.orderservicepoc.api;

import com.example.orderservicepoc.model.Order;
import com.example.orderservicepoc.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

public class OrderAPIImpl {

    @Autowired
    OrderService orderService;

    protected List<OrderResource> listOrders() {
        List<Order> orders = orderService.findOrders();
        return orders.stream().map(OrderResource::new).collect(Collectors.toList());
    }
}
