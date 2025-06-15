package com.ebudget.budget.resource.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record UpdateBudgetDTO(
        @NotNull
        @Positive
        Integer budgetMonth,
        @NotNull
        @Positive
        Integer budgetYear,
        @NotNull
        @Positive
        BigDecimal monthlyBudget
) {
}
