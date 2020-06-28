package com.example.orderservicepoc.data;

import com.example.orderservicepoc.api.OrderInfo;
import com.example.orderservicepoc.api.OrderItemInfo;
import com.example.orderservicepoc.model.Order;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.util.List;

import static org.assertj.core.util.Lists.newArrayList;

public class OrderTestData {

  public static final String ORDER_DATA_FILE_PATH = "src/test/resources/data/order.json";

  public static List<Order> mockOrders() throws Exception {
    try (FileReader fileReader = new FileReader(ORDER_DATA_FILE_PATH)) {
      return newArrayList(gson().fromJson(fileReader, Order[].class));
    }
  }

  private static Gson gson() {
    return new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
  }

  public static OrderInfo validOrderInfo() {
    OrderInfo orderInfo = new OrderInfo();
    orderInfo.setCustomerId("customer-id");
    orderInfo.setItems(newArrayList(orderItem("product-1", 2)));
    return orderInfo;
  }

  private static OrderItemInfo orderItem(String productCode, int quantity) {
    OrderItemInfo itemInfo = new OrderItemInfo();
    itemInfo.setProductCode(productCode);
    itemInfo.setQuantity(quantity);
    return itemInfo;
  }
}
