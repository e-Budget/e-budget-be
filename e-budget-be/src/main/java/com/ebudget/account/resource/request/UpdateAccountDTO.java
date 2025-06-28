package com.ebudget.account.resource.request;

import com.ebudget.account.model.enums.AccountLogo;
import com.ebudget.account.model.enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateAccountDTO(
        @NotNull
        AccountLogo accountLogo,
        @NotBlank
        String accountName,
        @NotNull
        AccountType accountType
) {
}
