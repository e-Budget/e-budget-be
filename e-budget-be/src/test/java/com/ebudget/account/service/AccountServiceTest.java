package com.ebudget.account.service;

import com.ebudget.account.model.Account;
import com.ebudget.account.model.enums.AccountLogo;
import com.ebudget.account.model.enums.AccountType;
import com.ebudget.account.repository.AccountRepository;
import com.ebudget.account.resource.request.NewAccountDTO;
import com.ebudget.account.resource.request.UpdateAccountDTO;
import com.ebudget.account.resource.response.AccountDTO;
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
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
@DisplayName("Account Service")
class AccountServiceTest {
    @Inject
    AccountService accountService;
    @InjectMock
    AccountRepository accountRepository;

    private UUID sampleAccountId;
    private Account sampleAccount;

    @BeforeEach
    void setup() {
        sampleAccountId = UUID.randomUUID();
        sampleAccount = Account.builder()
                .accountId(sampleAccountId)
                .accountLogo(AccountLogo.NONE)
                .accountName("accountName")
                .accountType(AccountType.BANK_ACCOUNT)
                .initialBalance(new BigDecimal(0))
                .balance(new BigDecimal(0))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should add a new account")
    void shouldAddAccount() {
        // given
        NewAccountDTO newAccountDTO = new NewAccountDTO(
                AccountLogo.NONE,
                "name",
                AccountType.BANK_ACCOUNT,
                new BigDecimal("0.0")
        );

        doNothing().when(accountRepository).persistAndFlush(any(Account.class));

        // when
        AccountDTO account = accountService.addAccount(newAccountDTO);

        // then
        assertThat(account).isInstanceOf(AccountDTO.class);
        assertThat(account.getAccountLogo()).isEqualTo(newAccountDTO.accountLogo());
        assertThat(account.getAccountName()).isEqualTo(newAccountDTO.accountName());
        assertThat(account.getAccountType()).isEqualTo(newAccountDTO.accountType());
        assertThat(account.getInitialBalance()).isEqualTo(newAccountDTO.initialBalance());
        assertThat(account.getBalance()).isEqualTo(newAccountDTO.initialBalance());

        verify(accountRepository, times(1)).persistAndFlush(any(Account.class));
    }

    @Test
    @DisplayName("Should update an account")
    void shouldUpdateAccount() {
        // given
        UpdateAccountDTO updateAccountDTO = new UpdateAccountDTO(
                AccountLogo.SANTANDER,
                "newAccountName",
                AccountType.BENEFIT_ACCOUNT
        );

        when(accountRepository.findById(any(UUID.class))).thenReturn(sampleAccount);

        // when
        accountService.updateAccount(sampleAccountId, updateAccountDTO);

        // then
        assertThat(sampleAccount.getAccountId()).isEqualTo(sampleAccountId);
        assertThat(sampleAccount.getAccountLogo()).isEqualTo(updateAccountDTO.accountLogo());
        assertThat(sampleAccount.getAccountName()).isEqualTo(updateAccountDTO.accountName());
        assertThat(sampleAccount.getAccountType()).isEqualTo(updateAccountDTO.accountType());

        verify(accountRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Should throw exception on update a non-existing account")
    void shouldThrowExceptionOnUpdateNonExistingAccount() {
        // given
        UpdateAccountDTO updateAccountDTO = new UpdateAccountDTO(
                AccountLogo.SANTANDER,
                "newAccountName",
                AccountType.BENEFIT_ACCOUNT
        );

        when(accountRepository.findById(any(UUID.class))).thenReturn(null);

        // when / then
        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() -> {
            accountService.updateAccount(sampleAccountId, updateAccountDTO);
        });

        verify(accountRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Should get an account")
    void shouldGetAccount() {
        // given
        when(accountRepository.findById(any(UUID.class))).thenReturn(sampleAccount);

        // when
        AccountDTO accountDTO = accountService.getAccount(sampleAccountId);

        // then
        assertThat(accountDTO.getAccountId()).isEqualTo(sampleAccount.getAccountId());
        assertThat(accountDTO.getAccountLogo()).isEqualTo(sampleAccount.getAccountLogo());
        assertThat(accountDTO.getAccountName()).isEqualTo(sampleAccount.getAccountName());
        assertThat(accountDTO.getAccountType()).isEqualTo(sampleAccount.getAccountType());
        assertThat(accountDTO.getInitialBalance()).isEqualTo(sampleAccount.getInitialBalance());
        assertThat(accountDTO.getBalance()).isEqualTo(sampleAccount.getBalance());
        // should add createdAt and updatedAt

        verify(accountRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Should throw exception on get an non-existing account")
    void shouldThrowExceptionOnGetNonExistingAccount() {
        // given
        when(accountRepository.findById(any(UUID.class))).thenReturn(null);

        // when / then
        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() -> {
            accountService.getAccount(sampleAccountId);
        });

        verify(accountRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Should get all accounts")
    void shouldGetAllAccounts() {
        // given
        when(accountRepository.listAll()).thenReturn(List.of(sampleAccount));

        // when
        List<AccountDTO> accounts = accountService.getAccounts();

        // then
        assertThat(accounts).hasSize(1);

        verify(accountRepository, times(1)).listAll();
    }

    @Test
    @DisplayName("Should delete an account")
    void shouldDeleteAnAccount() {
        // given
        when(accountRepository.findById(any(UUID.class))).thenReturn(sampleAccount);
        when(accountRepository.deleteById(any(UUID.class))).thenReturn(true);

        // when / then
        assertThatNoException().isThrownBy(() -> accountService.deleteAccount(sampleAccountId));

        verify(accountRepository, times(1)).findById(any(UUID.class));
        verify(accountRepository, times(1)).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("Should throw exception on delete a non-existing account")
    void shouldThrowExceptionOnDeleteNonExistingAccount() {
        // given
        when(accountRepository.findById(any(UUID.class))).thenReturn(null);

        // when / then
        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() -> {
            accountService.deleteAccount(sampleAccountId);
        });

        verify(accountRepository, times(1)).findById(any(UUID.class));
    }
}