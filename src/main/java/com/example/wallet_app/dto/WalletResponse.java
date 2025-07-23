package com.example.wallet_app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class WalletResponse {
    private UUID walletId;
    private BigDecimal balance;
}

