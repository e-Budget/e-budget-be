package com.ebudget.expense.service.interfaces;

import com.ebudget.expense.resource.request.NewExpenseDTO;
import com.ebudget.expense.resource.request.UpdateExpenseDTO;
import com.ebudget.expense.resource.response.ExpenseDTO;

import java.util.List;
import java.util.UUID;

public interface IExpenseService {
    ExpenseDTO addExpense(NewExpenseDTO newExpenseDTO);
    void updateExpense(UUID expenseId, UpdateExpenseDTO updateExpenseDTO);
    ExpenseDTO getExpense(UUID expenseId);
    List<ExpenseDTO> getExpenses();
    void deleteExpense(UUID expenseId);
}
