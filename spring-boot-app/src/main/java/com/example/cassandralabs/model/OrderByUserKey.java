package com.example.cassandralabs.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;

@PrimaryKeyClass
public class OrderByUserKey implements Serializable {
  @PrimaryKeyColumn(name = "user_id", type = PrimaryKeyType.PARTITIONED)
  private UUID userId;

  @PrimaryKeyColumn(name = "order_ts", type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
  private Instant orderTs;

  public OrderByUserKey() {}

  public OrderByUserKey(UUID userId, Instant orderTs) {
    this.userId = userId;
    this.orderTs = orderTs;
  }

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public Instant getOrderTs() {
    return orderTs;
  }

  public void setOrderTs(Instant orderTs) {
    this.orderTs = orderTs;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    OrderByUserKey that = (OrderByUserKey) other;
    return Objects.equals(userId, that.userId) && Objects.equals(orderTs, that.orderTs);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, orderTs);
  }
}
