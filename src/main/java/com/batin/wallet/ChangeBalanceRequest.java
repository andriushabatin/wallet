package com.batin.wallet;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ChangeBalanceRequest {

    @NotNull(message = "walletId cannot be null")
    private UUID walletId;
    @NotNull(message = "operationType cannot be null")
    private OperationType operationType;
    @NotNull(message = "amount cannot be null")
    private Double amount;
}
