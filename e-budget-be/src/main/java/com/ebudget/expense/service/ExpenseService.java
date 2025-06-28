package com.ebudget.expense.service;

import com.ebudget.account.model.Account;
import com.ebudget.account.repository.AccountRepository;
import com.ebudget.budget.model.Budget;
import com.ebudget.budget.repository.BudgetRepository;
import com.ebudget.category.model.Category;
import com.ebudget.category.repository.CategoryRepository;
import com.ebudget.core.exceptions.EntityNotFoundException;
import com.ebudget.expense.model.Expense;
import com.ebudget.expense.repository.ExpenseRepository;
import com.ebudget.expense.resource.request.NewExpenseDTO;
import com.ebudget.expense.resource.request.UpdateExpenseDTO;
import com.ebudget.expense.resource.response.ExpenseDTO;
import com.ebudget.expense.service.interfaces.IExpenseService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
@RequiredArgsConstructor
public class ExpenseService implements IExpenseService {
    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final AccountRepository accountRepository;
    private final BudgetRepository budgetRepository;

    @Override
    @Transactional
    public ExpenseDTO addExpense(NewExpenseDTO newExpenseDTO) {
        Category category = categoryRepository.findById(newExpenseDTO.categoryId());
        Account account = accountRepository.findById(newExpenseDTO.accountId());

        if(account == null) {
            throw new EntityNotFoundException(Account.class, newExpenseDTO.accountId());
        }

        Expense expense = Expense.builder()
                .expenseDescription(newExpenseDTO.expenseDescription())
                .expenseMonth(newExpenseDTO.expenseMonth())
                .expenseYear(newExpenseDTO.expenseYear())
                .amount(newExpenseDTO.amount())
                .date(newExpenseDTO.date())
                .category(category)
                .account(account)
                .build();

        if(category != null) {
            processBudget(expense);
        }

        processAccount(expense);

        expenseRepository.persistAndFlush(expense);

        return new ExpenseDTO(expense);
    }

    @Override
    @Transactional
    public void updateExpense(UUID expenseId, UpdateExpenseDTO updateExpenseDTO) {
        Expense expense = expenseRepository.findById(expenseId);

        if(expense == null) {
            throw new EntityNotFoundException(Expense.class, expenseId);
        }

        processBudget(expense, updateExpenseDTO);
        processAccount(expense, updateExpenseDTO);

        expense.update(updateExpenseDTO);
    }

    @Override
    public ExpenseDTO getExpense(UUID expenseId) {
        Expense expense = expenseRepository.findById(expenseId);

        if(expense == null) {
            throw new EntityNotFoundException(Expense.class, expenseId);
        }

        return new ExpenseDTO(expense);
    }

    @Override
    public List<ExpenseDTO> getExpenses() {
        List<Expense> expenses = expenseRepository.listAll();

        return expenses.stream()
                .map(ExpenseDTO::new)
                .toList();
    }

    @Override
    @Transactional
    public void deleteExpense(UUID expenseId) {
        Expense expense = expenseRepository.findById(expenseId);

        if(expense == null) {
            throw new EntityNotFoundException(Expense.class, expenseId);
        }

        Budget budget = budgetRepository.findByCategoryMonthYear(expense.getCategory(), expense.getExpenseMonth(), expense.getExpenseYear());

        if(budget != null) {
            budget.add(expense.getAmount());
        }

        expense.getAccount().deposit(expense.getAmount());

        expenseRepository.delete(expense);
    }

    private void processBudget(Expense expense) {
        Integer associatedMonth = expense.getDate().getMonthValue();
        Integer associatedYear = expense.getDate().getYear();

        Budget budget = budgetRepository.findByCategoryMonthYear(expense.getCategory(), associatedMonth, associatedYear);

        if(budget != null) {
            budget.subtract(expense.getAmount());
        }
    }

    private void processBudget(Expense expense, UpdateExpenseDTO updateExpenseDTO) {
        Integer associatedMonth = expense.getDate().getMonthValue();
        Integer associatedYear = expense.getDate().getYear();

        Budget budget = budgetRepository.findByCategoryMonthYear(expense.getCategory(), associatedMonth, associatedYear);

        if(budget != null) {
            budget.add(expense.getAmount());
        }

        Category newCategory = categoryRepository.findById(updateExpenseDTO.categoryId());

        if(newCategory != null) {
            Integer newAssociatedMonth = updateExpenseDTO.date().getMonthValue();
            Integer newAssociatedYear = updateExpenseDTO.date().getYear();

            Budget newBudget = budgetRepository.findByCategoryMonthYear(newCategory, newAssociatedMonth, newAssociatedYear);

            if(newBudget != null) {
                newBudget.subtract(updateExpenseDTO.amount());
            }
        }

        expense.bindCategory(newCategory);
    }

    private void processAccount(Expense expense) {
        expense.getAccount().withdraw(expense.getAmount());
    }

    private void processAccount(Expense expense, UpdateExpenseDTO updateExpenseDTO) {
        Account account = expense.getAccount();

        account.deposit(expense.getAmount());

        Account newAccount = accountRepository.findById(updateExpenseDTO.accountId());

        if(newAccount == null) {
            throw new EntityNotFoundException(Account.class, updateExpenseDTO.accountId());
        }

        newAccount.withdraw(updateExpenseDTO.amount());
        expense.bindAccount(newAccount);
    }
}
