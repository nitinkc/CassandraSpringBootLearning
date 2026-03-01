package com.example.cassandralabs.model;

import java.time.Instant;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("users_by_email")
public class UserByEmail {
  @PrimaryKey
  private UserByEmailKey key;

  @Column("full_name")
  private String fullName;

  @Column("created_at")
  private Instant createdAt;

  public UserByEmail() {}

  public UserByEmail(UserByEmailKey key, String fullName, Instant createdAt) {
    this.key = key;
    this.fullName = fullName;
    this.createdAt = createdAt;
  }

  public UserByEmailKey getKey() {
    return key;
  }

  public void setKey(UserByEmailKey key) {
    this.key = key;
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
