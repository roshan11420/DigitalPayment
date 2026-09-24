package com.DigitalPayment.DigitalPayment.controller;

import com.DigitalPayment.DigitalPayment.dto.WalletRequest;
import com.DigitalPayment.DigitalPayment.dto.WalletResponse;
import com.DigitalPayment.DigitalPayment.entity.AppUser;
import com.DigitalPayment.DigitalPayment.repository.UserRepository;
import com.DigitalPayment.DigitalPayment.service.WalletService;
import jakarta.validation.Valid;
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
public class WalletController {
    private final WalletService walletService;
    private final UserRepository userRepository;

    public WalletController(WalletService walletService, UserRepository userRepository) {
        this.walletService = walletService;
        this.userRepository = userRepository;
    }

    @GetMapping("/wallet")
    public ResponseEntity<WalletResponse> getWallet(@AuthenticationPrincipal UserDetails userDetails) {
        AppUser user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(walletService.getWallet(user.getId()));
    }

    @PostMapping("/wallet/credit")
    public ResponseEntity<WalletResponse> creditMoney(@AuthenticationPrincipal UserDetails userDetails,
                                                     @Valid @RequestBody WalletRequest request) {
        AppUser user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(walletService.creditMoney(user.getId(), request));
    }

    @PostMapping("/wallet/debit")
    public ResponseEntity<WalletResponse> debitMoney(@AuthenticationPrincipal UserDetails userDetails,
                                                    @Valid @RequestBody WalletRequest request) {
        AppUser user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(walletService.debitMoney(user.getId(), request));
    }
}
