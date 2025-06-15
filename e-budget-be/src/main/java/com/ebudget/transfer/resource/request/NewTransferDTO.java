package com.ebudget.transfer.resource.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record NewTransferDTO(
        @NotNull
        String transferDescription,
        @NotNull
        BigDecimal amount,
        @NotNull
        UUID fromAccount,
        @NotNull
        UUID toAccount
) {
}
