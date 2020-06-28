package com.example.orderservicepoc.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderAPI extends OrderAPIImpl {

  @GetMapping
  public List<OrderResource> listOrders() {
    return super.listOrders();
  }
}
