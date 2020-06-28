package com.example.orderservicepoc.api;

import com.example.orderservicepoc.model.OrderEntity;
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

  @DeleteMapping("/{orderId}")
  public OrderResource cancelOrder(@PathVariable String orderId) {
    return super.cancelOrder(orderId);
  }

  @PostMapping("/{orderId}/complete")
  public OrderResource completeOrder(@PathVariable String orderId) {
    OrderEntity order = orderService.completeOrder(orderId);
    return new OrderResource(order);
  }
}
