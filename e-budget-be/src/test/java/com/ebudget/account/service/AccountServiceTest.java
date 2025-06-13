package com.ebudget.account.service;

import com.ebudget.account.AccountNotFoundException;
import com.ebudget.account.model.Account;
import com.ebudget.account.model.enums.AccountType;
import com.ebudget.account.repository.AccountRepository;
import com.ebudget.account.resource.request.NewAccountDTO;
import com.ebudget.account.resource.request.UpdateAccountDTO;
import com.ebudget.account.resource.response.AccountDTO;
import com.ebudget.core.exceptions.InvalidParameterException;
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
                .accountLogo("accountLogo")
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
        doNothing().when(accountRepository).persistAndFlush(any(Account.class));

        // given
        NewAccountDTO newAccountDTO = new NewAccountDTO(
                "logo",
                "name",
                AccountType.BANK_ACCOUNT,
                new BigDecimal("0.0")
        );

        // when
        AccountDTO account = accountService.addAccount(newAccountDTO);

        // then
        assertThat(account).isInstanceOf(AccountDTO.class);
        assertThat(account.accountLogo()).isEqualTo(newAccountDTO.accountLogo());
        assertThat(account.accountName()).isEqualTo(newAccountDTO.accountName());
        assertThat(account.accountType()).isEqualTo(newAccountDTO.accountType());
        assertThat(account.initialBalance()).isEqualTo(newAccountDTO.initialBalance());
        assertThat(account.balance()).isEqualTo(newAccountDTO.initialBalance());

        verify(accountRepository, times(1)).persistAndFlush(any(Account.class));
    }

    @Test
    @DisplayName("Should update an account")
    void shouldUpdateAccount() {
        // given
        UpdateAccountDTO updateAccountDTO = new UpdateAccountDTO(
                "newAccountLogo",
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
    @DisplayName("Should throw exception on update an account when accountId is null")
    void shouldThrowExceptionOnUpdateAccountWhenAccountIdIsNull() {
        // given
        UUID accountId = null;

        UpdateAccountDTO updateAccountDTO = new UpdateAccountDTO(
                "newAccountLogo",
                "newAccountName",
                AccountType.BENEFIT_ACCOUNT
        );

        // when / then
        assertThatExceptionOfType(InvalidParameterException.class).isThrownBy(() -> {
            accountService.updateAccount(accountId, updateAccountDTO);
        });
    }

    @Test
    @DisplayName("Should throw exception on update a non-existing account")
    void shouldThrowExceptionOnUpdateNonExistingAccount() {
        // given
        UpdateAccountDTO updateAccountDTO = new UpdateAccountDTO(
                "newAccountLogo",
                "newAccountName",
                AccountType.BENEFIT_ACCOUNT
        );

        when(accountRepository.findById(any(UUID.class))).thenReturn(null);

        // when / then
        assertThatExceptionOfType(AccountNotFoundException.class).isThrownBy(() -> {
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
        assertThat(accountDTO.accountId()).isEqualTo(sampleAccount.getAccountId());
        assertThat(accountDTO.accountLogo()).isEqualTo(sampleAccount.getAccountLogo());
        assertThat(accountDTO.accountName()).isEqualTo(sampleAccount.getAccountName());
        assertThat(accountDTO.accountType()).isEqualTo(sampleAccount.getAccountType());
        assertThat(accountDTO.initialBalance()).isEqualTo(sampleAccount.getInitialBalance());
        assertThat(accountDTO.balance()).isEqualTo(sampleAccount.getBalance());

        verify(accountRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Should throw exception on get an account when accountId is null")
    void shouldThrowExceptionOnGetAccountWhenAccountIdIsNull() {
        // given
        UUID accountId = null;

        // when / then
        assertThatExceptionOfType(InvalidParameterException.class).isThrownBy(() -> {
            accountService.getAccount(accountId);
        });
    }

    @Test
    @DisplayName("Should throw exception on get an non-existing account")
    void shouldThrowExceptionOnGetNonExistingAccount() {
        // given
        when(accountRepository.findById(any(UUID.class))).thenReturn(null);

        // when / then
        assertThatExceptionOfType(AccountNotFoundException.class).isThrownBy(() -> {
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
    @DisplayName("Should throw exception on delete an account when accountId is null")
    void shouldThrowExceptionOnDeleteAnAccountWhenAccountIdIsNull() {
        // given
        UUID accountId = null;

        // when / then
        assertThatExceptionOfType(InvalidParameterException.class).isThrownBy(() -> {
            accountService.deleteAccount(accountId);
        });
    }

    @Test
    @DisplayName("Should throw exception on delete a non-existing account")
    void shouldThrowExceptionOnDeleteNonExistingAccount() {
        // given
        when(accountRepository.findById(any(UUID.class))).thenReturn(null);

        // when / then
        assertThatExceptionOfType(AccountNotFoundException.class).isThrownBy(() -> {
            accountService.deleteAccount(sampleAccountId);
        });

        verify(accountRepository, times(1)).findById(any(UUID.class));
    }
}