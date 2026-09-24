package com.DigitalPayment.DigitalPayment.repository;

import com.DigitalPayment.DigitalPayment.entity.Wallet;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUserId(Long userId);

    Optional<Wallet> findByAccountNumber(String accountNumber);
}
