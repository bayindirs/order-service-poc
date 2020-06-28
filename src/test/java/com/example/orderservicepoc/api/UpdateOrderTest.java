package com.example.orderservicepoc.api;

import com.example.orderservicepoc.data.OrderRepository;
import com.example.orderservicepoc.data.OrderTestData;
import com.example.orderservicepoc.model.Order;
import com.example.orderservicepoc.model.OrderItem;
import com.example.orderservicepoc.model.OrderStatus;
import com.example.orderservicepoc.util.DateUtil;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UpdateOrderTest extends DocumentedMvcTest {

  Order newOrder;
  Order completedOrder;

  @MockBean
  OrderRepository orderRepository;

  @BeforeEach
  public void setUp(
          WebApplicationContext webApplicationContext,
          RestDocumentationContextProvider restDocumentation) {
    super.setUp(webApplicationContext, restDocumentation);
    setupOrder();
  }

  private void setupOrder() {
    try {
      newOrder =
              OrderTestData.mockOrders().stream()
                      .filter(o -> o.getStatus().equals(OrderStatus.NEW))
                      .findAny()
                      .orElse(null);
      completedOrder =
              OrderTestData.mockOrders().stream()
                      .filter(o -> o.getStatus().equals(OrderStatus.COMPLETED))
                      .findAny()
                      .orElse(null);
      when(orderRepository.findById(newOrder.getId())).thenReturn(Optional.of(newOrder));
      when(orderRepository.findById(completedOrder.getId()))
              .thenReturn(Optional.of(completedOrder));
      when(orderRepository.save(any(Order.class)))
              .then(
                      invocationOnMock -> {
                        Order order = invocationOnMock.getArgument(0);
                        if (order.getId() == null) {
                          order.setId(UUID.randomUUID().toString());
                        }
                        for (OrderItem orderItem : order.getItems()) {
                          if (orderItem.getId() == null) {
                            orderItem.setId(UUID.randomUUID().toString());
                          }
                        }
                        return order;
                      });
    } catch (Exception ignored) {
    }
  }

  @Test
  void updateOrder() throws Exception {
    OrderInfo orderInfo = OrderTestData.validOrderInfo();
    orderInfo.setCustomerId(newOrder.getCustomerId());
    ResultActions resultActions = mockMvc.perform(updateOrderRequest(newOrder.getId(), orderInfo));
    resultActions
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(newOrder.getId()))
            .andExpect(jsonPath("$.customerId").value(orderInfo.getCustomerId()))
            .andExpect(jsonPath("$.status").value(OrderStatus.NEW.name()))
            .andExpect(jsonPath("$.orderDate").exists());
    for (int i = 0; i < orderInfo.getItems().size(); i++) {
      OrderItemInfo orderItemInfo = orderInfo.getItems().get(i);
      String itemPath = String.format("$.items[%d]", i);
      resultActions
              .andExpect(jsonPath(itemPath).exists())
              .andExpect(jsonPath(itemPath.concat(".id")).exists())
              .andExpect(
                      jsonPath(itemPath.concat(".productCode")).value(orderItemInfo.getProductCode()))
              .andExpect(jsonPath(itemPath.concat(".quantity")).value(orderItemInfo.getQuantity()));
    }
  }

  @Test
  void updateOrderItemsRequired() throws Exception {
    OrderInfo orderInfo = OrderTestData.validOrderInfo();
    orderInfo.setCustomerId(newOrder.getCustomerId());
    orderInfo.setItems(null);
    ResultActions resultActions = mockMvc.perform(updateOrderRequest(newOrder.getId(), orderInfo));
    resultActions
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.errors.items").value("ITEMS_REQUIRED"));
  }

  @Test
  void updateOrderOrderDateCannotBePastDate() throws Exception {
    OrderInfo orderInfo = OrderTestData.validOrderInfo();
    orderInfo.setCustomerId(newOrder.getCustomerId());
    orderInfo.setOrderDate(DateUtil.toString(new Date(0)));
    ResultActions resultActions = mockMvc.perform(updateOrderRequest(newOrder.getId(), orderInfo));
    resultActions
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.errors.orderDate").value("ORDER_DATE_CANNOT_BE_PAST_DATE"));
  }

  @Test
  void updateOrderOnlyNewOrderCanBeUpdated() throws Exception {
    OrderInfo orderInfo = OrderTestData.validOrderInfo();
    orderInfo.setCustomerId(completedOrder.getCustomerId());
    ResultActions resultActions =
            mockMvc.perform(updateOrderRequest(completedOrder.getId(), orderInfo));
    resultActions
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.errors.status").value("ONLY_NEW_ORDER_CAN_BE_UPDATED"));
  }

  @Test
  void updateOrderCustomerInfoDoesNotMatch() throws Exception {
    OrderInfo orderInfo = OrderTestData.validOrderInfo();
    orderInfo.setCustomerId("dummy-customer");
    ResultActions resultActions = mockMvc.perform(updateOrderRequest(newOrder.getId(), orderInfo));
    resultActions
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.errors.customerId").value("CUSTOMER_INFO_DOES_NOT_MATCH"));
  }

  private RequestBuilder updateOrderRequest(String orderId, OrderInfo orderInfo) {
    String body = new Gson().toJson(orderInfo);
    return MockMvcRequestBuilders.put("/order/{orderId}", orderId)
            .content(body)
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON);
  }
}
