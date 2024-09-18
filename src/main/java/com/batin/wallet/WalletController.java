package com.batin.wallet;

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

    @PostMapping
    public Wallet changeBalance(@Valid @RequestBody ChangeBalanceRequest request) {
        return service.changeBalance(request);
    }

    @GetMapping("/{uuid}")
    public WalletDto getBalanceByWalletId(@PathVariable UUID uuid) {
        return service.getBalanceByWalletId(uuid);
    }

    @PostMapping("/new")
    public Wallet addWallet(@Valid @RequestBody WalletDto walletDto) {
        return service.addWallet(walletDto);
    }

    @GetMapping("/all")
    public List<Wallet> getAllWallets() {
        return service.getAllWallets();
    }
}
