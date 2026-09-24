package com.DigitalPayment.DigitalPayment.dto;

import java.math.BigDecimal;

public record UserProfileResponse(Long id, String name, String email, String mobile, String accountNumber, BigDecimal balance) {}
