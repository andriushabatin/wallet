package com.batin.wallet;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository repository;
    private final WalletMapper mapper;

    public Wallet changeBalance(ChangeBalanceRequest request) {
        UUID walletId = request.getWalletId();
        Wallet wallet = repository.findById(walletId).get();

        int currBalance = wallet.getBalance();
        int amount = request.getAmount();

        switch (request.getOperationType()) {
            case DEPOSIT -> {
                wallet.setBalance(currBalance + amount);
            }
            case WITHDRAW -> {
                wallet.setBalance(currBalance - amount);
            }
        }
        return repository.save(wallet);
    }
    public WalletDto getBalanceByWalletId(UUID id) {
        Wallet wallet = repository.findById(id).get();
        return mapper.toDto(wallet);
    }

    public Wallet addWallet(WalletDto dto) {
        return repository.save(mapper.toWallet(dto));
    }

    public List<Wallet> getAllWallets() {
        return repository.findAll();
    }
}
