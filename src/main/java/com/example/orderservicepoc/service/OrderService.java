package com.example.orderservicepoc.service;

import com.example.orderservicepoc.api.OrderInfo;
import com.example.orderservicepoc.api.OrderItemInfo;
import com.example.orderservicepoc.data.OrderRepository;
import com.example.orderservicepoc.exception.RequestArgumentNotValidException;
import com.example.orderservicepoc.model.Order;
import com.example.orderservicepoc.model.OrderItem;
import com.example.orderservicepoc.model.OrderStatus;
import com.example.orderservicepoc.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

  @Autowired
  OrderRepository orderRepository;

  public List<Order> findOrders() {
    return orderRepository.findAll();
  }

  public Order createOrder(OrderInfo orderInfo) {
    Date orderDate;
    try {
      orderDate = DateUtil.fromString(orderInfo.getOrderDate());
    } catch (ParseException e) {
      throw new RequestArgumentNotValidException("orderDate", "CANNOT_PARSE_ORDER_DATE");
    }
    if (orderDate != null && orderDate.before(new Date())) {
      throw new RequestArgumentNotValidException("orderDate", "ORDER_DATE_CANNOT_BE_PAST_DATE");
    }

    Order order = new Order();
    order.setCustomerId(orderInfo.getCustomerId());
    order.setOrderDate(orderDate);
    order.setStatus(OrderStatus.NEW);
    order.setItems(createOrderItems(orderInfo.getItems()));
    return orderRepository.save(order);
  }

  private List<OrderItem> createOrderItems(List<OrderItemInfo> items) {
    if (items != null) {
      return items.stream()
              .map(
                      i -> {
                        OrderItem orderItem = new OrderItem();
                        orderItem.setProductCode(i.getProductCode());
                        orderItem.setQuantity(i.getQuantity());
                        return orderItem;
                      })
              .collect(Collectors.toList());
    }
    return new ArrayList<>();
  }
}