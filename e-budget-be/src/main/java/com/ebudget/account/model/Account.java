package com.ebudget.account.model;

import com.ebudget.account.model.enums.FinancialInstitution;
import com.ebudget.account.model.enums.AccountType;
import com.ebudget.account.resource.request.UpdateAccountDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class Account {
    @Id
    @Column(name = "account_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID accountId;
    @Enumerated(EnumType.STRING)
    @Column(name = "account_logo")
    private FinancialInstitution financialInstitution;
    @Column(name = "account_name")
    private String accountName;
    @Enumerated(EnumType.STRING)
    @Column(name = "account_type")
    private AccountType accountType;
    @Column(name = "initial_balance")
    private BigDecimal initialBalance;
    private BigDecimal balance;
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void update(UpdateAccountDTO updateAccountDTO) {
        setFinancialInstitution(updateAccountDTO.financialInstitution());
        setAccountName(updateAccountDTO.accountName());
        setAccountType(updateAccountDTO.accountType());
    }

    public void withdraw(BigDecimal amount) {
        setBalance(getBalance().subtract(amount));
    }

    public void deposit(BigDecimal amount) {
        setBalance(getBalance().add(amount));
    }
}
