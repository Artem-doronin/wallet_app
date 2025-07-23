package com.example.wallet_app.controller;

import com.example.wallet_app.dto.WalletOperationRequest;
import com.example.wallet_app.dto.WalletResponse;
import com.example.wallet_app.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
public class WalletController {

    private final WalletService walletService;

    @PostMapping
    public ResponseEntity<WalletResponse> processWalletOperation(
            @RequestBody @Valid WalletOperationRequest request) {
        WalletResponse response = walletService.processOperation(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<WalletResponse> getWalletBalance(
            @PathVariable UUID walletId) {
        WalletResponse response = walletService.getBalance(walletId);
        return ResponseEntity.ok(response);
    }
}
