package com.ebudget.budget.service;

import com.ebudget.budget.exception.BudgetAlreadyExistsException;
import com.ebudget.budget.model.Budget;
import com.ebudget.budget.repository.BudgetRepository;
import com.ebudget.budget.resource.request.NewBudgetDTO;
import com.ebudget.budget.resource.request.UpdateBudgetDTO;
import com.ebudget.budget.resource.response.BudgetDTO;
import com.ebudget.budget.service.interfaces.IBudgetService;
import com.ebudget.category.model.Category;
import com.ebudget.category.repository.CategoryRepository;
import com.ebudget.core.exceptions.EntityNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
@RequiredArgsConstructor
public class BudgetService implements IBudgetService {
    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;

    private static final String CATEGORY = "category";
    private static final String BUDGET_MONTH = "budgetMonth";
    private static final String BUDGET_YEAR = "budgetYear";

    @Override
    @Transactional
    public BudgetDTO addBudget(NewBudgetDTO newBudgetDTO) {
        Category category = categoryRepository.findById(newBudgetDTO.categoryId());

        if(category == null) {
            throw new EntityNotFoundException(Category.class, newBudgetDTO.categoryId());
        }

        if(containsBudget(category, newBudgetDTO.budgetMonth(), newBudgetDTO.budgetYear())) {
            throw new BudgetAlreadyExistsException(Map.of(
                    CATEGORY, category.getCategoryName(),
                    BUDGET_MONTH, newBudgetDTO.budgetMonth(),
                    BUDGET_YEAR, newBudgetDTO.budgetYear()
            ));
        }

        Budget budget = Budget.builder()
                .budgetMonth(newBudgetDTO.budgetMonth())
                .budgetYear(newBudgetDTO.budgetYear())
                .category(category)
                .monthlyBudget(newBudgetDTO.monthlyBudget())
                .monthlyBudgetUsed(new BigDecimal("0.00"))
                .monthlyBudgetUsedPercentage(new BigDecimal("0.00"))
                .monthlyBudgetBalance(newBudgetDTO.monthlyBudget())
                .build();

        budgetRepository.persistAndFlush(budget);

        return new BudgetDTO(budget);
    }

    @Override
    @Transactional
    public void updateBudget(UUID budgetId, UpdateBudgetDTO updateBudgetDTO) {
        Budget budget = budgetRepository.findById(budgetId);

        if(budget == null) {
            throw new EntityNotFoundException(Budget.class, budgetId);
        }

        if((!updateBudgetDTO.budgetMonth().equals(budget.getBudgetMonth()) ||
            !updateBudgetDTO.budgetYear().equals(budget.getBudgetYear())) &&
            containsBudget(budget.getCategory(), updateBudgetDTO.budgetMonth(), updateBudgetDTO.budgetYear())) {
            throw new BudgetAlreadyExistsException(Map.of(
                    CATEGORY, budget.getCategory().getCategoryName(),
                    BUDGET_MONTH, updateBudgetDTO.budgetMonth(),
                    BUDGET_YEAR, updateBudgetDTO.budgetYear()
            ));
        }

        budget.update(updateBudgetDTO);
    }

    @Override
    public BudgetDTO getBudget(UUID budgetId) {
        Budget budget = budgetRepository.findById(budgetId);

        if(budget == null) {
            throw new EntityNotFoundException(Budget.class, budgetId);
        }

        return new BudgetDTO(budget);
    }

    @Override
    public List<BudgetDTO> getBudgets() {
        List<Budget> budgets = budgetRepository.listAll();

        return budgets.stream()
                .map(BudgetDTO::new)
                .toList();
    }

    @Override
    @Transactional
    public void deleteBudget(UUID budgetId) {
        Budget budget = budgetRepository.findById(budgetId);

        if(budget == null) {
            throw new EntityNotFoundException(Budget.class, budgetId);
        }

        budgetRepository.delete(budget);
    }

    private boolean containsBudget(Category category, Integer budgetMonth, Integer budgetYear) {
        Long count = budgetRepository.countByCategoryMonthYear(Map.of(
                CATEGORY, category,
                BUDGET_MONTH, budgetMonth,
                BUDGET_YEAR, budgetYear
        ));

        return count > 0;
    }
}
