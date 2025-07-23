package com.example.wallet_app.dto;

import com.example.wallet_app.entity.OperationType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class WalletOperationRequest {

    private UUID walletId;
    private OperationType operationType;
    private BigDecimal amount;
}
