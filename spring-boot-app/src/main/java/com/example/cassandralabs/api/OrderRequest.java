package com.example.cassandralabs.api;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderRequest(UUID userId, BigDecimal amount, String status) {}
