package com.batin.wallet;

import org.springframework.stereotype.Component;

@Component
public class WalletMapper {

    public WalletDto toDto(Wallet wallet) {
        return WalletDto.builder()
                .balance(wallet.getBalance())
                .build();
    }

    public Wallet toWallet(WalletDto dto) {
        return Wallet.builder()
                .balance(dto.getBalance())
                .build();
    }
}