package com.batin.wallet.service;

import com.batin.wallet.mapper.WalletMapper;
import com.batin.wallet.repository.WalletRepository;
import com.batin.wallet.model.ChangeBalanceRequest;
import com.batin.wallet.model.Wallet;
import com.batin.wallet.model.WalletDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository repository;
    private final WalletMapper mapper;

    @Transactional
    public Wallet changeBalance(ChangeBalanceRequest request) {
        UUID walletId = request.getWalletId();
        Wallet wallet = repository.findById(walletId)
                .orElseThrow(() -> new NoSuchElementException("Wallet with id '" + walletId + "' not found"));

        double currBalance = wallet.getBalance();
        double amount = request.getAmount();

        switch (request.getOperationType()) {
            case DEPOSIT -> {
                wallet.setBalance(currBalance + amount);
            }
            case WITHDRAW -> {
                double newBalance = currBalance - amount;
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
