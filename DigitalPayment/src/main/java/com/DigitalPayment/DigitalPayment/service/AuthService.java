package com.DigitalPayment.DigitalPayment.service;

import com.DigitalPayment.DigitalPayment.dto.AuthResponse;
import com.DigitalPayment.DigitalPayment.dto.LoginRequest;
import com.DigitalPayment.DigitalPayment.dto.RegisterRequest;
import com.DigitalPayment.DigitalPayment.dto.UserProfileResponse;
import com.DigitalPayment.DigitalPayment.entity.AppUser;
import com.DigitalPayment.DigitalPayment.entity.Wallet;
import com.DigitalPayment.DigitalPayment.types.WalletStatus;
import com.DigitalPayment.DigitalPayment.exception.CustomException;
import com.DigitalPayment.DigitalPayment.repository.UserRepository;
import com.DigitalPayment.DigitalPayment.repository.WalletRepository;
import com.DigitalPayment.DigitalPayment.security.JwtTokenProvider;
import java.math.BigDecimal;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        String mobile = request.mobile().trim();

        if (userRepository.existsByEmail(email)) {
            throw new CustomException("Email already registered");
        }
        if (userRepository.existsByMobile(mobile)) {
            throw new CustomException("Mobile number already registered");
        }

        AppUser user = new AppUser();
        user.setName(request.name().trim());
        user.setEmail(email);
        user.setMobile(mobile);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setStatus(WalletStatus.ACTIVE);

        AppUser savedUser = userRepository.save(user);

        Wallet wallet = new Wallet();
        wallet.setUser(savedUser);
        wallet.setAccountNumber(generateAccountNumber());
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setStatus(WalletStatus.ACTIVE);
        walletRepository.save(wallet);
        savedUser.setWallet(wallet);

        String token = jwtTokenProvider.generateToken(savedUser.getEmail());
        return new AuthResponse(token, "Registration successful", mapUser(savedUser, wallet));
    }

    public AuthResponse login(LoginRequest request) {
        AppUser user = userRepository.findByEmail(request.email().trim().toLowerCase())
                .orElseThrow(() -> new CustomException("User not found"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new CustomException("Invalid email or password");
        }

        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new CustomException("Wallet not found for user"));

        String token = jwtTokenProvider.generateToken(user.getEmail());
        return new AuthResponse(token, "Login successful", mapUser(user, wallet));
    }

    public UserProfileResponse getUserProfile(String email) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found"));
        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new CustomException("Wallet not found for user"));
        return mapUser(user, wallet);
    }

    private UserProfileResponse mapUser(AppUser user, Wallet wallet) {
        return new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getMobile(),
                wallet.getAccountNumber(),
                wallet.getBalance()
        );
    }

    private String generateAccountNumber() {
        Random random = new Random();
        int randomNumber = 10000000 + random.nextInt(90000000);
        return "100" + randomNumber;
    }
}
