package com.ebudget.budget.service;

import com.ebudget.budget.exception.BudgetAlreadyExistsException;
import com.ebudget.budget.model.Budget;
import com.ebudget.budget.repository.BudgetRepository;
import com.ebudget.budget.resource.request.NewBudgetDTO;
import com.ebudget.budget.resource.request.UpdateBudgetDTO;
import com.ebudget.budget.resource.response.BudgetDTO;
import com.ebudget.category.model.Category;
import com.ebudget.category.repository.CategoryRepository;
import com.ebudget.core.exceptions.EntityNotFoundException;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
@DisplayName("Budget Service")
class BudgetServiceTest {
    @Inject
    BudgetService budgetService;
    @InjectMock
    BudgetRepository budgetRepository;
    @InjectMock
    CategoryRepository categoryRepository;

    private UUID sampleBudgetId;
    private Category sampleCategory;
    private Budget sampleBudget;

    @BeforeEach
    void setup() {
        sampleBudgetId = UUID.randomUUID();
        sampleCategory = Category.builder()
                .categoryId(UUID.randomUUID())
                .categoryName("categoryName")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        sampleBudget = Budget.builder()
                .budgetId(sampleBudgetId)
                .budgetMonth(6)
                .budgetYear(2025)
                .category(sampleCategory)
                .monthlyBudget(new BigDecimal("100.00"))
                .monthlyBudgetUsed(new BigDecimal("0.00"))
                .monthlyBudgetUsedPercentage(new BigDecimal("0.00"))
                .monthlyBudgetBalance(new BigDecimal("100.00"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should add a new budget")
    void shoudlAddBudget() {
        when(categoryRepository.findById(any(UUID.class))).thenReturn(sampleCategory);
        when(budgetRepository.countByCategoryMonthYear(anyMap())).thenReturn(0L);
        doNothing().when(budgetRepository).persistAndFlush(any(Budget.class));

        // given
        NewBudgetDTO newBudgetDTO = new NewBudgetDTO(
                6,
                2025,
                UUID.randomUUID(),
                new BigDecimal("100.00")
        );

        // when
        BudgetDTO budget = budgetService.addBudget(newBudgetDTO);

        // then
        assertThat(budget.budgetMonth()).isEqualTo(newBudgetDTO.budgetMonth());
        assertThat(budget.budgetYear()).isEqualTo(newBudgetDTO.budgetYear());
        assertThat(budget.monthlyBudget()).isEqualTo(newBudgetDTO.monthlyBudget());
        assertThat(budget.category().categoryId()).isEqualTo(sampleCategory.getCategoryId());

        verify(categoryRepository, times(1)).findById(any(UUID.class));
        verify(budgetRepository, times(1)).persistAndFlush(any(Budget.class));
    }

    @Test
    @DisplayName("Should throw exception on add budget with a non-existing category")
    void shouldThrowExceptionOnAddBudgetNonExistingCategory() {
        when(categoryRepository.findById(any(UUID.class))).thenReturn(null);

        // given
        NewBudgetDTO newBudgetDTO = new NewBudgetDTO(
                6,
                2025,
                UUID.randomUUID(),
                new BigDecimal("100.00")
        );

        // when / then
        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() -> {
            budgetService.addBudget(newBudgetDTO);
        });

        verify(categoryRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Should throw exception on add a non-unique budget")
    void shouldThrowExceptionOnAddNonUniqueBudget() {
        when(categoryRepository.findById(any(UUID.class))).thenReturn(sampleCategory);
        when(budgetRepository.countByCategoryMonthYear(anyMap())).thenReturn(1L);

        // given
        NewBudgetDTO newBudgetDTO = new NewBudgetDTO(
                6,
                2025,
                UUID.randomUUID(),
                new BigDecimal("100.00")
        );

        // when / then
        assertThatExceptionOfType(BudgetAlreadyExistsException.class).isThrownBy(() -> {
            budgetService.addBudget(newBudgetDTO);
        });

        verify(categoryRepository, times(1)).findById(any(UUID.class));
        verify(budgetRepository, times(1)).countByCategoryMonthYear(anyMap());
    }

    @Test
    @DisplayName("Should update a budget")
    void shouldUpdateBudget() {
        when(budgetRepository.findById(any(UUID.class))).thenReturn(sampleBudget);
        when(budgetRepository.countByCategoryMonthYear(anyMap())).thenReturn(0L);

        // given
        UpdateBudgetDTO updateBudgetDTO = new UpdateBudgetDTO(
                7,
                2025,
                new BigDecimal("10.00")
        );

        // when
        budgetService.updateBudget(sampleBudgetId, updateBudgetDTO);

        // then
        assertThat(sampleBudget.getBudgetId()).isEqualTo(sampleBudgetId);
        assertThat(sampleBudget.getBudgetMonth()).isEqualTo(updateBudgetDTO.budgetMonth());
        assertThat(sampleBudget.getBudgetYear()).isEqualTo(updateBudgetDTO.budgetYear());
        assertThat(sampleBudget.getCategory()).isEqualTo(sampleCategory);
        assertThat(sampleBudget.getMonthlyBudget()).isEqualTo(updateBudgetDTO.monthlyBudget());
        // should add remaining assertions after expenses implementation to check the other props

        verify(budgetRepository, times(1)).findById(any(UUID.class));
        verify(budgetRepository, times(1)).countByCategoryMonthYear(anyMap());
    }

    @Test
    @DisplayName("Should throw an exception on update a non-existing budget")
    void shouldThrowExceptionOnUpdateBudgetNonExistingBudget() {
        when(budgetRepository.findById(any(UUID.class))).thenReturn(null);

        // given
        UpdateBudgetDTO updateBudgetDTO = new UpdateBudgetDTO(
                7,
                2026,
                new BigDecimal("10.00")
        );

        // when // then
        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() -> {
            budgetService.updateBudget(sampleBudgetId, updateBudgetDTO);
        });

        verify(budgetRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Should throw exception on update a non-unique budget")
    void shouldThrowExceptionOnUpdateNonUniqueBudget() {
        when(budgetRepository.findById(any(UUID.class))).thenReturn(sampleBudget);
        when(budgetRepository.countByCategoryMonthYear(anyMap())).thenReturn(1L);

        // given
        UpdateBudgetDTO updateBudgetDTO = new UpdateBudgetDTO(
                6,
                2026,
                new BigDecimal("10.00")
        );

        // when / then
        assertThatExceptionOfType(BudgetAlreadyExistsException.class).isThrownBy(() -> {
            budgetService.updateBudget(sampleBudgetId, updateBudgetDTO);
        });

        verify(budgetRepository, times(1)).findById(any(UUID.class));
        verify(budgetRepository, times(1)).countByCategoryMonthYear(anyMap());
    }

    @Test
    @DisplayName("Should get a budget")
    void shouldGetBudget() {
        // given
        when(budgetRepository.findById(any(UUID.class))).thenReturn(sampleBudget);

        // when
        BudgetDTO budget = budgetService.getBudget(sampleBudgetId);

        // then
        assertThat(budget.budgetId()).isEqualTo(sampleBudget.getBudgetId());
        assertThat(budget.budgetMonth()).isEqualTo(sampleBudget.getBudgetMonth());
        assertThat(budget.budgetYear()).isEqualTo(sampleBudget.getBudgetYear());
        assertThat(budget.category().categoryId()).isEqualTo(sampleBudget.getCategory().getCategoryId());
        assertThat(budget.monthlyBudget()).isEqualTo(sampleBudget.getMonthlyBudget());
        assertThat(budget.monthlyBudgetUsed()).isEqualTo(sampleBudget.getMonthlyBudgetUsed());
        assertThat(budget.monthlyBudgetUsedPercentage()).isEqualTo(sampleBudget.getMonthlyBudgetUsedPercentage());
        assertThat(budget.monthlyBudgetBalance()).isEqualTo(sampleBudget.getMonthlyBudgetBalance());
        assertThat(budget.createdAt()).isEqualTo(sampleBudget.getCreatedAt());
        assertThat(budget.updatedAt()).isEqualTo(sampleBudget.getUpdatedAt());

        verify(budgetRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Should throw exception on get a non-existing budget")
    void shouldThrowExceptionOnGetNonExistingBudget() {
        // given
        when(budgetRepository.findById(any(UUID.class))).thenReturn(null);

        // when / then
        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() -> {
           budgetService.getBudget(sampleBudgetId);
        });

        verify(budgetRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Should get all budgets")
    void shouldGetBudgets() {
        // given
        when(budgetRepository.listAll()).thenReturn(List.of(sampleBudget));

        // when
        List<BudgetDTO> budgets = budgetService.getBudgets();

        // then
        assertThat(budgets).hasSize(1);

        verify(budgetRepository, times(1)).listAll();
    }

    @Test
    @DisplayName("Should delete a budget")
    void shouldDeleteBudget() {
        // given
        when(budgetRepository.findById(any(UUID.class))).thenReturn(sampleBudget);
        doNothing().when(budgetRepository).delete(any(Budget.class));

        // when / then
        assertThatNoException().isThrownBy(() -> {
            budgetService.deleteBudget(sampleBudgetId);
        });

        verify(budgetRepository, times(1)).findById(any(UUID.class));
        verify(budgetRepository, times(1)).delete(any(Budget.class));
    }

    @Test
    @DisplayName("Should throw exception on delete a non-existing budget")
    void shouldThrowExceptionOnDeleteNonExistingBudget() {
        // given
        when(budgetRepository.findById(any(UUID.class))).thenReturn(null);

        // when / then
        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() -> {
            budgetService.deleteBudget(sampleBudgetId);
        });

        verify(budgetRepository, times(1)).findById(any(UUID.class));
    }
}