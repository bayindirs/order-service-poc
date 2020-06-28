package com.example.orderservicepoc.api;

import com.example.orderservicepoc.data.OrderRepository;
import com.example.orderservicepoc.data.OrderTestData;
import com.example.orderservicepoc.model.OrderEntity;
import com.example.orderservicepoc.model.OrderItem;
import com.example.orderservicepoc.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompleteOrderTest extends DocumentedMvcTest {

  OrderEntity newOrder;
  OrderEntity cancelledOrder;

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
      cancelledOrder =
              OrderTestData.mockOrders().stream()
                      .filter(o -> o.getStatus().equals(OrderStatus.COMPLETED))
                      .findAny()
                      .orElse(null);
      when(orderRepository.findById(newOrder.getId())).thenReturn(Optional.of(newOrder));
      when(orderRepository.findById(cancelledOrder.getId()))
              .thenReturn(Optional.of(cancelledOrder));
      when(orderRepository.save(any(OrderEntity.class)))
              .then(
                      invocationOnMock -> {
                        OrderEntity order = invocationOnMock.getArgument(0);
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
  void completeOrder() throws Exception {
    ResultActions resultActions = mockMvc.perform(completeOrderRequest(newOrder.getId()));
    resultActions
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(newOrder.getId()))
            .andExpect(jsonPath("$.status").value(OrderStatus.COMPLETED.name()));
  }

  @Test
  void completeOrderOnlyNewOrderCanBeCompleted() throws Exception {
    ResultActions resultActions = mockMvc.perform(completeOrderRequest(cancelledOrder.getId()));
    resultActions
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.errors.status").value("ONLY_NEW_ORDER_CAN_BE_COMPLETED"));
  }

  private RequestBuilder completeOrderRequest(String orderId) {
    return MockMvcRequestBuilders.post("/order/{orderId}/complete", orderId);
  }
}
