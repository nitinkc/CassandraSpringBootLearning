package com.example.cassandralabs.model;

import java.time.Instant;
import java.util.UUID;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("users_by_id")
public class UserById {
  @PrimaryKeyColumn(name = "user_id", type = PrimaryKeyType.PARTITIONED)
  private UUID userId;

  @Column("email")
  private String email;

  @Column("full_name")
  private String fullName;

  @Column("created_at")
  private Instant createdAt;

  public UserById() {}

  public UserById(UUID userId, String email, String fullName, Instant createdAt) {
    this.userId = userId;
    this.email = email;
    this.fullName = fullName;
    this.createdAt = createdAt;
  }

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
}
