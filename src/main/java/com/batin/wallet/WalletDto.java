package com.batin.wallet;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletDto {

    @Min(value = 0, message = "Balance should be positive")
    @NotNull(message = "Balance cannot be null")
    private Long balance;
}