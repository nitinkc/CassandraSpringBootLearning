package com.example.cassandralabs.model;

import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("orders_by_user")
public class OrderByUser {
  @PrimaryKey
  private OrderByUserKey key;

  @Column("order_id")
  private UUID orderId;

  @Column("amount")
  private BigDecimal amount;

  @Column("status")
  private String status;

  public OrderByUser() {}

  public OrderByUser(OrderByUserKey key, UUID orderId, BigDecimal amount, String status) {
    this.key = key;
    this.orderId = orderId;
    this.amount = amount;
    this.status = status;
  }

  public OrderByUserKey getKey() {
    return key;
  }

  public void setKey(OrderByUserKey key) {
    this.key = key;
  }

  public UUID getOrderId() {
    return orderId;
  }

  public void setOrderId(UUID orderId) {
    this.orderId = orderId;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
