package com.ebudget.expense.resource.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record UpdateExpenseDTO(
        @NotBlank
        String expenseDescription,
        @NotNull
        Integer expenseMonth,
        @NotNull
        Integer expenseYear,
        @NotNull
        @PositiveOrZero
        BigDecimal amount,
        UUID categoryId,
        @NotNull
        UUID accountId,
        @NotNull
        LocalDate date
) {
}
