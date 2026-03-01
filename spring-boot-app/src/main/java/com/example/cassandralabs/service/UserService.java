package com.example.cassandralabs.service;

import com.example.cassandralabs.model.UserByEmail;
import com.example.cassandralabs.model.UserByEmailKey;
import com.example.cassandralabs.model.UserById;
import com.example.cassandralabs.repository.UserByEmailRepository;
import com.example.cassandralabs.repository.UserByIdRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.cassandra.core.CassandraBatchOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  private final UserByIdRepository userByIdRepository;
  private final UserByEmailRepository userByEmailRepository;
  private final CassandraTemplate cassandraTemplate;

  public UserService(UserByIdRepository userByIdRepository,
      UserByEmailRepository userByEmailRepository,
      CassandraTemplate cassandraTemplate) {
    this.userByIdRepository = userByIdRepository;
    this.userByEmailRepository = userByEmailRepository;
    this.cassandraTemplate = cassandraTemplate;
  }

  public UserById createUser(String email, String fullName) {
    UUID userId = UUID.randomUUID();
    Instant createdAt = Instant.now();

    UserById userById = new UserById(userId, email, fullName, createdAt);
    UserByEmail userByEmail = new UserByEmail(new UserByEmailKey(email, userId), fullName, createdAt);

    CassandraBatchOperations batchOps = cassandraTemplate.batchOps();
    batchOps.insert(userById);
    batchOps.insert(userByEmail);
    batchOps.execute();

    return userById;
  }

  public Optional<UserById> findById(UUID userId) {
    return userByIdRepository.findById(userId);
  }

  public List<UserByEmail> findByEmail(String email) {
    return userByEmailRepository.findByKeyEmail(email);
  }
}
