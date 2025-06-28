package com.ebudget.expense.resource.response;

import com.ebudget.account.resource.response.AccountDTO;
import com.ebudget.category.resource.response.CategoryDTO;
import com.ebudget.expense.model.Expense;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(force = true)
public class ExpenseDTO {
    private final UUID expenseId;
    private final String expenseDescription;
    private final Integer expenseMonth;
    private final Integer expenseYear;
    private final BigDecimal amount;
    private final CategoryDTO category;
    private final AccountDTO account;
    private final LocalDate date;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public ExpenseDTO(Expense expense) {
        this.expenseId = expense.getExpenseId();
        this.expenseDescription = expense.getExpenseDescription();
        this.expenseMonth = expense.getExpenseMonth();
        this.expenseYear = expense.getExpenseYear();
        this.amount = expense.getAmount();
        this.category = expense.getCategory() != null ? new CategoryDTO(expense.getCategory().getCategoryId(), expense.getCategory().getCategoryName(), expense.getCategory().getCreatedAt(), expense.getCategory().getUpdatedAt()) : null;
        this.account = new AccountDTO(expense.getAccount().getAccountId(), expense.getAccount().getAccountLogo(), expense.getAccount().getAccountName(), expense.getAccount().getAccountType(), expense.getAccount().getInitialBalance(), expense.getAccount().getBalance(), expense.getAccount().getCreatedAt(), expense.getAccount().getUpdatedAt());
        this.date = expense.getDate();
        this.createdAt = expense.getCreatedAt();
        this.updatedAt = expense.getUpdatedAt();
    }
}
