package com.DigitalPayment.DigitalPayment.controller;

import com.DigitalPayment.DigitalPayment.dto.TransactionResponse;
import com.DigitalPayment.DigitalPayment.dto.TransferRequest;
import com.DigitalPayment.DigitalPayment.entity.AppUser;
import com.DigitalPayment.DigitalPayment.repository.UserRepository;
import com.DigitalPayment.DigitalPayment.service.TransactionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TransactionController {
    private final TransactionService transactionService;
    private final UserRepository userRepository;

    public TransactionController(TransactionService transactionService, UserRepository userRepository) {
        this.transactionService = transactionService;
        this.userRepository = userRepository;
    }

    @PostMapping("/transactions/transfer")
    public ResponseEntity<TransactionResponse> transferMoney(@AuthenticationPrincipal UserDetails userDetails,
                                                           @Valid @RequestBody TransferRequest request) {
        AppUser user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(transactionService.transferMoney(user.getId(), request));
    }

    @GetMapping("/transactions/history")
    public ResponseEntity<List<TransactionResponse>> getHistory(@AuthenticationPrincipal UserDetails userDetails) {
        AppUser user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(transactionService.getHistory(user.getId()));
    }
}
