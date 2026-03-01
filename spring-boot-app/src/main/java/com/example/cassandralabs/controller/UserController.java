package com.example.cassandralabs.controller;

import com.example.cassandralabs.api.UserRequest;
import com.example.cassandralabs.model.UserByEmail;
import com.example.cassandralabs.model.UserById;
import com.example.cassandralabs.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User management APIs")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping
  @Operation(summary = "Create a user", description = "Creates a user and stores records by id and email")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "User created", content = @Content(schema = @Schema(implementation = UserById.class))),
      @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
  })
  public ResponseEntity<UserById> createUser(@RequestBody UserRequest request) {
    UserById created = userService.createUser(request.email(), request.fullName());
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @GetMapping("/{userId}")
  @Operation(summary = "Get user by id", description = "Returns the user document stored by id")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "User found", content = @Content(schema = @Schema(implementation = UserById.class))),
      @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
  })
  public ResponseEntity<UserById> getById(@PathVariable UUID userId) {
    Optional<UserById> user = userService.findById(userId);
    return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping("/by-email")
  @Operation(summary = "Get users by email", description = "Queries the email lookup table")
  @ApiResponse(responseCode = "200", description = "Users found", content = @Content(schema = @Schema(implementation = UserByEmail.class)))
  public List<UserByEmail> getByEmail(@RequestParam String email) {
    return userService.findByEmail(email);
  }
}
