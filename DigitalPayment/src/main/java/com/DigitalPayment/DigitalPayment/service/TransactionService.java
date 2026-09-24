package com.DigitalPayment.DigitalPayment.service;

import com.DigitalPayment.DigitalPayment.dto.TransactionResponse;
import com.DigitalPayment.DigitalPayment.dto.TransferRequest;
import com.DigitalPayment.DigitalPayment.entity.AppUser;
import com.DigitalPayment.DigitalPayment.entity.Transaction;
import com.DigitalPayment.DigitalPayment.entity.Wallet;
import com.DigitalPayment.DigitalPayment.types.TransactionStatus;
import com.DigitalPayment.DigitalPayment.types.TransactionType;
import com.DigitalPayment.DigitalPayment.types.WalletStatus;
import com.DigitalPayment.DigitalPayment.exception.CustomException;
import com.DigitalPayment.DigitalPayment.repository.TransactionRepository;
import com.DigitalPayment.DigitalPayment.repository.WalletRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final WalletService walletService;

    @Transactional
    public TransactionResponse transferMoney(Long senderUserId, TransferRequest request) {
        if (request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new CustomException("Amount must be greater than zero");
        }

        Wallet senderWallet = walletRepository.findByUserId(senderUserId)
                .orElseThrow(() -> new CustomException("Sender wallet not found"));

        if (senderWallet.getStatus() == WalletStatus.BLOCKED) {
            throw new CustomException("Sender wallet is blocked");
        }

        AppUser receiverUser = walletService.findUserByIdentifier(request.receiverIdentifier().trim());
        if (receiverUser == null) {
            throw new CustomException("Receiver not found");
        }

        Wallet receiverWallet = receiverUser.getWallet();
        if (receiverWallet == null) {
            throw new CustomException("Receiver wallet not found");
        }
        if (receiverWallet.getStatus() == WalletStatus.BLOCKED) {
            throw new CustomException("Receiver wallet is blocked");
        }
        if (senderWallet.getId().equals(receiverWallet.getId())) {
            throw new CustomException("You cannot transfer money to yourself");
        }
        if (senderWallet.getBalance().compareTo(request.amount()) < 0) {
            throw new CustomException("Insufficient balance for transfer");
        }

        senderWallet.setBalance(senderWallet.getBalance().subtract(request.amount()));
        receiverWallet.setBalance(receiverWallet.getBalance().add(request.amount()));
        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);

        Transaction transaction = new Transaction();
        transaction.setReferenceNumber("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        transaction.setSenderWallet(senderWallet);
        transaction.setReceiverWallet(receiverWallet);
        transaction.setAmount(request.amount());
        transaction.setType(TransactionType.TRANSFER);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setDescription(request.description() == null || request.description().isBlank()
                ? "Transfer to " + receiverUser.getName() : request.description());

        transactionRepository.save(transaction);

        return mapToResponse(transaction);
    }

    public List<TransactionResponse> getHistory(Long userId) {
        return transactionRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        String senderAccount = transaction.getSenderWallet() != null ? transaction.getSenderWallet().getAccountNumber() : "SYSTEM";
        String receiverAccount = transaction.getReceiverWallet() != null ? transaction.getReceiverWallet().getAccountNumber() : "SYSTEM";

        return new TransactionResponse(
                transaction.getId(),
                transaction.getReferenceNumber(),
                transaction.getType().name(),
                transaction.getStatus().name(),
                transaction.getAmount(),
                transaction.getDescription(),
                senderAccount,
                receiverAccount,
                transaction.getCreatedAt()
        );
    }
}
