package com.example.wallet_app.service;

import com.example.wallet_app.dto.WalletOperationRequest;
import com.example.wallet_app.dto.WalletResponse;
import com.example.wallet_app.entity.OperationType;
import com.example.wallet_app.entity.Wallet;
import com.example.wallet_app.exception.InsufficientFundsException;
import com.example.wallet_app.exception.WalletNotFoundException;
import com.example.wallet_app.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    @Override
    @Transactional(readOnly = true)
    public WalletResponse getBalance(UUID walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException(walletId));
        return new WalletResponse(wallet.getId(), wallet.getBalance());
    }

    @Override
    @Retryable(retryFor = ObjectOptimisticLockingFailureException.class, maxAttempts = 5)
    @Transactional
    public WalletResponse processOperation(WalletOperationRequest request) {
        Wallet wallet = walletRepository.findByIdForUpdate(request.getWalletId())
                .orElseThrow(() -> new WalletNotFoundException(request.getWalletId()));

        BigDecimal newBalance;
        if (request.getOperationType() == OperationType.DEPOSIT) {
            newBalance = wallet.getBalance().add(request.getAmount());
        } else {
            if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
                throw new InsufficientFundsException(request.getWalletId());
            }
            newBalance = wallet.getBalance().subtract(request.getAmount());
        }

        wallet.setBalance(newBalance);
        wallet = walletRepository.save(wallet);

        return new WalletResponse(wallet.getId(), wallet.getBalance());
    }
}