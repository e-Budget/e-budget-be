package com.ebudget.income.resource.response;

import com.ebudget.account.resource.response.AccountDTO;
import com.ebudget.income.model.Income;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(force = true)
public class IncomeDTO {
    private final UUID incomeId;
    private final String incomeDescription;
    private final BigDecimal amount;
    private final AccountDTO account;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public IncomeDTO(Income income) {
        this.incomeId = income.getIncomeId();
        this.incomeDescription = income.getIncomeDescription();
        this.amount = income.getAmount();
        this.account = new AccountDTO(income.getAccount());
        this.createdAt = income.getCreatedAt();
        this.updatedAt = income.getUpdatedAt();
    }
}
