package com.example.wallet_app.service;

import com.example.wallet_app.dto.WalletBalanceResponse;
import com.example.wallet_app.dto.WalletOperationRequest;
import com.example.wallet_app.dto.WalletOperationResponse;
import com.example.wallet_app.entity.OperationType;
import com.example.wallet_app.entity.Wallet;
import com.example.wallet_app.exception.InsufficientFundsException;
import com.example.wallet_app.repository.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    @Retryable(
            value = {OptimisticLockException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 50))
    @Transactional
    @Override
    public WalletOperationResponse processOperation(WalletOperationRequest request) {
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Amount must be positive");

        Wallet wallet = walletRepository.findById(request.getWalletId())
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));
        BigDecimal newBalance;
        if (request.getOperationType() == OperationType.DEPOSIT) {
            newBalance = wallet.getBalance().add(request.getAmount());
        } else {
            if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
                throw new InsufficientFundsException("Not enough balance");
            }
            newBalance = wallet.getBalance().subtract(request.getAmount());
        }
        wallet.setBalance(newBalance);
        walletRepository.save(wallet);

        WalletOperationResponse response = new WalletOperationResponse();
        response.setWalletId(wallet.getId());
        response.setBalance(wallet.getBalance());
        response.setOperationType(request.getOperationType());
        response.setAmount(request.getAmount());
        response.setSuccess(true);

        return response;
    }


        @Override
        public WalletBalanceResponse getBalance (UUID walletId){
            Wallet wallet = walletRepository.findById(walletId)
                    .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));
            WalletBalanceResponse response = new WalletBalanceResponse();
            response.setBalance(wallet.getBalance());
            return response;
        }
    }

