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

  protected OrderResource createOrder(OrderInfo orderInfo) {
    Order order = orderService.createOrder(orderInfo);
    return new OrderResource(order);
  }

  protected OrderResource updateOrder(String orderId, OrderInfo orderInfo) {
    Order order = orderService.updateOrder(orderId, orderInfo);
    return new OrderResource(order);
  }

  protected OrderResource cancelOrder(String orderId) {
    Order order = orderService.cancelOrder(orderId);
    return new OrderResource(order);
  }
}
