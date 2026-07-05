package com.restaurant.dto;

import java.math.BigDecimal;

public record DishResponse(Long id, String name, BigDecimal price) {}
