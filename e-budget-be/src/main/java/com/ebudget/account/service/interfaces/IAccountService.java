package com.ebudget.account.service.interfaces;

import com.ebudget.account.resource.request.NewAccountDTO;
import com.ebudget.account.resource.request.UpdateAccountDTO;
import com.ebudget.account.resource.response.AccountDTO;

import java.util.List;
import java.util.UUID;

public interface IAccountService {
    AccountDTO addAccount(NewAccountDTO newAccountDTO);
    void updateAccount(UUID accountId, UpdateAccountDTO updateAccountDTO);
    AccountDTO getAccount(UUID accountId);
    List<AccountDTO> getAccounts();
    void deleteAccount(UUID accountId);
}
