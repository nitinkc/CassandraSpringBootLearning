package com.example.cassandralabs.repository;

import com.example.cassandralabs.model.OrderByUser;
import com.example.cassandralabs.model.OrderByUserKey;
import java.util.List;
import java.util.UUID;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface OrderByUserRepository extends CassandraRepository<OrderByUser, OrderByUserKey> {
  List<OrderByUser> findByKeyUserId(UUID userId);
}
