package com.DigitalPayment.DigitalPayment.dto;

import java.math.BigDecimal;

public record WalletResponse(Long id, String accountNumber, BigDecimal balance, String status) {}
