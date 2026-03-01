package com.example.cassandralabs;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.cassandralabs.learning.LearningPlan;
import com.example.cassandralabs.learning.LearningTopic;
import java.util.List;
import org.junit.jupiter.api.Test;

public class LearningPlanTest {
  @Test
  void learningPlanHasCoreTopics() {
    List<LearningTopic> topics = LearningPlan.topics();
    assertThat(topics).hasSizeGreaterThan(5);
    assertThat(topics.get(0).id()).isEqualTo("01");
  }
}
