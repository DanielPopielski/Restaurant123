package com.restaurant.dto;

public record AuthenticationResponse(String token, String username, String role) {}
