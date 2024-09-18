package com.batin.wallet;

import lombok.Data;

import java.util.UUID;

@Data
public class ChangeBalanceRequest {

    private UUID walletId;
    private OperationType operationType;
    private int amount;
}
