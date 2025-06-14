package com.ebudget.income.service;

import com.ebudget.account.AccountNotFoundException;
import com.ebudget.account.model.Account;
import com.ebudget.account.repository.AccountRepository;
import com.ebudget.account.resource.response.AccountDTO;
import com.ebudget.core.exceptions.InvalidParameterException;
import com.ebudget.income.exception.IncomeNotFoundException;
import com.ebudget.income.model.Income;
import com.ebudget.income.repository.IncomeRepository;
import com.ebudget.income.resource.request.NewIncomeDTO;
import com.ebudget.income.resource.response.IncomeDTO;
import com.ebudget.income.service.interfaces.IIncomeService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
public class IncomeService implements IIncomeService {
    private final IncomeRepository incomeRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public IncomeDTO addIncome(NewIncomeDTO newIncomeDTO) {
        Account account = accountRepository.findById(newIncomeDTO.accountId());

        if(account == null) {
            throw new AccountNotFoundException();
        }

        processIncome(account, newIncomeDTO.amount());

        Income income = Income.builder()
                .incomeDescription(newIncomeDTO.incomeDescription())
                .amount(newIncomeDTO.amount())
                .account(account)
                .build();

        incomeRepository.persistAndFlush(income);

        return new IncomeDTO(
                income.getIncomeId(),
                income.getIncomeDescription(),
                income.getAmount(),
                new AccountDTO(
                        income.getAccount().getAccountId(),
                        income.getAccount().getAccountLogo(),
                        income.getAccount().getAccountName(),
                        income.getAccount().getAccountType(),
                        income.getAccount().getInitialBalance(),
                        income.getAccount().getBalance(),
                        income.getAccount().getCreatedAt(),
                        income.getAccount().getUpdatedAt()
                ),
                income.getCreatedAt(),
                income.getUpdatedAt()
        );
    }

    @Override
    @Transactional
    public void updateIncome(UUID incomeId, NewIncomeDTO updateIncomeDTO) {
        if(incomeId == null) {
            throw new InvalidParameterException();
        }

        Income income = incomeRepository.findById(incomeId);

        if(income == null) {
            throw new IncomeNotFoundException();
        }

        if(income.getAccount().getAccountId().equals(updateIncomeDTO.accountId())) {
            processIncome(income.getAccount(), income.getAmount().negate(), updateIncomeDTO.amount());

            income.update(updateIncomeDTO);
        } else {
            Account withdrawAccount = income.getAccount();
            Account depositAccount = accountRepository.findById(updateIncomeDTO.accountId());

            processIncome(withdrawAccount, income.getAmount().negate(), depositAccount, updateIncomeDTO.amount());

            income.update(updateIncomeDTO, depositAccount);
        }
    }

    @Override
    public IncomeDTO getIncome(UUID incomeId) {
        if(incomeId == null) {
            throw new InvalidParameterException();
        }

        Income income = incomeRepository.findById(incomeId);

        if(income == null) {
            throw new IncomeNotFoundException();
        }

        return new IncomeDTO(
                income.getIncomeId(),
                income.getIncomeDescription(),
                income.getAmount(),
                new AccountDTO(
                        income.getAccount().getAccountId(),
                        income.getAccount().getAccountLogo(),
                        income.getAccount().getAccountName(),
                        income.getAccount().getAccountType(),
                        income.getAccount().getInitialBalance(),
                        income.getAccount().getBalance(),
                        income.getAccount().getCreatedAt(),
                        income.getAccount().getUpdatedAt()
                ),
                income.getCreatedAt(),
                income.getUpdatedAt()
        );
    }

    @Override
    public List<IncomeDTO> getIncomes() {
        List<Income> incomes = incomeRepository.listAll();

        return incomes.stream()
                .map(income -> new IncomeDTO(
                        income.getIncomeId(),
                        income.getIncomeDescription(),
                        income.getAmount(),
                        new AccountDTO(
                                income.getAccount().getAccountId(),
                                income.getAccount().getAccountLogo(),
                                income.getAccount().getAccountName(),
                                income.getAccount().getAccountType(),
                                income.getAccount().getInitialBalance(),
                                income.getAccount().getBalance(),
                                income.getAccount().getCreatedAt(),
                                income.getAccount().getUpdatedAt()
                        ),
                        income.getCreatedAt(),
                        income.getUpdatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteIncome(UUID incomeId) {
        if(incomeId == null) {
            throw new InvalidParameterException();
        }

        Income income = incomeRepository.findById(incomeId);

        if(income == null) {
            throw new IncomeNotFoundException();
        }

        processIncome(income.getAccount(), income.getAmount().negate());

        incomeRepository.delete(income);
    }

    private void processIncome(Account account, BigDecimal amount) {
        account.updateBalance(amount);
    }

    private void processIncome(Account account, BigDecimal withdrawAmount, BigDecimal depositAmount) {
        account.updateBalance(withdrawAmount);
        account.updateBalance(depositAmount);
    }

    private void processIncome(Account withdrawAccount, BigDecimal withdrawAmount, Account depositAccount, BigDecimal depositAmount) {
        withdrawAccount.updateBalance(withdrawAmount);
        depositAccount.updateBalance(depositAmount);
    }
}
