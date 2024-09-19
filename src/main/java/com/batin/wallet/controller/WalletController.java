package com.batin.wallet.controller;

import com.batin.wallet.annotation.RequestPerSecondLimited;
import com.batin.wallet.service.WalletService;
import com.batin.wallet.model.ChangeBalanceRequest;
import com.batin.wallet.model.Wallet;
import com.batin.wallet.model.WalletDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService service;

    @RequestPerSecondLimited(1000)
    @PostMapping
    public Wallet changeBalance(@Valid @RequestBody ChangeBalanceRequest request) {
        return service.changeBalance(request);
    }

    @RequestPerSecondLimited(1000)
    @GetMapping("/{uuid}")
    public WalletDto getBalanceByWalletId(@PathVariable UUID uuid) {
        return service.getBalanceByWalletId(uuid);
    }

    @RequestPerSecondLimited(1000)
    @PostMapping("/new")
    public Wallet addWallet(@Valid @RequestBody WalletDto walletDto) {
        return service.addWallet(walletDto);
    }

    @RequestPerSecondLimited(1000)
    @GetMapping("/all")
    public List<Wallet> getAllWallets() {
        return service.getAllWallets();
    }
}
