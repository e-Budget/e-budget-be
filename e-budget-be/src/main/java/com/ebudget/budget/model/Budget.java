package com.ebudget.budget.model;

import com.ebudget.budget.resource.request.UpdateBudgetDTO;
import com.ebudget.category.model.Category;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
public class Budget {
    @Id
    @Column(name = "budget_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID budgetId;
    @Column(name = "budget_month")
    private Integer budgetMonth;
    @Column(name = "budget_year")
    private Integer budgetYear;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @Column(name = "monthly_budget")
    private BigDecimal monthlyBudget;
    @Column(name = "monthly_budget_used")
    private BigDecimal monthlyBudgetUsed;
    @Column(name = "monthly_budget_used_percentage")
    private BigDecimal monthlyBudgetUsedPercentage;
    @Column(name = "monthly_budget_balance")
    private BigDecimal monthlyBudgetBalance;
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void update(UpdateBudgetDTO updateBudgetDTO) {
        setBudgetMonth(updateBudgetDTO.budgetMonth());
        setBudgetYear(updateBudgetDTO.budgetYear());
        // Logic to update monthlyBudgetUsed / monthlyBudgetUsedPercentage
        // / monthlyBudgetBalance to be implemented along with expenses
        // feature
        setMonthlyBudget(updateBudgetDTO.monthlyBudget());
        setMonthlyBudgetBalance(getMonthlyBudget().add(getMonthlyBudgetUsed().negate()));
        setMonthlyBudgetUsedPercentage(getMonthlyBudgetUsed().multiply(new BigDecimal(100)).divide(getMonthlyBudget(), RoundingMode.HALF_UP));
    }

    public void subtract(BigDecimal amount) {
        setMonthlyBudgetBalance(getMonthlyBudgetBalance().subtract(amount));
        setMonthlyBudgetUsed(getMonthlyBudgetUsed().add(amount));
        setMonthlyBudgetUsedPercentage(getMonthlyBudgetUsed().multiply(new BigDecimal(100)).divide(getMonthlyBudget(), RoundingMode.HALF_UP));
    }

    public void add(BigDecimal amount) {
        setMonthlyBudgetBalance(getMonthlyBudgetBalance().add(amount));
        setMonthlyBudgetUsed(getMonthlyBudgetUsed().subtract(amount));
        setMonthlyBudgetUsedPercentage(getMonthlyBudgetUsed().multiply(new BigDecimal(100)).divide(getMonthlyBudget(), RoundingMode.HALF_UP));
    }
}
