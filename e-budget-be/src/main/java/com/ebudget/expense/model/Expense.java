package com.ebudget.expense.model;

import com.ebudget.account.model.Account;
import com.ebudget.category.model.Category;
import com.ebudget.expense.resource.request.UpdateExpenseDTO;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
public class Expense {
    @Id
    @Column(name = "expense_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID expenseId;
    @Column(name = "expense_description")
    private String expenseDescription;
    @Column(name = "expense_month")
    private Integer expenseMonth;
    @Column(name = "expense_year")
    private Integer expenseYear;
    private BigDecimal amount;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
    private LocalDate date;
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void update(UpdateExpenseDTO updateExpenseDTO) {
        setExpenseDescription(updateExpenseDTO.expenseDescription());
        setAmount(updateExpenseDTO.amount());
        setDate(updateExpenseDTO.date());
    }

    public void bindCategory(Category category) {
        setCategory(category);
    }

    public void bindAccount(Account account) {
        setAccount(account);
    }
}
