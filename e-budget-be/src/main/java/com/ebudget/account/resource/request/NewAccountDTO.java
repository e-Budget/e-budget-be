package com.ebudget.account.resource.request;

import com.ebudget.account.model.enumeration.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NewAccountDTO(
        String accountLogo,
        @NotBlank
        String accountName,
        @NotNull
        AccountType accountType,
        @NotNull
        Double initialBalance
) {
}
