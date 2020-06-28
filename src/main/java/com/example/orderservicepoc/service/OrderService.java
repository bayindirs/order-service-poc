package com.example.orderservicepoc.service;

import com.example.orderservicepoc.data.OrderRepository;
import com.example.orderservicepoc.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

  @Autowired
  OrderRepository orderRepository;

  public List<Order> findOrders() {
    return orderRepository.findAll();
  }
}
