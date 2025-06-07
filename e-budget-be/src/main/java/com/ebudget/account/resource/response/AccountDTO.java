package com.ebudget.account.resource.response;

import com.ebudget.account.model.enumeration.AccountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AccountDTO(
        UUID accountId,
        String accountLogo,
        String accountName,
        AccountType accountType,
        BigDecimal initialBalance,
        BigDecimal balance,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
