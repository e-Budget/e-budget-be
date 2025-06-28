package com.ebudget.transfer.service;

import com.ebudget.account.model.Account;
import com.ebudget.account.repository.AccountRepository;
import com.ebudget.core.exceptions.EntityNotFoundException;
import com.ebudget.transfer.exception.RecipientAccountNotFoundException;
import com.ebudget.transfer.exception.SenderAccountNotFoundException;
import com.ebudget.transfer.model.Transfer;
import com.ebudget.transfer.repository.TransferRepository;
import com.ebudget.transfer.resource.request.NewTransferDTO;
import com.ebudget.transfer.resource.response.TransferDTO;
import com.ebudget.transfer.service.interfaces.ITransferService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
@RequiredArgsConstructor
public class TransferService implements ITransferService {
    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public TransferDTO addTransfer(NewTransferDTO newTransferDTO) {
        Account senderBankAccount = accountRepository.findById(newTransferDTO.fromAccount());

        if(senderBankAccount == null) {
            throw new SenderAccountNotFoundException(
                    Map.of("accountId", newTransferDTO.fromAccount())
            );
        }

        Account recipientBankAccount = accountRepository.findById(newTransferDTO.toAccount());

        if(recipientBankAccount == null) {
            throw new RecipientAccountNotFoundException(
                    Map.of("accountId", newTransferDTO.toAccount())
            );
        }

        processTransfer(senderBankAccount, recipientBankAccount, newTransferDTO.amount());

        Transfer transfer = Transfer.builder()
                .transferDescription(newTransferDTO.transferDescription())
                .amount(newTransferDTO.amount())
                .fromAccount(senderBankAccount)
                .toAccount(recipientBankAccount)
                .build();

        transferRepository.persistAndFlush(transfer);

        return new TransferDTO(transfer);
    }

    @Override
    @Transactional
    public void deleteTransfer(UUID transferId) {
        Transfer transfer = transferRepository.findById(transferId);

        if(transfer == null) {
            throw new EntityNotFoundException(Transfer.class, transferId);
        }

        processTransfer(transfer.getToAccount(), transfer.getFromAccount(), transfer.getAmount());

        transferRepository.delete(transfer);
    }

    @Override
    public TransferDTO getTransfer(UUID transferId) {
        Transfer transfer = transferRepository.findById(transferId);

        if(transfer == null) {
            throw new EntityNotFoundException(Transfer.class, transferId);
        }

        return new TransferDTO(transfer);
    }

    @Override
    public List<TransferDTO> getTransfers() {
        List<Transfer> transfers = transferRepository.listAll();

        return transfers.stream()
                .map(TransferDTO::new)
                .toList();
    }

    private void processTransfer(Account senderBankAccount, Account recipientBankAccount, BigDecimal amount) {
        senderBankAccount.withdraw(amount);
        recipientBankAccount.deposit(amount);
    }
}
