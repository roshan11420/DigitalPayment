package com.DigitalPayment.DigitalPayment.service;

import com.DigitalPayment.DigitalPayment.dto.WalletRequest;
import com.DigitalPayment.DigitalPayment.dto.WalletResponse;
import com.DigitalPayment.DigitalPayment.entity.AppUser;
import com.DigitalPayment.DigitalPayment.entity.Transaction;
import com.DigitalPayment.DigitalPayment.entity.Wallet;
import com.DigitalPayment.DigitalPayment.types.TransactionStatus;
import com.DigitalPayment.DigitalPayment.types.TransactionType;
import com.DigitalPayment.DigitalPayment.types.WalletStatus;
import com.DigitalPayment.DigitalPayment.exception.CustomException;
import com.DigitalPayment.DigitalPayment.repository.TransactionRepository;
import com.DigitalPayment.DigitalPayment.repository.UserRepository;
import com.DigitalPayment.DigitalPayment.repository.WalletRepository;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public WalletResponse getWallet(Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException("Wallet not found for user"));
        return mapToResponse(wallet);
    }

    @Transactional
    public WalletResponse creditMoney(Long userId, WalletRequest request) {
        validateAmount(request.amount());

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException("Wallet not found for user"));
        if (wallet.getStatus() == WalletStatus.BLOCKED) {
            throw new CustomException("Wallet is blocked. Please contact support");
        }

        wallet.setBalance(wallet.getBalance().add(request.amount()));
        walletRepository.save(wallet);

        createTransaction(wallet, null, request.amount(), TransactionType.CREDIT,
                request.description() == null || request.description().isBlank() ? "Wallet credited" : request.description());

        return mapToResponse(wallet);
    }

    @Transactional
    public WalletResponse debitMoney(Long userId, WalletRequest request) {
        validateAmount(request.amount());

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException("Wallet not found for user"));
        if (wallet.getStatus() == WalletStatus.BLOCKED) {
            throw new CustomException("Wallet is blocked. Please contact support");
        }
        if (wallet.getBalance().compareTo(request.amount()) < 0) {
            throw new CustomException("Insufficient wallet balance");
        }

        wallet.setBalance(wallet.getBalance().subtract(request.amount()));
        walletRepository.save(wallet);

        createTransaction(wallet, null, request.amount(), TransactionType.DEBIT,
                request.description() == null || request.description().isBlank() ? "Wallet debited" : request.description());

        return mapToResponse(wallet);
    }

    public AppUser findUserByIdentifier(String identifier) {
        AppUser user = userRepository.findByMobile(identifier).orElse(null);
        if (user == null) {
            user = userRepository.findByEmail(identifier).orElse(null);
        }
        if (user == null) {
            Wallet wallet = walletRepository.findByAccountNumber(identifier).orElse(null);
            if (wallet != null) {
                user = wallet.getUser();
            }
        }
        return user;
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CustomException("Amount must be greater than zero");
        }
    }

    private void createTransaction(Wallet senderWallet, Wallet receiverWallet, BigDecimal amount,
                                  TransactionType type, String description) {
        Transaction transaction = new Transaction();
        transaction.setReferenceNumber("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        transaction.setSenderWallet(senderWallet);
        transaction.setReceiverWallet(receiverWallet);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setDescription(description);
        transactionRepository.save(transaction);
    }

    private WalletResponse mapToResponse(Wallet wallet) {
        return new WalletResponse(
                wallet.getId(),
                wallet.getAccountNumber(),
                wallet.getBalance(),
                wallet.getStatus().name()
        );
    }
}
