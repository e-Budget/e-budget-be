package com.ebudget.income.resource.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record NewIncomeDTO(
        @NotBlank
        String incomeDescription,
        @NotNull
        @Positive
        BigDecimal amount,
        @NotNull
        UUID accountId
) {
}
