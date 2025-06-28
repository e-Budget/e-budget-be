package com.ebudget.budget.resource.response;

import com.ebudget.budget.model.Budget;
import com.ebudget.category.resource.response.CategoryDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(force = true)
public class BudgetDTO {
    private final UUID budgetId;
    private final Integer budgetMonth;
    private final Integer budgetYear;
    private final CategoryDTO category;
    private final BigDecimal monthlyBudget;
    private final BigDecimal monthlyBudgetUsed;
    private final BigDecimal monthlyBudgetUsedPercentage;
    private final BigDecimal monthlyBudgetBalance;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public BudgetDTO(Budget budget) {
        this.budgetId = budget.getBudgetId();
        this.budgetMonth = budget.getBudgetMonth();
        this.budgetYear = budget.getBudgetYear();
        this.category = new CategoryDTO(budget.getCategory());
        this.monthlyBudget = budget.getMonthlyBudget();
        this.monthlyBudgetUsed = budget.getMonthlyBudgetUsed();
        this.monthlyBudgetUsedPercentage = budget.getMonthlyBudgetUsedPercentage();
        this.monthlyBudgetBalance = budget.getMonthlyBudgetBalance();
        this.createdAt = budget.getCreatedAt();
        this.updatedAt = budget.getUpdatedAt();
    }
}
