package com.example.cassandralabs.repository;

import com.example.cassandralabs.model.UserByEmail;
import com.example.cassandralabs.model.UserByEmailKey;
import java.util.List;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface UserByEmailRepository extends CassandraRepository<UserByEmail, UserByEmailKey> {
  List<UserByEmail> findByKeyEmail(String email);
}
