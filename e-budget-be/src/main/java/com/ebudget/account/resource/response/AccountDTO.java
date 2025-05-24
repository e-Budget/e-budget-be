package com.ebudget.account.resource.response;

import com.ebudget.account.model.enumeration.AccountType;

import java.time.LocalDateTime;
import java.util.UUID;

public record AccountDTO(
        UUID accountId,
        String accountLogo,
        String accountName,
        AccountType accountType,
        Double initialBalance,
        Double balance,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
