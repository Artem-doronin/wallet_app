package com.example.wallet_app.controller;

import com.example.wallet_app.dto.WalletOperationRequest;
import com.example.wallet_app.dto.WalletResponse;
import com.example.wallet_app.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
@Slf4j
public class WalletController {

    private final WalletService walletService;

    @PostMapping
    public ResponseEntity<WalletResponse> processWalletOperation(
            @RequestBody @Valid WalletOperationRequest request) {
        log.info("Processing wallet operation: {}", request);
        try {
            WalletResponse response = walletService.processOperation(request);
            log.debug("Operation completed successfully for wallet: {}", request.getWalletId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error processing operation for wallet {}: {}", request.getWalletId(), e.getMessage());
            throw e;
        }
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<WalletResponse> getWalletBalance(
            @PathVariable UUID walletId) {
        log.info("Getting balance for wallet: {}", walletId);
        WalletResponse response = walletService.getBalance(walletId);
        log.debug("Balance retrieved for wallet {}: {}", walletId, response.getBalance());
        return ResponseEntity.ok(response);
    }
}
