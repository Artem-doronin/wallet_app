package com.example.wallet_app.dto;

import com.example.wallet_app.entity.OperationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletOperationResponse {
    private UUID walletId;
    private BigDecimal balance;
    private OperationType operationType;
    private BigDecimal amount;
    private boolean success;
    private String message;
}

