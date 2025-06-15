package com.ebudget.income.service;

import com.ebudget.account.exception.AccountNotFoundException;
import com.ebudget.account.model.Account;
import com.ebudget.account.model.enums.AccountType;
import com.ebudget.account.repository.AccountRepository;
import com.ebudget.core.exceptions.InvalidParameterException;
import com.ebudget.income.exception.IncomeNotFoundException;
import com.ebudget.income.model.Income;
import com.ebudget.income.repository.IncomeRepository;
import com.ebudget.income.resource.request.NewIncomeDTO;
import com.ebudget.income.resource.response.IncomeDTO;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
@DisplayName("Income Service")
class IncomeServiceTest {
    @Inject
    IncomeService incomeService;
    @InjectMock
    IncomeRepository incomeRepository;
    @InjectMock
    AccountRepository accountRepository;

    private Account sampleAccount;
    private UUID sampleIncomeId;
    private Income sampleIncome;

    @BeforeEach
    void setup() {
        sampleAccount = Account.builder()
                .accountId(UUID.randomUUID())
                .accountLogo("accountLogo")
                .accountName("accountName")
                .accountType(AccountType.BANK_ACCOUNT)
                .initialBalance(new BigDecimal(0))
                .balance(new BigDecimal(0))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        sampleIncomeId = UUID.randomUUID();
        sampleIncome = Income.builder()
                .incomeId(sampleIncomeId)
                .incomeDescription("incomeDescription")
                .amount(new BigDecimal("100.00"))
                .account(sampleAccount)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should add an income")
    void shouldAddIncome() {
        // given
        when(accountRepository.findById(any(UUID.class))).thenReturn(sampleAccount);
        doNothing().when(incomeRepository).persistAndFlush(any(Income.class));

        NewIncomeDTO newIncomeDTO = new NewIncomeDTO(
                "incomeDescription",
                new BigDecimal("10.00"),
                sampleAccount.getAccountId()
        );

        // when
        IncomeDTO income = incomeService.addIncome(newIncomeDTO);

        // then
        assertThat(income.incomeDescription()).isEqualTo(newIncomeDTO.incomeDescription());
        assertThat(income.amount()).isEqualTo(newIncomeDTO.amount());
        assertThat(income.account().accountId()).isEqualTo(newIncomeDTO.accountId());

        verify(accountRepository, times(1)).findById(any(UUID.class));
        verify(incomeRepository, times(1)).persistAndFlush(any(Income.class));
    }

    @Test
    @DisplayName("Should throw exception on add an income when account does not exist")
    void shouldThrowExceptionOnAddIncomeWhenAccountDoesNotExist() {
        // given
        when(accountRepository.findById(any(UUID.class))).thenReturn(null);

        NewIncomeDTO newIncomeDTO = new NewIncomeDTO(
                "incomeDescription",
                new BigDecimal("10.00"),
                sampleAccount.getAccountId()
        );

        // when / then
        assertThatExceptionOfType(AccountNotFoundException.class).isThrownBy(() -> incomeService.addIncome(newIncomeDTO));

        verify(accountRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Should update an income when same account")
    void shouldUpdateIncomeWhenSameAccount() {
        // given
        when(incomeRepository.findById(any(UUID.class))).thenReturn(sampleIncome);

        NewIncomeDTO updateIncomeDTO = new NewIncomeDTO(
                "newIncomeDescription",
                new BigDecimal("150.00"),
                sampleAccount.getAccountId()
        );

        // when
        incomeService.updateIncome(sampleIncomeId, updateIncomeDTO);

        // then
        assertThat(sampleIncome.getIncomeId()).isEqualTo(sampleIncomeId);
        assertThat(sampleIncome.getIncomeDescription()).isEqualTo(updateIncomeDTO.incomeDescription());
        assertThat(sampleIncome.getAmount()).isEqualTo(updateIncomeDTO.amount());
        assertThat(sampleIncome.getAccount().getAccountId()).isEqualTo(updateIncomeDTO.accountId());

        verify(incomeRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Should update an income when different account")
    void shouldUpdateIncomeWhenDifferentAccount() {
        // given
        Account account = Account.builder()
                .accountId(UUID.randomUUID())
                .accountLogo("accountLogo")
                .accountName("accountName")
                .accountType(AccountType.BANK_ACCOUNT)
                .initialBalance(new BigDecimal(0))
                .balance(new BigDecimal(0))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(incomeRepository.findById(any(UUID.class))).thenReturn(sampleIncome);
        when(accountRepository.findById(any(UUID.class))).thenReturn(account);

        NewIncomeDTO updateIncomeDTO = new NewIncomeDTO(
                "newIncomeDescription",
                new BigDecimal("150.00"),
                account.getAccountId()
        );

        // when
        incomeService.updateIncome(sampleIncomeId, updateIncomeDTO);

        // then
        assertThat(sampleIncome.getIncomeId()).isEqualTo(sampleIncomeId);
        assertThat(sampleIncome.getIncomeDescription()).isEqualTo(updateIncomeDTO.incomeDescription());
        assertThat(sampleIncome.getAmount()).isEqualTo(updateIncomeDTO.amount());
        assertThat(sampleIncome.getAccount().getAccountId()).isEqualTo(updateIncomeDTO.accountId());

        verify(incomeRepository, times(1)).findById(any(UUID.class));
        verify(accountRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Should throw exception on update an income when incomeId is null")
    void shouldThrowExceptionOnUpdateIncomeWhenIncomeIdIsNull() {
        // given
        UUID incomeId = null;

        NewIncomeDTO updateIncomeDTO = new NewIncomeDTO(
                "newIncomeDescription",
                new BigDecimal("150.00"),
                sampleIncomeId
        );

        // when / then
        assertThatExceptionOfType(InvalidParameterException.class).isThrownBy(() -> incomeService.updateIncome(incomeId, updateIncomeDTO));
    }

    @Test
    @DisplayName("Should throw exception on update a non-existing income")
    void shouldThrowExceptionOnUpdateNonExistingIncome() {
        // given
        when(incomeRepository.findById(any(UUID.class))).thenReturn(null);

        NewIncomeDTO updateIncomeDTO = new NewIncomeDTO(
                "newIncomeDescription",
                new BigDecimal("150.00"),
                sampleIncomeId
        );

        // when / then
        assertThatExceptionOfType(IncomeNotFoundException.class).isThrownBy(() -> incomeService.updateIncome(sampleIncomeId, updateIncomeDTO));

        verify(incomeRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Should get an income")
    void shouldGetIncome() {
        // given
        when(incomeRepository.findById(any(UUID.class))).thenReturn(sampleIncome);

        // when
        IncomeDTO income = incomeService.getIncome(sampleIncomeId);

        // then
        assertThat(income.incomeId()).isEqualTo(sampleIncome.getIncomeId());
        assertThat(income.incomeDescription()).isEqualTo(sampleIncome.getIncomeDescription());
        assertThat(income.amount()).isEqualTo(sampleIncome.getAmount());
        assertThat(income.account().accountId()).isEqualTo(sampleIncome.getAccount().getAccountId());
        assertThat(income.createdAt()).isEqualTo(sampleIncome.getCreatedAt());
        assertThat(income.updatedAt()).isEqualTo(sampleIncome.getUpdatedAt());

        verify(incomeRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Should throw exception on get an income when incomeId is null")
    void shouldThrowExceptionOnGetIncomeWhenIncomeIdIsNull() {
        // given
        UUID incomeId = null;

        // when / then
        assertThatExceptionOfType(InvalidParameterException.class).isThrownBy(() -> incomeService.getIncome(incomeId));
    }

    @Test
    @DisplayName("Should throw exception on get a non-existing income")
    void shouldThrowExceptionOnGetNonExistingIncome() {
        // given
        when(incomeRepository.findById(any(UUID.class))).thenReturn(null);

        // when / then
        assertThatExceptionOfType(IncomeNotFoundException.class).isThrownBy(() -> incomeService.getIncome(sampleIncomeId));
    }

    @Test
    @DisplayName("Should get incomes")
    void shouldGetIncomes() {
        // given
        when(incomeRepository.listAll()).thenReturn(List.of(sampleIncome));

        // when
        List<IncomeDTO> incomes = incomeService.getIncomes();

        // then
        assertThat(incomes.getFirst().incomeId()).isEqualTo(sampleIncome.getIncomeId());
        assertThat(incomes.getFirst().incomeDescription()).isEqualTo(sampleIncome.getIncomeDescription());
        assertThat(incomes.getFirst().amount()).isEqualTo(sampleIncome.getAmount());
        assertThat(incomes.getFirst().account().accountId()).isEqualTo(sampleIncome.getAccount().getAccountId());
        assertThat(incomes.getFirst().createdAt()).isEqualTo(sampleIncome.getCreatedAt());
        assertThat(incomes.getFirst().updatedAt()).isEqualTo(sampleIncome.getUpdatedAt());

        verify(incomeRepository, times(1)).listAll();
    }

    @Test
    @DisplayName("Should delete an income")
    void shouldDeleteIncome() {
        // given
        when(incomeRepository.findById(any(UUID.class))).thenReturn(sampleIncome);
        doNothing().when(incomeRepository).delete(any(Income.class));

        // when / then
        assertThatNoException().isThrownBy(() -> incomeService.deleteIncome(sampleIncomeId));

        verify(incomeRepository, times(1)).findById(any(UUID.class));
        verify(incomeRepository, times(1)).delete(any(Income.class));
    }

    @Test
    @DisplayName("Should throw exception on delete an income when incomeId is null")
    void shouldThrowExceptionOnDeleteIncomeWhenIncomeIdIsNull() {
        // given
        UUID incomeId = null;

        // when / then
        assertThatExceptionOfType(InvalidParameterException.class).isThrownBy(() -> incomeService.deleteIncome(incomeId));
    }

    @Test
    @DisplayName("Should throw exception on delete a non-existing income")
    void shouldThrowExceptionOnDeleteNonExistingIncome() {
        // given
        when(incomeRepository.findById(any(UUID.class))).thenReturn(null);

        // when / then
        assertThatExceptionOfType(IncomeNotFoundException.class).isThrownBy(() -> incomeService.deleteIncome(sampleIncomeId));

        verify(incomeRepository, times(1)).findById(any(UUID.class));
    }
}