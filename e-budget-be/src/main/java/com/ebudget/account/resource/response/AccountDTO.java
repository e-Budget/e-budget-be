package com.ebudget.account.resource.response;

import com.ebudget.account.model.Account;
import com.ebudget.account.model.enums.AccountLogo;
import com.ebudget.account.model.enums.AccountType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(force = true)
public class AccountDTO {
    private final UUID accountId;
    private final AccountLogo accountLogo;
    private final String accountName;
    private final AccountType accountType;
    private final BigDecimal initialBalance;
    private final BigDecimal balance;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public AccountDTO(Account account) {
        this.accountId = account.getAccountId();
        this.accountLogo = account.getAccountLogo();
        this.accountName = account.getAccountName();
        this.accountType = account.getAccountType();
        this.initialBalance = account.getInitialBalance();
        this.balance = account.getBalance();
        this.createdAt = account.getCreatedAt();
        this.updatedAt = account.getUpdatedAt();
    }
}
