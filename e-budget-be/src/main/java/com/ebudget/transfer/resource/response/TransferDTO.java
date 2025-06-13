package com.ebudget.transfer.resource.response;

import com.ebudget.account.resource.response.AccountDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransferDTO(
        UUID transferId,
        String transferDescription,
        BigDecimal amount,
        AccountDTO fromAccount,
        AccountDTO toAccount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
