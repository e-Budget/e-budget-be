package com.ebudget.income.model;

import com.ebudget.account.model.Account;
import com.ebudget.income.resource.request.UpdateIncomeDTO;
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
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
public class Income {
    @Id
    @Column(name = "income_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID incomeId;
    @Column(name = "income_description")
    private String incomeDescription;
    private BigDecimal amount;
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void update(UpdateIncomeDTO updateIncomeDTO) {
        setIncomeDescription(updateIncomeDTO.incomeDescription());
        setAmount(updateIncomeDTO.amount());
    }

    public void update(UpdateIncomeDTO updateIncomeDTO, Account account) {
        setIncomeDescription(updateIncomeDTO.incomeDescription());
        setAmount(updateIncomeDTO.amount());
        setAccount(account);
    }
}
