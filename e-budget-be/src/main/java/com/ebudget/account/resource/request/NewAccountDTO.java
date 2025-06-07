package com.ebudget.account.resource.request;

import com.ebudget.account.model.enumeration.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record NewAccountDTO(
        String accountLogo,
        @NotBlank
        String accountName,
        @NotNull
        AccountType accountType,
        @NotNull
        BigDecimal initialBalance
) {
}
