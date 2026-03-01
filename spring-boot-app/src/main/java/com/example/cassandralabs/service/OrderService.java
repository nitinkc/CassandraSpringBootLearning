package com.example.cassandralabs.service;

import com.example.cassandralabs.model.OrderByUser;
import com.example.cassandralabs.model.OrderByUserKey;
import com.example.cassandralabs.repository.OrderByUserRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
  private final OrderByUserRepository orderByUserRepository;

  public OrderService(OrderByUserRepository orderByUserRepository) {
    this.orderByUserRepository = orderByUserRepository;
  }

  public OrderByUser createOrder(UUID userId, BigDecimal amount, String status) {
    OrderByUserKey key = new OrderByUserKey(userId, Instant.now());
    OrderByUser order = new OrderByUser(key, UUID.randomUUID(), amount, status);
    return orderByUserRepository.save(order);
  }

  public List<OrderByUser> findByUserId(UUID userId) {
    return orderByUserRepository.findByKeyUserId(userId);
  }
}
