package com.ebudget.budget.resource;

import com.ebudget.budget.model.Budget;
import com.ebudget.budget.repository.BudgetRepository;
import com.ebudget.budget.resource.request.NewBudgetDTO;
import com.ebudget.budget.resource.request.UpdateBudgetDTO;
import com.ebudget.budget.resource.response.BudgetDTO;
import com.ebudget.category.model.Category;
import com.ebudget.category.repository.CategoryRepository;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@DisplayName("Budget Resource")
@TestHTTPEndpoint(BudgetResource.class)
class BudgetResourceTest {
    @Inject
    BudgetRepository budgetRepository;
    @Inject
    CategoryRepository categoryRepository;

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
                .monthlyBudgetUsed(new BigDecimal("0.00"))
                .monthlyBudgetUsedPercentage(new BigDecimal("0.00"))
                .monthlyBudgetBalance(new BigDecimal("100.00"))
                .build();

        budgetRepository.persistAndFlush(sampleBudget);
    }

    @AfterEach
    @Transactional
    void destroy() {
        budgetRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    @DisplayName("Should add a budget")
    void shouldAddBudget() {
        NewBudgetDTO newBudgetDTO = new NewBudgetDTO(
                7,
                2025,
                sampleCategory.getCategoryId(),
                new BigDecimal("100.00")
        );

        BudgetDTO response = given()
            .contentType(ContentType.JSON)
            .body(newBudgetDTO)
        .when()
            .post()
        .then()
            .statusCode(Response.Status.CREATED.getStatusCode())
            .contentType(ContentType.JSON)
            .extract()
            .as(new TypeRef<BudgetDTO>() {});

        assertThat(response.getBudgetId()).isNotNull();
        assertThat(response.getBudgetId()).isInstanceOf(UUID.class);
        assertThat(response.getBudgetMonth()).isEqualTo(newBudgetDTO.budgetMonth());
        assertThat(response.getBudgetYear()).isEqualTo(newBudgetDTO.budgetYear());
        assertThat(response.getCategory().getCategoryId()).isEqualTo(newBudgetDTO.categoryId());
        assertThat(response.getMonthlyBudget()).isEqualTo(newBudgetDTO.monthlyBudget());
        assertThat(response.getMonthlyBudgetUsed()).isEqualTo(new BigDecimal("0.00"));
        assertThat(response.getMonthlyBudgetUsedPercentage()).isEqualTo(new BigDecimal("0.00"));
        assertThat(response.getMonthlyBudgetBalance()).isEqualTo(newBudgetDTO.monthlyBudget());
        assertThat(response.getCreatedAt()).isNotNull();
        assertThat(response.getCreatedAt()).isInstanceOf(LocalDateTime.class);
        assertThat(response.getUpdatedAt()).isNotNull();
        assertThat(response.getUpdatedAt()).isInstanceOf(LocalDateTime.class);
    }

    @Test
    @DisplayName("Should update a budget")
    void shouldUpdateBudget() {
        UpdateBudgetDTO updateBudgetDTO = new UpdateBudgetDTO(
                6,
                2025,
                new BigDecimal("10.00")
        );

        given()
            .contentType(ContentType.JSON)
            .body(updateBudgetDTO)
        .when()
            .put(String.valueOf(sampleBudget.getBudgetId()))
        .then()
            .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    @DisplayName("Should get a budget")
    void shouldGetBudget() {
        BudgetDTO response = given()
            .contentType(ContentType.JSON)
        .when()
            .get(String.valueOf(sampleBudget.getBudgetId()))
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(ContentType.JSON)
            .extract()
            .as(new TypeRef<BudgetDTO>() {});

        assertThat(response.getBudgetId()).isEqualTo(sampleBudget.getBudgetId());
        assertThat(response.getBudgetMonth()).isEqualTo(sampleBudget.getBudgetMonth());
        assertThat(response.getBudgetYear()).isEqualTo(sampleBudget.getBudgetYear());
        assertThat(response.getCategory().getCategoryId()).isEqualTo(sampleBudget.getCategory().getCategoryId());
        assertThat(response.getMonthlyBudget()).isEqualTo(sampleBudget.getMonthlyBudget());
        assertThat(response.getMonthlyBudgetUsed()).isEqualTo(sampleBudget.getMonthlyBudgetUsed());
        assertThat(response.getMonthlyBudgetUsedPercentage()).isEqualTo(sampleBudget.getMonthlyBudgetUsedPercentage());
        assertThat(response.getMonthlyBudgetBalance()).isEqualTo(sampleBudget.getMonthlyBudgetBalance());
        assertThat(response.getCreatedAt()).isNotNull();
        assertThat(response.getCreatedAt()).isInstanceOf(LocalDateTime.class);
        assertThat(response.getUpdatedAt()).isNotNull();
        assertThat(response.getUpdatedAt()).isInstanceOf(LocalDateTime.class);
    }

    @Test
    @DisplayName("Should get all budgets")
    void shouldGetBudgets() {
        List<BudgetDTO> response = given()
            .contentType(ContentType.JSON)
        .when()
            .get()
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(ContentType.JSON)
            .extract()
            .as(new TypeRef<List<BudgetDTO>>() {});

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().getBudgetId()).isEqualTo(sampleBudget.getBudgetId());
        assertThat(response.getFirst().getBudgetMonth()).isEqualTo(sampleBudget.getBudgetMonth());
        assertThat(response.getFirst().getBudgetYear()).isEqualTo(sampleBudget.getBudgetYear());
        assertThat(response.getFirst().getCategory().getCategoryId()).isEqualTo(sampleBudget.getCategory().getCategoryId());
        assertThat(response.getFirst().getMonthlyBudget()).isEqualTo(sampleBudget.getMonthlyBudget());
        assertThat(response.getFirst().getMonthlyBudgetUsed()).isEqualTo(sampleBudget.getMonthlyBudgetUsed());
        assertThat(response.getFirst().getMonthlyBudgetUsedPercentage()).isEqualTo(sampleBudget.getMonthlyBudgetUsedPercentage());
        assertThat(response.getFirst().getMonthlyBudgetBalance()).isEqualTo(sampleBudget.getMonthlyBudgetBalance());
        assertThat(response.getFirst().getCreatedAt()).isNotNull();
        assertThat(response.getFirst().getCreatedAt()).isInstanceOf(LocalDateTime.class);
        assertThat(response.getFirst().getUpdatedAt()).isNotNull();
        assertThat(response.getFirst().getUpdatedAt()).isInstanceOf(LocalDateTime.class);
    }

    @Test
    @DisplayName("Should delete a budget")
    void shouldDeleteBudget() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .delete(String.valueOf(sampleBudget.getBudgetId()))
        .then()
            .statusCode(Response.Status.OK.getStatusCode());
    }
}