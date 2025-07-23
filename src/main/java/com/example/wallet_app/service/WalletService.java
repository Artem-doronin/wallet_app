package com.example.wallet_app.service;

import com.example.wallet_app.dto.WalletOperationRequest;
import com.example.wallet_app.dto.WalletResponse;

import java.util.UUID;

public interface WalletService {
    WalletResponse processOperation(WalletOperationRequest request);
    WalletResponse getBalance(UUID walletId);
}