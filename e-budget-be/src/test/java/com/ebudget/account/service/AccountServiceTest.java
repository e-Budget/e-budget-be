package com.ebudget.account.service;

import com.ebudget.account.model.Account;
import com.ebudget.account.model.enums.AccountType;
import com.ebudget.account.repository.AccountRepository;
import com.ebudget.account.resource.request.NewAccountDTO;
import com.ebudget.account.resource.response.AccountDTO;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@QuarkusTest
@DisplayName("Account Service")
class AccountServiceTest {
    @Inject
    AccountService accountService;
    @InjectMock
    AccountRepository accountRepository;

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
}