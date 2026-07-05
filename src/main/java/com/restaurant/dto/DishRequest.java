package com.restaurant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record DishRequest(
        @NotBlank String name,
        @NotNull @Positive BigDecimal price
) {}
