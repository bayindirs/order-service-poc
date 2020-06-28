package com.example.orderservicepoc.api;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class OrderInfo {

  @NotBlank(message = "CUSTOMER_ID_REQUIRED")
  private String customerId;

  @NotEmpty(message = "ITEMS_REQUIRED")
  private List<OrderItemInfo> items;

  @NotBlank(message = "ORDER_DATE_REQUIRED")
  private String orderDate;
}
