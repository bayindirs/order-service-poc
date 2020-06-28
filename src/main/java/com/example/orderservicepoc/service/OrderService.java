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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

  public static final String ERROR_ORDER_DATE_CANNOT_BE_PAST_DATE =
          "ORDER_DATE_CANNOT_BE_PAST_DATE";
  public static final String ERROR_CANNOT_PARSE_ORDER_DATE = "CANNOT_PARSE_ORDER_DATE";
  public static final String ERROR_ORDER_NOT_FOUND = "ORDER_NOT_FOUND";
  public static final String ERROR_CUSTOMER_INFO_DOES_NOT_MATCH = "CUSTOMER_INFO_DOES_NOT_MATCH";
  public static final String ERROR_ONLY_NEW_ORDER_CAN_BE_UPDATED = "ONLY_NEW_ORDER_CAN_BE_UPDATED";

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
      throw new RequestArgumentNotValidException("orderDate", ERROR_CANNOT_PARSE_ORDER_DATE);
    }
    if (orderDate != null && orderDate.before(new Date())) {
      throw new RequestArgumentNotValidException("orderDate", ERROR_ORDER_DATE_CANNOT_BE_PAST_DATE);
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

  public Order updateOrder(String orderId, OrderInfo orderInfo) {
    Optional<Order> optionalOrder = orderRepository.findById(orderId);
    if (!optionalOrder.isPresent()) {
      throw new RequestArgumentNotValidException("orderId", ERROR_ORDER_NOT_FOUND);
    }
    Order order = optionalOrder.get();
    if (!order.getStatus().equals(OrderStatus.NEW)) {
      throw new RequestArgumentNotValidException("status", ERROR_ONLY_NEW_ORDER_CAN_BE_UPDATED);
    }
    if (!order.getCustomerId().contentEquals(orderInfo.getCustomerId())) {
      throw new RequestArgumentNotValidException("customerId", ERROR_CUSTOMER_INFO_DOES_NOT_MATCH);
    }
    try {
      Date orderDate = DateUtil.fromString(orderInfo.getOrderDate());
      if (orderDate != null && orderDate.before(new Date())) {
        throw new RequestArgumentNotValidException(
                "orderDate", ERROR_ORDER_DATE_CANNOT_BE_PAST_DATE);
      }
      order.setOrderDate(orderDate);
    } catch (ParseException e) {
      throw new RequestArgumentNotValidException("orderDate", ERROR_CANNOT_PARSE_ORDER_DATE);
    }
    order.getItems().clear();
    order.setItems(createOrderItems(orderInfo.getItems()));
    return orderRepository.save(order);
  }
}
