package com.ebudget.expense.resource;

import com.ebudget.account.model.Account;
import com.ebudget.account.model.enums.AccountType;
import com.ebudget.account.repository.AccountRepository;
import com.ebudget.budget.model.Budget;
import com.ebudget.budget.repository.BudgetRepository;
import com.ebudget.category.model.Category;
import com.ebudget.category.repository.CategoryRepository;
import com.ebudget.expense.model.Expense;
import com.ebudget.expense.repository.ExpenseRepository;
import com.ebudget.expense.resource.request.NewExpenseDTO;
import com.ebudget.expense.resource.request.UpdateExpenseDTO;
import com.ebudget.expense.resource.response.ExpenseDTO;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@DisplayName("Expense Resource")
@TestHTTPEndpoint(ExpenseResource.class)
class ExpenseResourceTest {
    @Inject
    ExpenseRepository expenseRepository;
    @Inject
    AccountRepository accountRepository;
    @Inject
    CategoryRepository categoryRepository;
    @Inject
    BudgetRepository budgetRepository;

    private Expense sampleExpense;
    private Account sampleAccount;
    private Category sampleCategory;
    private Budget sampleBudget;

    @BeforeEach
    @Transactional
    void setup() {
        sampleCategory = Category.builder()
                .categoryName("categoryName")
                .build();
        categoryRepository.persistAndFlush(sampleCategory);

        sampleBudget = Budget.builder()
                .budgetMonth(6)
                .budgetYear(2025)
                .category(sampleCategory)
                .monthlyBudget(new BigDecimal("100.00"))
                .monthlyBudgetUsed(new BigDecimal("10.00"))
                .monthlyBudgetUsedPercentage(new BigDecimal("10.00"))
                .monthlyBudgetBalance(new BigDecimal("90.00"))
                .build();
        budgetRepository.persistAndFlush(sampleBudget);

        sampleAccount = Account.builder()
                .accountLogo("accountLogo")
                .accountName("accountName")
                .accountType(AccountType.BANK_ACCOUNT)
                .initialBalance(new BigDecimal("100.00"))
                .balance(new BigDecimal("90.00"))
                .build();
        accountRepository.persistAndFlush(sampleAccount);

        sampleExpense = Expense.builder()
                .expenseDescription("expenseDescription")
                .amount(new BigDecimal("10.00"))
                .date(LocalDate.of(2025, 6, 27))
                .expenseMonth(6)
                .expenseYear(2025)
                .account(sampleAccount)
                .category(sampleCategory)
                .build();
        expenseRepository.persistAndFlush(sampleExpense);
    }

