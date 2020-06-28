package com.example.orderservicepoc.service;

import com.example.orderservicepoc.api.OrderInfo;
import com.example.orderservicepoc.api.OrderItemInfo;
import com.example.orderservicepoc.data.OrderRepository;
import com.example.orderservicepoc.exception.RequestArgumentNotValidException;
import com.example.orderservicepoc.model.OrderEntity;
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
  public static final String ERROR_ONLY_NEW_ORDER_CAN_BE_CANCELED =
          "ONLY_NEW_ORDER_CAN_BE_CANCELED";
  public static final String ERROR_ONLY_NEW_ORDER_CAN_BE_COMPLETED =
          "ONLY_NEW_ORDER_CAN_BE_COMPLETED";

  @Autowired
  OrderRepository orderRepository;

  public List<OrderEntity> findOrders() {
    return orderRepository.findAll();
  }

  public OrderEntity createOrder(OrderInfo orderInfo) {
    Date orderDate;
    try {
      orderDate = DateUtil.fromString(orderInfo.getOrderDate());
    } catch (ParseException e) {
      throw new RequestArgumentNotValidException("orderDate", ERROR_CANNOT_PARSE_ORDER_DATE);
    }
    if (orderDate != null && orderDate.before(new Date())) {
      throw new RequestArgumentNotValidException("orderDate", ERROR_ORDER_DATE_CANNOT_BE_PAST_DATE);
    }

    OrderEntity order = new OrderEntity();
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

  public OrderEntity updateOrder(String orderId, OrderInfo orderInfo) {
    OrderEntity order = findOrder(orderId);
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

  public OrderEntity cancelOrder(String orderId) {
    OrderEntity order = findOrder(orderId);
    if (!order.getStatus().equals(OrderStatus.NEW)) {
      throw new RequestArgumentNotValidException("status", ERROR_ONLY_NEW_ORDER_CAN_BE_CANCELED);
    }
    order.setStatus(OrderStatus.CANCELED);
    order.setCancelDate(new Date());
    return orderRepository.save(order);
  }

  public OrderEntity completeOrder(String orderId) {
    OrderEntity order = findOrder(orderId);
    if (!order.getStatus().equals(OrderStatus.NEW)) {
      throw new RequestArgumentNotValidException("status", ERROR_ONLY_NEW_ORDER_CAN_BE_COMPLETED);
    }
    order.setStatus(OrderStatus.COMPLETED);
    order.setCompleteDate(new Date());
    return orderRepository.save(order);
  }

  private OrderEntity findOrder(String orderId) {
    Optional<OrderEntity> optionalOrder = orderRepository.findById(orderId);
    if (!optionalOrder.isPresent()) {
      throw new RequestArgumentNotValidException("orderId", ERROR_ORDER_NOT_FOUND);
    }
    return optionalOrder.get();
  }
}
