package com.example.cassandralabs.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;

@PrimaryKeyClass
public class UserByEmailKey implements Serializable {
  @PrimaryKeyColumn(name = "email", type = PrimaryKeyType.PARTITIONED)
  private String email;

  @PrimaryKeyColumn(name = "user_id", type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING)
  private UUID userId;

  public UserByEmailKey() {}

  public UserByEmailKey(String email, UUID userId) {
    this.email = email;
    this.userId = userId;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    UserByEmailKey that = (UserByEmailKey) other;
    return Objects.equals(email, that.email) && Objects.equals(userId, that.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(email, userId);
  }
}
