package com.ebudget.budget.resource.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record NewBudgetDTO(
        @NotNull
        @Positive
        Integer budgetMonth,
        @NotNull
        @Positive
        Integer budgetYear,
        @NotNull
        UUID categoryId,
        @NotNull
        @Positive
        BigDecimal monthlyBudget
) {
}
