package com.example.orderservicepoc.api;

import lombok.Data;

import javax.persistence.OneToMany;
import java.util.List;

@Data
public class OrderInfo {

  private String customerId;

  @OneToMany
  private List<OrderItemInfo> items;
}
