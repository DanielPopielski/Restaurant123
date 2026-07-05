package com.restaurant.dto;

import java.time.Instant;
import java.util.List;

public record OrderNotification(
        Long orderId,
        int tableNumber,
        String status,
        Instant createdAt,
        List<OrderItemNotification> items
) {}
