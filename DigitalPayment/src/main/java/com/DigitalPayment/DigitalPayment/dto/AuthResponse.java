package com.DigitalPayment.DigitalPayment.dto;

public record AuthResponse(String token, String message, UserProfileResponse user) {}
