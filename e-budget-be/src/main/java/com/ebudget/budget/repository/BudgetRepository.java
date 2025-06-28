package com.ebudget.budget.repository;

import com.ebudget.budget.model.Budget;
import com.ebudget.category.model.Category;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class BudgetRepository implements PanacheRepositoryBase<Budget, UUID> {
    public Long countByCategoryMonthYear(Map<String, Object> values) {
        return this.count("category = :category and budgetMonth = :budgetMonth and budgetYear = :budgetYear", values);
    }

    public Budget findByCategoryMonthYear(Category category, Integer budgetMonth, Integer budgetYear) {
        return this.find("category = ?1 and budgetMonth = ?2 and budgetYear = ?3", category, budgetMonth, budgetYear).firstResult();
    }
}
