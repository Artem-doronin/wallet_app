package com.example.wallet_app;

import com.example.wallet_app.controller.WalletController;
import com.example.wallet_app.dto.WalletOperationRequest;
import com.example.wallet_app.dto.WalletResponse;
import com.example.wallet_app.entity.OperationType;
import com.example.wallet_app.exception.GlobalExceptionHandler;
import com.example.wallet_app.exception.InsufficientFundsException;
import com.example.wallet_app.exception.WalletNotFoundException;
import com.example.wallet_app.service.WalletServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest
@ContextConfiguration(classes = WalletController.class)
@Import(GlobalExceptionHandler.class)
public class WalletControllerMvcTest {
    private static final String BASE_URL = "/api/v1/wallets";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WalletServiceImpl walletService;

    private final UUID testWalletId = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11");
    private final BigDecimal testBalance = new BigDecimal("1000.00");

    @Test
    void processWithdrawOperation_shouldReturnUpdatedBalance() throws Exception {
        WalletOperationRequest request = new WalletOperationRequest(
                testWalletId,
                OperationType.WITHDRAW,
                new BigDecimal("200.00")
        );

        WalletResponse response = new WalletResponse(testWalletId, new BigDecimal("800.00"));
        when(walletService.processOperation(any(WalletOperationRequest.class))).thenReturn(response);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.walletId").value(testWalletId.toString()))
                .andExpect(jsonPath("$.balance").value(800.00));
    }

    @Test
    void getWalletBalance_shouldReturnBalance() throws Exception {
        WalletResponse response = new WalletResponse(testWalletId, testBalance);
        when(walletService.getBalance(testWalletId)).thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/{walletId}", testWalletId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.walletId").value(testWalletId.toString()))
                .andExpect(jsonPath("$.balance").value(1000.00));
    }

    @Test
    void processDepositOperation_shouldReturnUpdatedBalance() throws Exception {
        WalletOperationRequest request = new WalletOperationRequest(
                testWalletId,
                OperationType.DEPOSIT,
                new BigDecimal("500.50")
        );

        WalletResponse response = new WalletResponse(testWalletId, new BigDecimal("1500.50"));
        when(walletService.processOperation(any(WalletOperationRequest.class))).thenReturn(response);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.walletId").value(testWalletId.toString()))
                .andExpect(jsonPath("$.balance").value(1500.50));
    }

    @Test
    void getWalletBalance_whenWalletNotFound_shouldThrowException() throws Exception {
        UUID walletId = UUID.randomUUID();
        when(walletService.getBalance(walletId))
                .thenThrow(new WalletNotFoundException(walletId));

        mockMvc.perform(get(BASE_URL + "/{walletId}", walletId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString(walletId.toString())));
    }

    @Test
    void processWithdrawOperation_whenInsufficientFunds_shouldReturnBadRequest() throws Exception {
        WalletOperationRequest request = new WalletOperationRequest(
                testWalletId,
                OperationType.WITHDRAW,
                new BigDecimal("2000.00")
        );

        when(walletService.processOperation(any(WalletOperationRequest.class)))
                .thenThrow(new InsufficientFundsException(testWalletId));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Insufficient funds")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}