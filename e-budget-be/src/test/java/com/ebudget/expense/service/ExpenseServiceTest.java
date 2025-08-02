package com.ebudget.expense.service;

import com.ebudget.account.model.Account;
import com.ebudget.account.model.enums.FinancialInstitution;
import com.ebudget.account.model.enums.AccountType;
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
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
@DisplayName("Expense Service")
class ExpenseServiceTest {
    @Inject
    ExpenseService expenseService;
    @InjectMock
    ExpenseRepository expenseRepository;
    @InjectMock
    CategoryRepository categoryRepository;
    @InjectMock
    AccountRepository accountRepository;
    @InjectMock
    BudgetRepository budgetRepository;

    private UUID sampleExpenseId;
    private Expense sampleExpense;
    private Account sampleAccount;
    private Category sampleCategory;
    private Budget sampleBudget;

    @BeforeEach
    void setup() {
        sampleAccount = Account.builder()
                .accountId(UUID.randomUUID())
                .financialInstitution(FinancialInstitution.NONE)
                .accountName("accountName")
                .accountType(AccountType.BANK_ACCOUNT)
                .initialBalance(new BigDecimal("100.00"))
                .balance(new BigDecimal("90.00"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        sampleCategory = Category.builder()
                .categoryId(UUID.randomUUID())
                .categoryName("categoryName")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        sampleBudget = Budget.builder()
                .budgetId(UUID.randomUUID())
                .budgetMonth(6)
                .budgetYear(2025)
                .category(sampleCategory)
                .monthlyBudget(new BigDecimal("100.00"))
                .monthlyBudgetUsed(new BigDecimal("10.00"))
                .monthlyBudgetUsedPercentage(new BigDecimal("10.00"))
                .monthlyBudgetBalance(new BigDecimal("90.00"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        sampleExpenseId = UUID.randomUUID();
        sampleExpense = Expense.builder()
                .expenseId(sampleExpenseId)
                .expenseDescription("expenseDescription")
                .amount(new BigDecimal("10.00"))
                .date(LocalDate.of(2025, 6, 27))
                .expenseMonth(6)
                .expenseYear(2025)
                .account(sampleAccount)
                .category(sampleCategory)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should add an expense")
    void shouldAddExpense() {
        // given
        BigDecimal accountBalance = sampleAccount.getBalance();
        BigDecimal monthlyBudgetBalance = sampleBudget.getMonthlyBudgetBalance();
        BigDecimal monthlyBudgetUsed = sampleBudget.getMonthlyBudgetUsed();
        BigDecimal monthlyBudgetUsedPercentage = sampleBudget.getMonthlyBudgetUsedPercentage();

        NewExpenseDTO newExpenseDTO = new NewExpenseDTO(
                "expenseDescription",
                6,
                2025,
                new BigDecimal("10.00"),
                sampleCategory.getCategoryId(),
                sampleAccount.getAccountId(),
                LocalDate.of(2025, 6, 27)
        );

        when(categoryRepository.findById(any(UUID.class))).thenReturn(sampleCategory);
        when(accountRepository.findById(any(UUID.class))).thenReturn(sampleAccount);
        when(budgetRepository.findByCategoryMonthYear(any(Category.class), anyInt(), anyInt())).thenReturn(sampleBudget);
        doNothing().when(expenseRepository).persistAndFlush(any(Expense.class));

        // when
        ExpenseDTO expense = expenseService.addExpense(newExpenseDTO);

        // then
        assertThat(expense.getExpenseDescription()).isEqualTo(newExpenseDTO.expenseDescription());
        assertThat(expense.getExpenseMonth()).isEqualTo(newExpenseDTO.expenseMonth());
        assertThat(expense.getExpenseYear()).isEqualTo(newExpenseDTO.expenseYear());
        assertThat(expense.getAmount()).isEqualTo(newExpenseDTO.amount());
        assertThat(expense.getCategory().getCategoryId()).isEqualTo(newExpenseDTO.categoryId());
        assertThat(expense.getAccount().getAccountId()).isEqualTo(newExpenseDTO.accountId());
        assertThat(expense.getAccount().getBalance()).isEqualTo(accountBalance.subtract(newExpenseDTO.amount()));
        assertThat(sampleBudget.getMonthlyBudgetBalance()).isEqualTo(monthlyBudgetBalance.subtract(newExpenseDTO.amount()));
        assertThat(sampleBudget.getMonthlyBudgetUsed()).isEqualTo(monthlyBudgetUsed.add(newExpenseDTO.amount()));
        assertThat(sampleBudget.getMonthlyBudgetUsedPercentage()).isEqualTo(monthlyBudgetUsedPercentage.add(newExpenseDTO.amount().multiply(new BigDecimal(100)).divide(sampleBudget.getMonthlyBudget(), RoundingMode.HALF_UP)));

        verify(categoryRepository, times(1)).findById(any(UUID.class));
        verify(accountRepository, times(1)).findById(any(UUID.class));
        verify(budgetRepository, times(1)).findByCategoryMonthYear(any(Category.class), anyInt(), anyInt());
        verify(expenseRepository, times(1)).persistAndFlush(any(Expense.class));
    }

    @Test
    @DisplayName("Should add an expense when category is null")
    void shouldAddExpenseWhenCategoryIsNull() {
        // given
        BigDecimal accountBalance = sampleAccount.getBalance();

        NewExpenseDTO newExpenseDTO = new NewExpenseDTO(
                "expenseDescription",
                6,
                2025,
                new BigDecimal("10.00"),
                null,
                sampleAccount.getAccountId(),
                LocalDate.of(2025, 6, 27)
        );

        when(categoryRepository.findById(any(UUID.class))).thenReturn(null);
        when(accountRepository.findById(any(UUID.class))).thenReturn(sampleAccount);
        doNothing().when(expenseRepository).persistAndFlush(any(Expense.class));

        // when
        ExpenseDTO expense = expenseService.addExpense(newExpenseDTO);

        // then
        assertThat(expense.getExpenseDescription()).isEqualTo(newExpenseDTO.expenseDescription());
        assertThat(expense.getExpenseMonth()).isEqualTo(newExpenseDTO.expenseMonth());
        assertThat(expense.getExpenseYear()).isEqualTo(newExpenseDTO.expenseYear());
        assertThat(expense.getAmount()).isEqualTo(newExpenseDTO.amount());
        assertThat(expense.getCategory()).isNull();
        assertThat(expense.getAccount().getAccountId()).isEqualTo(newExpenseDTO.accountId());
        assertThat(expense.getAccount().getBalance()).isEqualTo(accountBalance.subtract(newExpenseDTO.amount()));

        verify(categoryRepository, times(1)).findById(null);
        verify(accountRepository, times(1)).findById(any(UUID.class));
        verify(expenseRepository, times(1)).persistAndFlush(any(Expense.class));
    }

    @Test
    @DisplayName("Should add an expense when category has no budget associated")
    void shouldAddExpenseWhenCategoryHasNoBudgetAssociated() {
        // given
        BigDecimal accountBalance = sampleAccount.getBalance();

        NewExpenseDTO newExpenseDTO = new NewExpenseDTO(
                "expenseDescription",
                6,
                2025,
                new BigDecimal("10.00"),
                sampleCategory.getCategoryId(),
                sampleAccount.getAccountId(),
                LocalDate.of(2025, 6, 27)
        );

        when(categoryRepository.findById(any(UUID.class))).thenReturn(sampleCategory);
        when(budgetRepository.findByCategoryMonthYear(any(Category.class), anyInt(), anyInt())).thenReturn(null);
        when(accountRepository.findById(any(UUID.class))).thenReturn(sampleAccount);
        doNothing().when(expenseRepository).persistAndFlush(any(Expense.class));

        // when
        ExpenseDTO expense = expenseService.addExpense(newExpenseDTO);

        // then
        assertThat(expense.getExpenseDescription()).isEqualTo(newExpenseDTO.expenseDescription());
        assertThat(expense.getExpenseMonth()).isEqualTo(newExpenseDTO.expenseMonth());
        assertThat(expense.getExpenseYear()).isEqualTo(newExpenseDTO.expenseYear());
        assertThat(expense.getAmount()).isEqualTo(newExpenseDTO.amount());
        assertThat(expense.getCategory().getCategoryId()).isEqualTo(newExpenseDTO.categoryId());
        assertThat(expense.getAccount().getAccountId()).isEqualTo(newExpenseDTO.accountId());
        assertThat(expense.getAccount().getBalance()).isEqualTo(accountBalance.subtract(newExpenseDTO.amount()));

        verify(categoryRepository, times(1)).findById(any(UUID.class));
        verify(budgetRepository, times(1)).findByCategoryMonthYear(any(Category.class), anyInt(), anyInt());
        verify(accountRepository, times(1)).findById(any(UUID.class));
        verify(expenseRepository, times(1)).persistAndFlush(any(Expense.class));
    }

    @Test
    @DisplayName("Should throw exception on add expense with a non-existing account")
    void shouldThrowExceptionOnAddExpenseNonExistingAccount() {
        // given
        NewExpenseDTO newExpenseDTO = new NewExpenseDTO(
                "expenseDescription",
                6,
                2025,
                new BigDecimal("10.00"),
                sampleCategory.getCategoryId(),
                sampleAccount.getAccountId(),
                LocalDate.of(2025, 6, 27)
        );

        when(accountRepository.findById(any(UUID.class))).thenReturn(null);

        // when / then
        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() -> {
            expenseService.addExpense(newExpenseDTO);
        });

        verify(accountRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Should update an expense")
    void shouldUpdateExpense() {
        // given
        BigDecimal accountBalance = sampleExpense.getAccount().getBalance();
        BigDecimal expenseAmount = sampleExpense.getAmount();

        UpdateExpenseDTO updateExpenseDTO = new UpdateExpenseDTO(
                "newExpenseDescription",
                6,
                2025,
                new BigDecimal("5.00"),
                sampleCategory.getCategoryId(),
                sampleAccount.getAccountId(),
                LocalDate.of(2025, 6, 28)
        );

        when(expenseRepository.findById(sampleExpenseId)).thenReturn(sampleExpense);
        when(budgetRepository.findByCategoryMonthYear(sampleExpense.getCategory(), sampleExpense.getDate().getMonthValue(), sampleExpense.getDate().getYear())).thenReturn(sampleBudget);
        when(categoryRepository.findById(updateExpenseDTO.categoryId())).thenReturn(sampleCategory);
        when(accountRepository.findById(updateExpenseDTO.accountId())).thenReturn(sampleAccount);

        // when
        expenseService.updateExpense(sampleExpenseId, updateExpenseDTO);

        // then
        assertThat(sampleExpense.getExpenseId()).isEqualTo(sampleExpenseId);
        assertThat(sampleExpense.getExpenseDescription()).isEqualTo(updateExpenseDTO.expenseDescription());
        assertThat(sampleExpense.getExpenseMonth()).isEqualTo(updateExpenseDTO.expenseMonth());
        assertThat(sampleExpense.getExpenseYear()).isEqualTo(updateExpenseDTO.expenseYear());
        assertThat(sampleExpense.getAmount()).isEqualTo(updateExpenseDTO.amount());
        assertThat(sampleExpense.getCategory().getCategoryId()).isEqualTo(updateExpenseDTO.categoryId());
        assertThat(sampleExpense.getAccount().getAccountId()).isEqualTo(updateExpenseDTO.accountId());
        assertThat(sampleExpense.getDate()).isEqualTo(updateExpenseDTO.date());
        assertThat(sampleExpense.getAccount().getBalance()).isEqualTo(accountBalance.add(expenseAmount.subtract(updateExpenseDTO.amount())));

        verify(expenseRepository, times(1)).findById(sampleExpenseId);
        verify(budgetRepository, times(2)).findByCategoryMonthYear(sampleExpense.getCategory(), sampleExpense.getDate().getMonthValue(), sampleExpense.getDate().getYear());
        verify(accountRepository, times(1)).findById(updateExpenseDTO.accountId());
    }

    @Test
    @DisplayName("Should throw exception on update a non-existing expense")
    void shouldThrowExceptionOnUpdateNonExistingExpense() {
        // given
        UpdateExpenseDTO updateExpenseDTO = new UpdateExpenseDTO(
                "expenseDescription",
                6,
                2025,
                new BigDecimal("5.00"),
                sampleCategory.getCategoryId(),
                sampleAccount.getAccountId(),
                LocalDate.of(2025, 6, 28)
        );

        when(expenseRepository.findById(any(UUID.class))).thenReturn(null);

        // when / then
        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() -> {
            expenseService.updateExpense(sampleExpenseId, updateExpenseDTO);
        });

        verify(expenseRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Should update an expense when current category has no budget associated")
    void shouldUpdateExpenseWhenCurrentCategoryHasNoBudgetAssociated() {
        // given
        BigDecimal accountBalance = sampleExpense.getAccount().getBalance();
        BigDecimal expenseAmount = sampleExpense.getAmount();

        UpdateExpenseDTO updateExpenseDTO = new UpdateExpenseDTO(
                "newExpenseDescription",
                6,
                2025,
                new BigDecimal("5.00"),
                sampleCategory.getCategoryId(),
                sampleAccount.getAccountId(),
                LocalDate.of(2025, 6, 28)
        );

        when(expenseRepository.findById(sampleExpenseId)).thenReturn(sampleExpense);
        when(budgetRepository.findByCategoryMonthYear(sampleExpense.getCategory(), sampleExpense.getDate().getMonthValue(), sampleExpense.getDate().getYear())).thenReturn(null);
        when(categoryRepository.findById(updateExpenseDTO.categoryId())).thenReturn(sampleCategory);
        when(accountRepository.findById(updateExpenseDTO.accountId())).thenReturn(sampleAccount);

        // when
        expenseService.updateExpense(sampleExpenseId, updateExpenseDTO);

        // then
        assertThat(sampleExpense.getExpenseId()).isEqualTo(sampleExpenseId);
        assertThat(sampleExpense.getExpenseDescription()).isEqualTo(updateExpenseDTO.expenseDescription());
        assertThat(sampleExpense.getExpenseMonth()).isEqualTo(updateExpenseDTO.expenseMonth());
        assertThat(sampleExpense.getExpenseYear()).isEqualTo(updateExpenseDTO.expenseYear());
        assertThat(sampleExpense.getAmount()).isEqualTo(updateExpenseDTO.amount());
        assertThat(sampleExpense.getCategory().getCategoryId()).isEqualTo(updateExpenseDTO.categoryId());
        assertThat(sampleExpense.getAccount().getAccountId()).isEqualTo(updateExpenseDTO.accountId());
        assertThat(sampleExpense.getDate()).isEqualTo(updateExpenseDTO.date());
        assertThat(sampleExpense.getAccount().getBalance()).isEqualTo(accountBalance.add(expenseAmount.subtract(updateExpenseDTO.amount())));

        verify(expenseRepository, times(1)).findById(sampleExpenseId);
        verify(budgetRepository, times(2)).findByCategoryMonthYear(sampleExpense.getCategory(), sampleExpense.getDate().getMonthValue(), sampleExpense.getDate().getYear());
        verify(accountRepository, times(1)).findById(updateExpenseDTO.accountId());
    }

    @Test
    @DisplayName("Should update an expense when new category is null")
    void shouldUpdateExpenseWhenNewCategoryIsNull() {
        // given
        BigDecimal accountBalance = sampleExpense.getAccount().getBalance();
        BigDecimal expenseAmount = sampleExpense.getAmount();

        UpdateExpenseDTO updateExpenseDTO = new UpdateExpenseDTO(
                "newExpenseDescription",
                6,
                2025,
                new BigDecimal("5.00"),
                null,
                sampleAccount.getAccountId(),
                LocalDate.of(2025, 6, 28)
        );

        when(expenseRepository.findById(sampleExpenseId)).thenReturn(sampleExpense);
        when(categoryRepository.findById(updateExpenseDTO.categoryId())).thenReturn(null);
        when(accountRepository.findById(updateExpenseDTO.accountId())).thenReturn(sampleAccount);

        // when
        expenseService.updateExpense(sampleExpenseId, updateExpenseDTO);

        // then
        assertThat(sampleExpense.getExpenseId()).isEqualTo(sampleExpenseId);
        assertThat(sampleExpense.getExpenseDescription()).isEqualTo(updateExpenseDTO.expenseDescription());
        assertThat(sampleExpense.getExpenseMonth()).isEqualTo(updateExpenseDTO.expenseMonth());
        assertThat(sampleExpense.getExpenseYear()).isEqualTo(updateExpenseDTO.expenseYear());
        assertThat(sampleExpense.getAmount()).isEqualTo(updateExpenseDTO.amount());
        assertThat(sampleExpense.getCategory()).isNull();
        assertThat(sampleExpense.getAccount().getAccountId()).isEqualTo(updateExpenseDTO.accountId());
        assertThat(sampleExpense.getDate()).isEqualTo(updateExpenseDTO.date());
        assertThat(sampleExpense.getAccount().getBalance()).isEqualTo(accountBalance.add(expenseAmount.subtract(updateExpenseDTO.amount())));

        verify(expenseRepository, times(1)).findById(sampleExpenseId);
        verify(categoryRepository, times(1)).findById(isNull());
        verify(accountRepository, times(1)).findById(updateExpenseDTO.accountId());
    }

    @Test
    @DisplayName("Should throw exception on update an expense when non-existing new account")
    void shouldThrowExceptionOnUpdateExpenseWhenNonExistingNewAccount() {
        // given
        UpdateExpenseDTO updateExpenseDTO = new UpdateExpenseDTO(
                "newExpenseDescription",
                6,
                2025,
                new BigDecimal("5.00"),
                sampleCategory.getCategoryId(),
                sampleAccount.getAccountId(),
                LocalDate.of(2025, 6, 28)
        );

        when(expenseRepository.findById(sampleExpenseId)).thenReturn(sampleExpense);
        when(budgetRepository.findByCategoryMonthYear(sampleExpense.getCategory(), sampleExpense.getDate().getMonthValue(), sampleExpense.getDate().getYear())).thenReturn(sampleBudget);
        when(categoryRepository.findById(updateExpenseDTO.categoryId())).thenReturn(sampleCategory);
        when(accountRepository.findById(updateExpenseDTO.accountId())).thenReturn(null);

        // when / then
        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() -> {
            expenseService.updateExpense(sampleExpenseId, updateExpenseDTO);
        });

        verify(expenseRepository, times(1)).findById(sampleExpenseId);
        verify(budgetRepository, times(2)).findByCategoryMonthYear(sampleExpense.getCategory(), sampleExpense.getDate().getMonthValue(), sampleExpense.getDate().getYear());
        verify(accountRepository, times(1)).findById(updateExpenseDTO.accountId());
    }

    @Test
    @DisplayName("Should get an expense")
    void shouldGetExpense() {
        // given
        when(expenseRepository.findById(any(UUID.class))).thenReturn(sampleExpense);

        // when
        ExpenseDTO expense = expenseService.getExpense(sampleExpenseId);

        // then
        assertThat(expense.getExpenseId()).isEqualTo(sampleExpense.getExpenseId());
        assertThat(expense.getExpenseDescription()).isEqualTo(sampleExpense.getExpenseDescription());
        assertThat(expense.getExpenseMonth()).isEqualTo(sampleExpense.getExpenseMonth());
        assertThat(expense.getExpenseYear()).isEqualTo(sampleExpense.getExpenseYear());
        assertThat(expense.getDate()).isEqualTo(sampleExpense.getDate());
        assertThat(expense.getAmount()).isEqualTo(sampleExpense.getAmount());
        assertThat(expense.getAccount().getAccountId()).isEqualTo(sampleExpense.getAccount().getAccountId());
        assertThat(expense.getCategory().getCategoryId()).isEqualTo(sampleExpense.getCategory().getCategoryId());
        assertThat(expense.getCreatedAt()).isEqualTo(sampleExpense.getCreatedAt());
        assertThat(expense.getUpdatedAt()).isEqualTo(sampleExpense.getUpdatedAt());

        verify(expenseRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Should throw exception on get a non-existing expense")
    void shouldThrowExceptionOnGetNonExistingExpense() {
        // given
        when(expenseRepository.findById(any(UUID.class))).thenReturn(null);

        // when / then
        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() -> {
            expenseService.getExpense(sampleExpenseId);
        });

        verify(expenseRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Should get all expenses")
    void shouldGetExpenses() {
        // given
        when(expenseRepository.listAll()).thenReturn(List.of(sampleExpense));

        // when
        List<ExpenseDTO> expenses = expenseService.getExpenses();

        // then
        assertThat(expenses).hasSize(1);
        assertThat(expenses.getFirst().getExpenseId()).isEqualTo(sampleExpense.getExpenseId());
        assertThat(expenses.getFirst().getExpenseDescription()).isEqualTo(sampleExpense.getExpenseDescription());
        assertThat(expenses.getFirst().getExpenseMonth()).isEqualTo(sampleExpense.getExpenseMonth());
        assertThat(expenses.getFirst().getExpenseYear()).isEqualTo(sampleExpense.getExpenseYear());
        assertThat(expenses.getFirst().getDate()).isEqualTo(sampleExpense.getDate());
        assertThat(expenses.getFirst().getAmount()).isEqualTo(sampleExpense.getAmount());
        assertThat(expenses.getFirst().getAccount().getAccountId()).isEqualTo(sampleExpense.getAccount().getAccountId());
        assertThat(expenses.getFirst().getCategory().getCategoryId()).isEqualTo(sampleExpense.getCategory().getCategoryId());
        assertThat(expenses.getFirst().getCreatedAt()).isEqualTo(sampleExpense.getCreatedAt());
        assertThat(expenses.getFirst().getUpdatedAt()).isEqualTo(sampleExpense.getUpdatedAt());

        verify(expenseRepository, times(1)).listAll();
    }

    @Test
    @DisplayName("Should delete an expense")
    void shouldDeleteExpense() {
        // given
        BigDecimal accountBalance = sampleAccount.getBalance();
        BigDecimal monthlyBudgetBalance = sampleBudget.getMonthlyBudgetBalance();
        BigDecimal monthlyBudgetUsed = sampleBudget.getMonthlyBudgetUsed();
        BigDecimal monthlyBudgetUsedPercentage = sampleBudget.getMonthlyBudgetUsedPercentage();

        when(expenseRepository.findById(any(UUID.class))).thenReturn(sampleExpense);
        when(budgetRepository.findByCategoryMonthYear(any(Category.class), anyInt(), anyInt())).thenReturn(sampleBudget);
        doNothing().when(expenseRepository).delete(any(Expense.class));

        // when / then
        assertThatNoException().isThrownBy(() -> {
            expenseService.deleteExpense(sampleExpenseId);
        });
        assertThat(sampleExpense.getAccount().getBalance()).isEqualTo(accountBalance.add(sampleExpense.getAmount()));
        assertThat(sampleBudget.getMonthlyBudgetBalance()).isEqualTo(monthlyBudgetBalance.add(sampleExpense.getAmount()));
        assertThat(sampleBudget.getMonthlyBudgetUsed()).isEqualTo(monthlyBudgetUsed.subtract(sampleExpense.getAmount()));
        assertThat(sampleBudget.getMonthlyBudgetUsedPercentage()).isEqualTo(monthlyBudgetUsedPercentage.subtract(sampleExpense.getAmount().multiply(new BigDecimal(100)).divide(sampleBudget.getMonthlyBudget(), RoundingMode.HALF_UP)));

        verify(expenseRepository, times(1)).findById(any(UUID.class));
        verify(budgetRepository, times(1)).findByCategoryMonthYear(any(Category.class), anyInt(), anyInt());
        verify(expenseRepository, times(1)).delete(any(Expense.class));
    }

    @Test
    @DisplayName("Should throw exception on delete a non-existing expense")
    void shouldThrowExceptionOnDeleteNonExistingExpense() {
        // given
        when(expenseRepository.findById(any(UUID.class))).thenReturn(null);

        // when / then
        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() -> {
            expenseService.deleteExpense(sampleExpenseId);
        });

        verify(expenseRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Should delete an expense when budget is null")
    void shouldDeleteExpenseWhenBudgetIsNull() {
        // given
        BigDecimal accountBalance = sampleAccount.getBalance();

        when(expenseRepository.findById(any(UUID.class))).thenReturn(sampleExpense);
        when(budgetRepository.findByCategoryMonthYear(any(Category.class), anyInt(), anyInt())).thenReturn(null);
        doNothing().when(expenseRepository).delete(any(Expense.class));

        // when / then
        assertThatNoException().isThrownBy(() -> {
            expenseService.deleteExpense(sampleExpenseId);
        });
        assertThat(sampleExpense.getAccount().getBalance()).isEqualTo(accountBalance.add(sampleExpense.getAmount()));

        verify(expenseRepository, times(1)).findById(any(UUID.class));
        verify(budgetRepository, times(1)).findByCategoryMonthYear(any(Category.class), anyInt(), anyInt());
        verify(expenseRepository, times(1)).delete(any(Expense.class));
    }
}