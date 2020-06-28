package com.example.orderservicepoc.api;

import com.example.orderservicepoc.data.OrderRepository;
import com.example.orderservicepoc.data.OrderTestData;
import com.example.orderservicepoc.model.OrderEntity;
import com.example.orderservicepoc.model.OrderItem;
import com.example.orderservicepoc.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CancelOrderTest extends DocumentedMvcTest {

  OrderEntity newOrder;
  OrderEntity completedOrder;

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
  void cancelOrder() throws Exception {
    ResultActions resultActions = mockMvc.perform(cancelOrderRequest(newOrder.getId()));
    resultActions
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(newOrder.getId()))
            .andExpect(jsonPath("$.status").value(OrderStatus.CANCELED.name()));
  }

  @Test
  void cancelOrderOnlyNewOrderCanBeCanceled() throws Exception {
    ResultActions resultActions = mockMvc.perform(cancelOrderRequest(completedOrder.getId()));
    resultActions
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.errors.status").value("ONLY_NEW_ORDER_CAN_BE_CANCELED"));
  }

  private RequestBuilder cancelOrderRequest(String orderId) {
    return MockMvcRequestBuilders.delete("/order/{orderId}", orderId)
            .accept(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF-8");
  }
}
