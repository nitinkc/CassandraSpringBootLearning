package com.example.cassandralabs.controller;

import com.example.cassandralabs.learning.LearningPlan;
import com.example.cassandralabs.learning.LearningTopic;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/learning-plan")
@Tag(name = "Learning Plan", description = "Learning plan topics")
public class LearningPlanController {
  @GetMapping
  @Operation(summary = "List learning topics", description = "Returns the ordered list of Cassandra learning topics")
  public List<LearningTopic> getLearningPlan() {
    return LearningPlan.topics();
  }
}
