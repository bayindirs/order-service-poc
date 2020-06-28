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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CreateOrderTest extends DocumentedMvcTest {

  @MockBean
  OrderRepository orderRepository;

  @BeforeEach
  public void setUp(
          WebApplicationContext webApplicationContext,
          RestDocumentationContextProvider restDocumentation) {
    super.setUp(webApplicationContext, restDocumentation);
    setupSave();
  }

  private void setupSave() {
    when(orderRepository.save(any(Order.class)))
            .then(
                    invocationOnMock -> {
                      Order order = invocationOnMock.getArgument(0);
                      order.setId(UUID.randomUUID().toString());
                      for (OrderItem orderItem : order.getItems()) {
                        orderItem.setId(UUID.randomUUID().toString());
                      }
                      return order;
                    });
  }

  @Test
  void createOrder() throws Exception {
    OrderInfo orderInfo = OrderTestData.validOrderInfo();
    ResultActions resultActions = mockMvc.perform(createOrderRequest(orderInfo));
    resultActions
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
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
  void createOrderItemsRequired() throws Exception {
    OrderInfo orderInfo = OrderTestData.validOrderInfo();
    orderInfo.setItems(null);
    ResultActions resultActions = mockMvc.perform(createOrderRequest(orderInfo));
    resultActions
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.errors.items").value("ITEMS_REQUIRED"));
  }

  @Test
  void createOrderOrderDateCannotBePastDate() throws Exception {
    OrderInfo orderInfo = OrderTestData.validOrderInfo();
    orderInfo.setOrderDate(DateUtil.toString(new Date(0)));
    ResultActions resultActions = mockMvc.perform(createOrderRequest(orderInfo));
    resultActions
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.errors.orderDate").value("ORDER_DATE_CANNOT_BE_PAST_DATE"));
  }

  private RequestBuilder createOrderRequest(OrderInfo orderInfo) {
    String body = new Gson().toJson(orderInfo);
    return MockMvcRequestBuilders.post("/order")
            .content(body)
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON);
  }
}
