package com.example.wallet_app.service;

import com.example.wallet_app.dto.WalletOperationRequest;
import com.example.wallet_app.dto.WalletResponse;
import com.example.wallet_app.entity.OperationType;
import com.example.wallet_app.entity.Wallet;
import com.example.wallet_app.exception.InsufficientFundsException;
import com.example.wallet_app.exception.WalletNotFoundException;
import com.example.wallet_app.properties.RetryProperties;
import com.example.wallet_app.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(RetryProperties.class)
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    @Override
    @Transactional(readOnly = true)
    public WalletResponse getBalance(UUID walletId) {
        log.debug("Fetching balance for wallet {}", walletId);
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> {
                    log.warn("Wallet not found when fetching balance: {}", walletId);
                    return new WalletNotFoundException(walletId);
                });
        return new WalletResponse(wallet.getId(), wallet.getBalance());
    }

    @Override
    @Retryable(
            retryFor = ObjectOptimisticLockingFailureException.class,
            maxAttemptsExpression = "#{@retryProperties.maxAttempts}",
            backoff = @Backoff(delayExpression = "#{@retryProperties.delay}")
    )
    @Transactional
    public WalletResponse processOperation(WalletOperationRequest request) {
        log.debug("Starting operation processing for wallet {}", request.getWalletId());
        Wallet wallet = walletRepository.findByIdForUpdate(request.getWalletId())
                .orElseThrow(() -> {
                    log.warn("Wallet not found: {}", request.getWalletId());
                    return new WalletNotFoundException(request.getWalletId());
                });

        BigDecimal newBalance;
        if (request.getOperationType() == OperationType.DEPOSIT) {
            log.debug("Processing deposit of {} to wallet {}", request.getAmount(), request.getWalletId());
            newBalance = wallet.getBalance().add(request.getAmount());
        } else {
            log.debug("Processing withdraw of {} from wallet {}", request.getAmount(), request.getWalletId());
            if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
                log.warn("Insufficient funds in wallet {}", request.getWalletId());
                throw new InsufficientFundsException(request.getWalletId());
            }
            newBalance = wallet.getBalance().subtract(request.getAmount());
        }

        wallet.setBalance(newBalance);
        wallet = walletRepository.save(wallet);

        log.info("Successfully processed {} operation for wallet {}. New balance: {}",
                request.getOperationType(), request.getWalletId(), newBalance);

        return new WalletResponse(wallet.getId(), wallet.getBalance());
    }
}