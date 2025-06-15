package com.ebudget.budget.resource.response;

import com.ebudget.category.resource.response.CategoryDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record BudgetDTO(
        UUID budgetId,
        Integer budgetMonth,
        Integer budgetYear,
        CategoryDTO category,
        BigDecimal monthlyBudget,
        BigDecimal monthlyBudgetUsed,
        BigDecimal monthlyBudgetUsedPercentage,
        BigDecimal monthlyBudgetBalance,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
