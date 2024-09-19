package com.batin.wallet;

import com.batin.wallet.controller.WalletController;
import com.batin.wallet.enums.OperationType;
import com.batin.wallet.model.ChangeBalanceRequest;
import com.batin.wallet.model.Wallet;
import com.batin.wallet.model.WalletDto;
import com.batin.wallet.service.WalletService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WalletController.class)
public class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WalletService walletService;

    private Wallet wallet;
    private WalletDto walletDto;
    private ChangeBalanceRequest changeBalanceRequest;

    @BeforeEach
    void setUp() {
        wallet = Wallet.builder()
                .id(UUID.randomUUID())
                .balance(100.0)
                .build();

        walletDto = WalletDto.builder()
                .balance(wallet.getBalance())
                .build();

        changeBalanceRequest = ChangeBalanceRequest.builder()
                .walletId(wallet.getId())
                .operationType(OperationType.DEPOSIT)
                .amount(50.5)
                .build();
    }

    @Test
    void testChangeBalance() throws Exception {
        when(walletService.changeBalance(any(ChangeBalanceRequest.class))).thenReturn(wallet);

        mockMvc.perform(post("/api/v1/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeBalanceRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(wallet.getId().toString()))
                .andExpect(jsonPath("$.balance").value(wallet.getBalance()));
    }

    @Test
    void testChangeBalanceWhenWalletNotFound() throws Exception {
        when(walletService.changeBalance(any(ChangeBalanceRequest.class)))
                .thenThrow(new NoSuchElementException("Wallet not found"));

        mockMvc.perform(post("/api/v1/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeBalanceRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetBalanceByWalletId() throws Exception {
        when(walletService.getBalanceByWalletId(wallet.getId())).thenReturn(walletDto);

        mockMvc.perform(get("/api/v1/wallets/{uuid}", wallet.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(walletDto.getBalance()));
    }

    @Test
    void testGetBalanceByWalletId_WalletNotFound() throws Exception {
        when(walletService.getBalanceByWalletId(wallet.getId()))
                .thenThrow(new NoSuchElementException("Wallet not found"));

        mockMvc.perform(get("/api/v1/wallets/{uuid}", wallet.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAddWallet() throws Exception {
        when(walletService.addWallet(any(WalletDto.class))).thenReturn(wallet);

        mockMvc.perform(post("/api/v1/wallets/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(walletDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(wallet.getId().toString()))
                .andExpect(jsonPath("$.balance").value(wallet.getBalance()));
    }

    @Test
    void testGetAllWallets() throws Exception {
        when(walletService.getAllWallets()).thenReturn(Collections.singletonList(wallet));

        mockMvc.perform(get("/api/v1/wallets/all")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(wallet.getId().toString()))
                .andExpect(jsonPath("$[0].balance").value(wallet.getBalance()));
    }
}
