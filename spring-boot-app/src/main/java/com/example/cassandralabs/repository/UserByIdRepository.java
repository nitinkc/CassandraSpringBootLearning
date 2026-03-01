package com.example.cassandralabs.repository;

import com.example.cassandralabs.model.UserById;
import java.util.UUID;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface UserByIdRepository extends CassandraRepository<UserById, UUID> {}
