package com.restaurant.dto;

import com.restaurant.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        Long tableId,
        OrderStatus status,
        BigDecimal totalPrice,
        Instant createdAt,
        List<OrderItemResponse> items
) {}
