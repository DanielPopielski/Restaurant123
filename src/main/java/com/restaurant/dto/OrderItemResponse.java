package com.restaurant.dto;

import java.math.BigDecimal;

public record OrderItemResponse(Long dishId, String dishName, int quantity, BigDecimal price) {}
