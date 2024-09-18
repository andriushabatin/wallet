package com.batin.wallet;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository repository;
    private final WalletMapper mapper;

    public Wallet changeBalance(ChangeBalanceRequest request) {
        UUID walletId = request.getWalletId();
        Wallet wallet = repository.findById(walletId)
                .orElseThrow(() -> new NoSuchElementException("Wallet with id '" + walletId + "' not found"));

        long currBalance = wallet.getBalance();
        long amount = request.getAmount();

        switch (request.getOperationType()) {
            case DEPOSIT -> {
                wallet.setBalance(currBalance + amount);
            }
            case WITHDRAW -> {
                long newBalance = currBalance - amount;
                if (newBalance >=0) {
                    wallet.setBalance(newBalance);
                } else throw new RuntimeException("Insufficient funds");
            }
        }

        return repository.save(wallet);
    }
    public WalletDto getBalanceByWalletId(UUID id) {
        Wallet wallet = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Wallet with id '" + id + "' not found"));

        return mapper.toDto(wallet);
    }

    public Wallet addWallet(WalletDto dto) {
        return repository.save(mapper.toWallet(dto));
    }

    public List<Wallet> getAllWallets() {
        return repository.findAll();
    }
}
