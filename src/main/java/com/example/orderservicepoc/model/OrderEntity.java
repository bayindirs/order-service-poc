package com.example.orderservicepoc.model;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class OrderEntity {

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid2")
  private String id;

  private String customerId;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderItem> items;

  private Date orderDate;

  private Date cancelDate;

  private Date completeDate;

  @Enumerated(EnumType.STRING)
  private OrderStatus status;
}
