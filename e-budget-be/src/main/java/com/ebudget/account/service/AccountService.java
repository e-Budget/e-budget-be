package com.ebudget.account.service;

import com.ebudget.account.model.Account;
import com.ebudget.account.repository.AccountRepository;
import com.ebudget.account.resource.request.NewAccountDTO;
import com.ebudget.account.resource.response.AccountDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    @Transactional
    public AccountDTO addAccount(NewAccountDTO newAccountDTO) {
        Account account = Account.builder()
                .accountLogo(newAccountDTO.accountLogo())
                .accountName(newAccountDTO.accountName())
                .accountType(newAccountDTO.accountType())
                .initialBalance(newAccountDTO.initialBalance())
                .balance(newAccountDTO.initialBalance())
                .build();

        accountRepository.persistAndFlush(account);

        return new AccountDTO(
                account.getAccountId(),
                account.getAccountLogo(),
                account.getAccountName(),
                account.getAccountType(),
                account.getInitialBalance(),
                account.getBalance(),
                account.getCreatedAt(),
                account.getUpdatedAt()
        );
    }
}
