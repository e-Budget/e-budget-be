package com.ebudget.budget.repository;

import com.ebudget.budget.model.Budget;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class BudgetRepository implements PanacheRepositoryBase<Budget, UUID> {
    public Long countByCategoryMonthYear(Map<String, Object> values) {
        return this.count("category = :category and budgetMonth = :budgetMonth and budgetYear = :budgetYear", values);
    }
}
