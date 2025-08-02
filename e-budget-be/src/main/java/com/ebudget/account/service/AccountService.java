package com.ebudget.account.service;

import com.ebudget.account.model.Account;
import com.ebudget.account.repository.AccountRepository;
import com.ebudget.account.resource.request.NewAccountDTO;
import com.ebudget.account.resource.request.UpdateAccountDTO;
import com.ebudget.account.resource.response.AccountDTO;
import com.ebudget.account.service.interfaces.IAccountService;
import com.ebudget.core.exceptions.EntityNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
@RequiredArgsConstructor
public class AccountService implements IAccountService {
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public AccountDTO addAccount(NewAccountDTO newAccountDTO) {
        Account account = Account.builder()
                .financialInstitution(newAccountDTO.financialInstitution())
                .accountName(newAccountDTO.accountName())
                .accountType(newAccountDTO.accountType())
                .initialBalance(newAccountDTO.initialBalance())
                .balance(newAccountDTO.initialBalance())
                .build();

        accountRepository.persistAndFlush(account);

        return new AccountDTO(account);
    }

    @Override
    @Transactional
    public void updateAccount(UUID accountId, UpdateAccountDTO updateAccountDTO) {
        Account account = accountRepository.findById(accountId);

        if(account == null) {
            throw new EntityNotFoundException(Account.class, accountId);
        }

        account.update(updateAccountDTO);
    }

    @Override
    public AccountDTO getAccount(UUID accountId) {
        Account account = accountRepository.findById(accountId);

        if(account == null) {
            throw new EntityNotFoundException(Account.class, accountId);
        }

        return new AccountDTO(account);
    }

    @Override
    public List<AccountDTO> getAccounts() {
        List<Account> accounts = accountRepository.listAll();

        return accounts.stream()
                .map(AccountDTO::new)
                .toList();
    }

    @Override
    @Transactional
    public void deleteAccount(UUID accountId) {
        Account account = accountRepository.findById(accountId);

        if(account == null) {
            throw new EntityNotFoundException(Account.class, accountId);
        }

        accountRepository.deleteById(accountId);
    }
}
