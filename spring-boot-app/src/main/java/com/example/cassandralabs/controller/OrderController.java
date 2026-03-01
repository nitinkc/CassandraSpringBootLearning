package com.example.cassandralabs.controller;

import com.example.cassandralabs.api.OrderRequest;
import com.example.cassandralabs.model.OrderByUser;
import com.example.cassandralabs.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Order management APIs")
public class OrderController {
  private final OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @PostMapping
  @Operation(summary = "Create an order", description = "Creates an order for a user")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Order created", content = @Content(schema = @Schema(implementation = OrderByUser.class))),
      @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
  })
  public ResponseEntity<OrderByUser> createOrder(@RequestBody OrderRequest request) {
    OrderByUser created = orderService.createOrder(request.userId(), request.amount(), request.status());
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @GetMapping("/{userId}")
  @Operation(summary = "List user orders", description = "Returns all orders for the given user id")
  @ApiResponse(responseCode = "200", description = "Orders returned", content = @Content(schema = @Schema(implementation = OrderByUser.class)))
  public List<OrderByUser> getOrders(@PathVariable UUID userId) {
    return orderService.findByUserId(userId);
  }
}
