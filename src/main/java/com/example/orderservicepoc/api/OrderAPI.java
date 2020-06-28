package com.example.orderservicepoc.api;

import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderAPI extends OrderAPIImpl {

  @GetMapping
  public List<OrderResource> listOrders() {
    return super.listOrders();
  }

  @PostMapping
  public OrderResource createOrder(@RequestBody @Valid OrderInfo orderInfo) {
    return super.createOrder(orderInfo);
  }

  @PutMapping("/{orderId}")
  public OrderResource updateOrder(
          @PathVariable String orderId, @RequestBody @Valid OrderInfo orderInfo) {
    return super.updateOrder(orderId, orderInfo);
  }
}