    @AfterEach
    @Transactional
    void destroy() {
        expenseRepository.deleteAll();
        accountRepository.deleteAll();
        budgetRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    @DisplayName("Should add an expense")
    void shouldAddExpense() {
        NewExpenseDTO newExpenseDTO = new NewExpenseDTO(
                "expenseDescription",
                6,
                2025,
                new BigDecimal("10.00"),
                sampleCategory.getCategoryId(),
                sampleAccount.getAccountId(),
                LocalDate.of(2025, 6, 27)
        );

        ExpenseDTO response = given()
            .contentType(ContentType.JSON)
            .body(newExpenseDTO)
        .when()
            .post()
        .then()
            .statusCode(Response.Status.CREATED.getStatusCode())
            .contentType(ContentType.JSON)
            .extract()
            .as(new TypeRef<ExpenseDTO>() {});

        assertThat(response.getExpenseId()).isNotNull();
        assertThat(response.getExpenseId()).isInstanceOf(UUID.class);
        assertThat(response.getExpenseDescription()).isEqualTo(newExpenseDTO.expenseDescription());
        assertThat(response.getExpenseMonth()).isEqualTo(newExpenseDTO.expenseMonth());
        assertThat(response.getExpenseYear()).isEqualTo(newExpenseDTO.expenseYear());
        assertThat(response.getDate()).isEqualTo(newExpenseDTO.date());
        assertThat(response.getAmount()).isEqualTo(newExpenseDTO.amount());
        assertThat(response.getCategory().categoryId()).isEqualTo(newExpenseDTO.categoryId());
        assertThat(response.getAccount().accountId()).isEqualTo(newExpenseDTO.accountId());
        assertThat(response.getCreatedAt()).isNotNull();
        assertThat(response.getCreatedAt()).isInstanceOf(LocalDateTime.class);
        assertThat(response.getUpdatedAt()).isNotNull();
        assertThat(response.getUpdatedAt()).isInstanceOf(LocalDateTime.class);
    }

    @Test
    @DisplayName("Should update an expense")
    void shouldUpdateExpense() {
        UpdateExpenseDTO updateExpenseDTO = new UpdateExpenseDTO(
                "newExpenseDescription",
                6,
                2025,
                new BigDecimal("5.00"),
                sampleCategory.getCategoryId(),
                sampleAccount.getAccountId(),
                LocalDate.of(2025, 6, 28)
        );

        given()
            .contentType(ContentType.JSON)
            .body(updateExpenseDTO)
        .when()
            .put(String.valueOf(sampleExpense.getExpenseId()))
        .then()
            .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    @DisplayName("Should get an expense")
    void shouldGetExpense() {
        ExpenseDTO response = given()
            .contentType(ContentType.JSON)
        .when()
            .get(String.valueOf(sampleExpense.getExpenseId()))
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(ContentType.JSON)
            .extract()
            .as(new TypeRef<ExpenseDTO>() {});

        assertThat(response.getExpenseId()).isEqualTo(sampleExpense.getExpenseId());
        assertThat(response.getExpenseDescription()).isEqualTo(sampleExpense.getExpenseDescription());
        assertThat(response.getExpenseMonth()).isEqualTo(sampleExpense.getExpenseMonth());
        assertThat(response.getExpenseYear()).isEqualTo(sampleExpense.getExpenseYear());
        assertThat(response.getDate()).isEqualTo(sampleExpense.getDate());
        assertThat(response.getAmount()).isEqualTo(sampleExpense.getAmount());
        assertThat(response.getCategory().categoryId()).isEqualTo(sampleExpense.getCategory().getCategoryId());
        assertThat(response.getAccount().accountId()).isEqualTo(sampleExpense.getAccount().getAccountId());
        assertThat(response.getCreatedAt()).isEqualTo(sampleExpense.getCreatedAt());
        assertThat(response.getUpdatedAt()).isEqualTo(sampleExpense.getUpdatedAt());
    }

    @Test
    @DisplayName("Should get all expenses")
    void shouldGetExpenses() {
        List<ExpenseDTO> response = given()
            .contentType(ContentType.JSON)
        .when()
            .get()
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(ContentType.JSON)
            .extract()
            .as(new TypeRef<List<ExpenseDTO>>() {});

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().getExpenseId()).isEqualTo(sampleExpense.getExpenseId());
        assertThat(response.getFirst().getExpenseDescription()).isEqualTo(sampleExpense.getExpenseDescription());
        assertThat(response.getFirst().getExpenseMonth()).isEqualTo(sampleExpense.getExpenseMonth());
        assertThat(response.getFirst().getExpenseYear()).isEqualTo(sampleExpense.getExpenseYear());
        assertThat(response.getFirst().getDate()).isEqualTo(sampleExpense.getDate());
        assertThat(response.getFirst().getAmount()).isEqualTo(sampleExpense.getAmount());
        assertThat(response.getFirst().getCategory().categoryId()).isEqualTo(sampleExpense.getCategory().getCategoryId());
        assertThat(response.getFirst().getAccount().accountId()).isEqualTo(sampleExpense.getAccount().getAccountId());
        assertThat(response.getFirst().getCreatedAt()).isEqualTo(sampleExpense.getCreatedAt());
        assertThat(response.getFirst().getUpdatedAt()).isEqualTo(sampleExpense.getUpdatedAt());
    }

    @Test
    @DisplayName("Should delete an expense")
    void shouldDeleteExpense() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .delete(String.valueOf(sampleExpense.getExpenseId()))
        .then()
            .statusCode(Response.Status.OK.getStatusCode());
    }
}