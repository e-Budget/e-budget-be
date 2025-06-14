package com.ebudget.income.resource.response;

import com.ebudget.account.resource.response.AccountDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record IncomeDTO(
        UUID incomeId,
        String accountDescription,
        BigDecimal amount,
        AccountDTO account,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) { }
