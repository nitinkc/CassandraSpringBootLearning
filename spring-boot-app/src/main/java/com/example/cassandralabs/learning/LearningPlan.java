package com.example.cassandralabs.learning;

import java.util.List;

public class LearningPlan {
  private LearningPlan() {}

  public static List<LearningTopic> topics() {
    return List.of(
      new LearningTopic("01", "Keyspace basics", "replication strategy and schema setup"),
      new LearningTopic("02", "Partitioning and clustering", "primary key shape and data locality"),
      new LearningTopic("03", "Modeling by query", "denormalization for access patterns"),
      new LearningTopic("04", "Indexes and materialized views", "when (and when not) to use them"),
      new LearningTopic("05", "Consistency, LWT, and batching", "trade-offs and guarantees"),
      new LearningTopic("06", "TTL and tombstones", "storage/latency impact"),
      new LearningTopic("07", "Aggregation and filtering", "limits and server-side constraints")
    );
  }
}
