package com.example.wallet_app.service;

import com.example.wallet_app.dto.WalletBalanceResponse;
import com.example.wallet_app.dto.WalletOperationRequest;
import com.example.wallet_app.dto.WalletOperationResponse;

import java.math.BigDecimal;
import java.util.UUID;

public interface WalletService {
    WalletOperationResponse processOperation(WalletOperationRequest request);
    WalletBalanceResponse getBalance(UUID walletId);
}