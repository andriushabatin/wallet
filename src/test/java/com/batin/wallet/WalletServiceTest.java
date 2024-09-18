package com.batin.wallet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletMapper walletMapper;

    @InjectMocks
    private WalletService walletService;

    private Wallet wallet;
    private WalletDto walletDto;
    private ChangeBalanceRequest changeBalanceRequest;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

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
    void testDepositChangeBalance() {
        when(walletRepository.findById(wallet.getId())).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        Wallet updatedWallet = walletService.changeBalance(changeBalanceRequest);

        assertNotNull(updatedWallet);
        assertEquals(100.0 + 50.5, updatedWallet.getBalance());
        verify(walletRepository, times(1)).save(wallet);
    }

    @Test
    void testWithdrawChangeBalance() {
        changeBalanceRequest.setOperationType(OperationType.WITHDRAW);
        changeBalanceRequest.setAmount(50.5);

        when(walletRepository.findById(wallet.getId())).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        Wallet updatedWallet = walletService.changeBalance(changeBalanceRequest);

        assertNotNull(updatedWallet);
        assertEquals(100.0 - 50.5, updatedWallet.getBalance());
        verify(walletRepository, times(1)).save(wallet);
    }

    @Test
    void testChangeBalanceWhenInsufficientFunds() {
        changeBalanceRequest.setOperationType(OperationType.WITHDRAW);
        changeBalanceRequest.setAmount(150.0);

        when(walletRepository.findById(wallet.getId())).thenReturn(Optional.of(wallet));

        assertThrows(RuntimeException.class, () -> walletService.changeBalance(changeBalanceRequest));
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void testChangeBalanceWhenWalletNotFound() {
        when(walletRepository.findById(wallet.getId())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> walletService.changeBalance(changeBalanceRequest));
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void testGetBalanceByWalletId() {
        when(walletRepository.findById(wallet.getId())).thenReturn(Optional.of(wallet));
        when(walletMapper.toDto(wallet)).thenReturn(walletDto);

        WalletDto retrievedDto = walletService.getBalanceByWalletId(wallet.getId());

        assertNotNull(retrievedDto);
        assertEquals(walletDto.getBalance(), retrievedDto.getBalance());
    }

    @Test
    void testGetBalanceByWalletIdWhenWalletNotFound() {
        when(walletRepository.findById(wallet.getId())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> walletService.getBalanceByWalletId(wallet.getId()));
    }

    @Test
    void testAddWallet() {
        when(walletMapper.toWallet(walletDto)).thenReturn(wallet);
        when(walletRepository.save(wallet)).thenReturn(wallet);

        Wallet createdWallet = walletService.addWallet(walletDto);

        assertNotNull(createdWallet);
        assertEquals(wallet.getId(), createdWallet.getId());
        assertEquals(wallet.getBalance(), createdWallet.getBalance());
    }

    @Test
    void testGetAllWallets() {
        when(walletRepository.findAll()).thenReturn(List.of(wallet));

        List<Wallet> wallets = walletService.getAllWallets();

        assertNotNull(wallets);
        assertFalse(wallets.isEmpty());
        assertEquals(wallet.getId(), wallets.get(0).getId());
        assertEquals(wallet.getBalance(), wallets.get(0).getBalance());
    }
}
