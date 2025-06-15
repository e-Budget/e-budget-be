package com.ebudget.budget.service.interfaces;

import com.ebudget.budget.resource.request.NewBudgetDTO;
import com.ebudget.budget.resource.request.UpdateBudgetDTO;
import com.ebudget.budget.resource.response.BudgetDTO;

import java.util.List;
import java.util.UUID;

public interface IBudgetService {
    BudgetDTO addBudget(NewBudgetDTO newBudgetDTO);
    void updateBudget(UUID budgetId, UpdateBudgetDTO updateBudgetDTO);
    BudgetDTO getBudget(UUID budgetId);
    List<BudgetDTO> getBudgets();
    void deleteBudget(UUID budgetId);
}
