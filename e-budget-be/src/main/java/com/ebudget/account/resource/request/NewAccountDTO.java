package com.ebudget.account.resource.request;

import com.ebudget.account.model.enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record NewAccountDTO(
        String accountLogo,
        @NotBlank
        String accountName,
        @NotNull
        AccountType accountType,
        @NotNull
        @Positive
        BigDecimal initialBalance
) {
}
