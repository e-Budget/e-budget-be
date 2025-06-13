package com.ebudget.account.service;

import com.ebudget.account.AccountNotFoundException;
import com.ebudget.account.model.Account;
import com.ebudget.account.repository.AccountRepository;
import com.ebudget.account.resource.request.NewAccountDTO;
import com.ebudget.account.resource.request.UpdateAccountDTO;
import com.ebudget.account.resource.response.AccountDTO;
import com.ebudget.account.service.interfaces.IAccountService;
import com.ebudget.core.exceptions.InvalidParameterException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
public class AccountService implements IAccountService {
    private final AccountRepository accountRepository;

    @Override
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

    @Override
    @Transactional
    public void updateAccount(UUID accountId, UpdateAccountDTO updateAccountDTO) {
        if(accountId == null) {
            throw new InvalidParameterException();
        }

        Account account = accountRepository.findById(accountId);

        if(account == null) {
            throw new AccountNotFoundException();
        }

        account.update(updateAccountDTO);
    }

    @Override
    public AccountDTO getAccount(UUID accountId) {
        if(accountId == null) {
            throw new InvalidParameterException();
        }

        Account account = accountRepository.findById(accountId);

        if(account == null) {
            throw new AccountNotFoundException();
        }

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

    @Override
    public List<AccountDTO> getAccounts() {
        List<Account> accounts = accountRepository.listAll();

        return accounts.stream().map(account -> new AccountDTO(
                account.getAccountId(),
                account.getAccountLogo(),
                account.getAccountName(),
                account.getAccountType(),
                account.getInitialBalance(),
                account.getBalance(),
                account.getCreatedAt(),
                account.getUpdatedAt()
        )).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteAccount(UUID accountId) {
        if(accountId == null) {
            throw new InvalidParameterException();
        }

        Account account = accountRepository.findById(accountId);

        if(account == null) {
            throw new AccountNotFoundException();
        }

        accountRepository.deleteById(accountId);
    }
}
