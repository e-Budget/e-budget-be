package com.ebudget.account.service;

import com.ebudget.account.model.Account;
import com.ebudget.account.model.enumeration.AccountType;
import com.ebudget.account.repository.AccountRepository;
import com.ebudget.account.resource.request.NewAccountDTO;
import com.ebudget.account.resource.response.AccountDTO;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;

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
        // given
        NewAccountDTO newAccountDTO = new NewAccountDTO(
                "logo",
                "name",
                AccountType.BANK_ACCOUNT,
                0.0
        );

        doNothing().when(accountRepository).persistAndFlush(any(Account.class));

        // when
        AccountDTO newAccount = accountService.addAccount(newAccountDTO);

        // then
        assertThat(newAccount).isInstanceOf(AccountDTO.class);
        assertThat(newAccount.accountLogo()).isEqualTo(newAccountDTO.accountLogo());
        assertThat(newAccount.accountName()).isEqualTo(newAccountDTO.accountName());
        assertThat(newAccount.accountType()).isEqualTo(newAccountDTO.accountType());
        assertThat(newAccount.initialBalance()).isEqualTo(newAccountDTO.initialBalance());
        assertThat(newAccount.balance()).isEqualTo(newAccountDTO.initialBalance());
    }
}