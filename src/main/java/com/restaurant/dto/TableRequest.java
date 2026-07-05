package com.restaurant.dto;

import jakarta.validation.constraints.Positive;

public record TableRequest(
        @Positive int tableNumber,
        @Positive int seats
) {}
