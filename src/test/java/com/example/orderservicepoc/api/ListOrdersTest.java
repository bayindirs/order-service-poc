package com.example.orderservicepoc.api;

import com.example.orderservicepoc.data.OrderRepository;
import com.example.orderservicepoc.model.Order;
import com.example.orderservicepoc.model.OrderItem;
import com.example.orderservicepoc.util.DateUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static com.example.orderservicepoc.data.OrderTestData.mockOrders;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ListOrdersTest extends DocumentedMvcTest {

  @MockBean
  OrderRepository orderRepository;

  @BeforeEach
  public void setUp(
          WebApplicationContext webApplicationContext,
          RestDocumentationContextProvider restDocumentation) {
    super.setUp(webApplicationContext, restDocumentation);
  }

  @Test
  void listOrders() throws Exception {
    List<Order> orders = mockOrders();
    when(orderRepository.findAll()).thenReturn(orders);
    ResultActions resultActions = mockMvc.perform(listOrderRequest());
    resultActions.andDo(print()).andExpect(status().isOk());
    assertOrders(orders, resultActions);
  }

  private void assertOrders(List<Order> orders, ResultActions resultActions) throws Exception {
    for (Order order : orders) {
      assertOrder(order, resultActions);
    }
  }

  private void assertOrder(Order order, ResultActions resultActions) throws Exception {
    String orderPath = String.format("$[?(@.id == '%s')]", order.getId());
    resultActions
            .andExpect(jsonPath(orderPath).exists())
            .andExpect(jsonPath(orderPath.concat(".customerId")).value(order.getCustomerId()))
            .andExpect(
                    jsonPath(orderPath.concat(".orderDate")).value(DateUtil.toString(order.getOrderDate())))
            .andExpect(jsonPath(orderPath.concat(".status")).value(order.getStatus().name()));
    assertOrderItems(order, resultActions);
  }

  private void assertOrderItems(Order order, ResultActions resultActions) throws Exception {
    for (OrderItem orderItem : order.getItems()) {
      assertOrderItem(order, orderItem, resultActions);
    }
  }

  private void assertOrderItem(Order order, OrderItem orderItem, ResultActions resultActions)
          throws Exception {
    String orderPath = String.format("$[?(@.id == '%s')]", order.getId());
    String itemPath = orderPath + String.format(".items[?(@.id == '%s')]", orderItem.getId());
    resultActions
            .andExpect(jsonPath(itemPath).exists())
            .andExpect(jsonPath(itemPath.concat(".productCode")).value(orderItem.getProductCode()))
            .andExpect(jsonPath(itemPath.concat(".quantity")).value(orderItem.getQuantity()));
  }

  private RequestBuilder listOrderRequest() {
    return MockMvcRequestBuilders.get("/order")
            .characterEncoding("UTF-8").accept(MediaType.APPLICATION_JSON);
  }
}
