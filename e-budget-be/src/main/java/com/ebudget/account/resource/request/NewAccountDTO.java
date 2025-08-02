package com.ebudget.account.resource.request;

import com.ebudget.account.model.enums.FinancialInstitution;
import com.ebudget.account.model.enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record NewAccountDTO(
        @NotNull
        FinancialInstitution financialInstitution,
        @NotBlank
        String accountName,
        @NotNull
        AccountType accountType,
        @NotNull
        @PositiveOrZero
        BigDecimal initialBalance
) {
}
