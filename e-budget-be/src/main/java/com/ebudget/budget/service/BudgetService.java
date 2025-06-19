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
import com.ebudget.category.resource.response.CategoryDTO;
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

    @Override
    @Transactional
    public BudgetDTO addBudget(NewBudgetDTO newBudgetDTO) {
        Category category = categoryRepository.findById(newBudgetDTO.categoryId());

        if(category == null) {
            throw new EntityNotFoundException(Category.class, newBudgetDTO.categoryId());
        }

        if(containsBudget(category, newBudgetDTO.budgetMonth(), newBudgetDTO.budgetYear())) {
            throw new BudgetAlreadyExistsException(Map.of(
                    "category", category.getCategoryName(),
                    "budgetMonth", newBudgetDTO.budgetMonth(),
                    "budgetYear", newBudgetDTO.budgetYear()
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

        return new BudgetDTO(
                budget.getBudgetId(),
                budget.getBudgetMonth(),
                budget.getBudgetYear(),
                new CategoryDTO(
                        budget.getCategory().getCategoryId(),
                        budget.getCategory().getCategoryName(),
                        budget.getCategory().getCreatedAt(),
                        budget.getCategory().getUpdatedAt()
                ),
                budget.getMonthlyBudget(),
                budget.getMonthlyBudgetUsed(),
                budget.getMonthlyBudgetUsedPercentage(),
                budget.getMonthlyBudgetBalance(),
                budget.getCreatedAt(),
                budget.getUpdatedAt()
        );
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
                    "category", budget.getCategory().getCategoryName(),
                    "budgetMonth", updateBudgetDTO.budgetMonth(),
                    "budgetYear", updateBudgetDTO.budgetYear()
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

        return new BudgetDTO(
                budget.getBudgetId(),
                budget.getBudgetMonth(),
                budget.getBudgetYear(),
                new CategoryDTO(
                        budget.getCategory().getCategoryId(),
                        budget.getCategory().getCategoryName(),
                        budget.getCategory().getCreatedAt(),
                        budget.getCategory().getUpdatedAt()
                ),
                budget.getMonthlyBudget(),
                budget.getMonthlyBudgetUsed(),
                budget.getMonthlyBudgetUsedPercentage(),
                budget.getMonthlyBudgetBalance(),
                budget.getCreatedAt(),
                budget.getUpdatedAt()
        );
    }

    @Override
    public List<BudgetDTO> getBudgets() {
        List<Budget> budgets = budgetRepository.listAll();

        return budgets.stream()
                .map(budget -> new BudgetDTO(
                        budget.getBudgetId(),
                        budget.getBudgetMonth(),
                        budget.getBudgetYear(),
                        new CategoryDTO(
                                budget.getCategory().getCategoryId(),
                                budget.getCategory().getCategoryName(),
                                budget.getCategory().getCreatedAt(),
                                budget.getCategory().getUpdatedAt()
                        ),
                        budget.getMonthlyBudget(),
                        budget.getMonthlyBudgetUsed(),
                        budget.getMonthlyBudgetUsedPercentage(),
                        budget.getMonthlyBudgetBalance(),
                        budget.getCreatedAt(),
                        budget.getUpdatedAt()
                ))
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
                "category", category,
                "budgetMonth", budgetMonth,
                "budgetYear", budgetYear
        ));

        return count > 0;
    }
}
