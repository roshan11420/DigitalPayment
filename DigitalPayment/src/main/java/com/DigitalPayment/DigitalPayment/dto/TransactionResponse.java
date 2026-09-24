package com.DigitalPayment.DigitalPayment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        String referenceNumber,
        String type,
        String status,
        BigDecimal amount,
        String description,
        String senderAccount,
        String receiverAccount,
        LocalDateTime createdAt
) {}
