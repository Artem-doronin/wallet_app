package com.example.wallet_app.controller;

import com.example.wallet_app.dto.WalletBalanceResponse;
import com.example.wallet_app.dto.WalletOperationRequest;
import com.example.wallet_app.dto.WalletOperationResponse;
import com.example.wallet_app.service.WalletServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletServiceImpl walletService;

    @PostMapping
    public ResponseEntity<WalletOperationResponse> processOperation(
            @Validated @RequestBody WalletOperationRequest request) {
        return ResponseEntity.ok(walletService.processOperation(request));
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<WalletBalanceResponse> getBalance(
            @PathVariable UUID walletId) {
        return ResponseEntity.ok(walletService.getBalance(walletId));
    }
}