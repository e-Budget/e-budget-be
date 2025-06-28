package com.ebudget.transfer.service;

import com.ebudget.account.model.Account;
import com.ebudget.account.model.enums.AccountLogo;
import com.ebudget.account.model.enums.AccountType;
import com.ebudget.account.repository.AccountRepository;
import com.ebudget.core.exceptions.EntityNotFoundException;
import com.ebudget.transfer.exception.RecipientAccountNotFoundException;
import com.ebudget.transfer.exception.SenderAccountNotFoundException;
import com.ebudget.transfer.model.Transfer;
import com.ebudget.transfer.repository.TransferRepository;
import com.ebudget.transfer.resource.request.NewTransferDTO;
import com.ebudget.transfer.resource.response.TransferDTO;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
@DisplayName("Transfer Service")
class TransferServiceTest {
    @Inject
    TransferService transferService;
    @InjectMock
    TransferRepository transferRepository;
    @InjectMock
    AccountRepository accountRepository;

    private Account sampleFromAccount;
    private Account sampleToAccount;
    private UUID sampleTransferId;
    private Transfer sampleTransfer;

    @BeforeEach
    void setup() {
        sampleFromAccount = Account.builder()
                .accountId(UUID.randomUUID())
                .accountLogo(AccountLogo.NONE)
                .accountName("fromAccountName")
                .accountType(AccountType.BANK_ACCOUNT)
                .initialBalance(new BigDecimal("100.00"))
                .balance(new BigDecimal("100.00"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        sampleToAccount = Account.builder()
                .accountId(UUID.randomUUID())
                .accountLogo(AccountLogo.NONE)
                .accountName("toAccountName")
                .accountType(AccountType.BANK_ACCOUNT)
                .initialBalance(new BigDecimal("50.00"))
                .balance(new BigDecimal("50.00"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        sampleTransferId = UUID.randomUUID();
        sampleTransfer = Transfer.builder()
                .transferId(sampleTransferId)
                .amount(new BigDecimal("100.00"))
                .fromAccount(sampleFromAccount)
                .toAccount(sampleToAccount)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should add a transfer")
    void shouldAddTransfer() {
        // given
        BigDecimal fromAccountBalance = sampleFromAccount.getBalance();
        BigDecimal toAccountBalance = sampleToAccount.getBalance();

        NewTransferDTO newTransferDTO = new NewTransferDTO(
                "transferDescription",
                new BigDecimal("10.00"),
                sampleFromAccount.getAccountId(),
                sampleToAccount.getAccountId()
        );

        when(accountRepository.findById(sampleFromAccount.getAccountId())).thenReturn(sampleFromAccount);
        when(accountRepository.findById(sampleToAccount.getAccountId())).thenReturn(sampleToAccount);
        doNothing().when(transferRepository).persistAndFlush(any(Transfer.class));

        // when
        TransferDTO transfer = transferService.addTransfer(newTransferDTO);

        // then
        assertThat(transfer.getTransferDescription()).isEqualTo(newTransferDTO.transferDescription());
        assertThat(transfer.getAmount()).isEqualTo(newTransferDTO.amount());
        assertThat(transfer.getFromAccount().getAccountId()).isEqualTo(newTransferDTO.fromAccount());
        assertThat(transfer.getToAccount().getAccountId()).isEqualTo(newTransferDTO.toAccount());
        assertThat(transfer.getFromAccount().getBalance()).isEqualTo(fromAccountBalance.subtract(newTransferDTO.amount()));
        assertThat(transfer.getToAccount().getBalance()).isEqualTo(toAccountBalance.add(newTransferDTO.amount()));

        verify(accountRepository, times(2)).findById(any(UUID.class));
        verify(transferRepository, times(1)).persistAndFlush(any(Transfer.class));
    }

    @Test
    @DisplayName("Should throw exception on add transfer when sender account does not exist")
    void shouldThrowExceptionOnAddTransferNoSenderAccountExists() {
        // given
        NewTransferDTO newTransferDTO = new NewTransferDTO(
                "transferDescription",
                new BigDecimal("10.00"),
                sampleFromAccount.getAccountId(),
                sampleToAccount.getAccountId()
        );

        when(accountRepository.findById(sampleFromAccount.getAccountId())).thenReturn(null);

        // when / then
        assertThatExceptionOfType(SenderAccountNotFoundException.class).isThrownBy(() -> {
            transferService.addTransfer(newTransferDTO);
        });

        verify(accountRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Should throw exception on add transfer when recipient account does not exist")
    void shouldThrowExceptionOnAddTransferNoRecipientAccountExists() {
        // given
        NewTransferDTO newTransferDTO = new NewTransferDTO(
                "transferDescription",
                new BigDecimal("10.00"),
                sampleFromAccount.getAccountId(),
                sampleToAccount.getAccountId()
        );

        when(accountRepository.findById(sampleFromAccount.getAccountId())).thenReturn(sampleFromAccount);
        when(accountRepository.findById(sampleToAccount.getAccountId())).thenReturn(null);

        // when / then
        assertThatExceptionOfType(RecipientAccountNotFoundException.class).isThrownBy(() -> {
            transferService.addTransfer(newTransferDTO);
        });

        verify(accountRepository, times(2)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Should delete a transfer")
    void shouldDeleteTransfer() {
        // given
        BigDecimal fromAccountBalance = sampleFromAccount.getBalance();
        BigDecimal toAccountBalance = sampleToAccount.getBalance();

        when(transferRepository.findById(any(UUID.class))).thenReturn(sampleTransfer);
        doNothing().when(transferRepository).delete(any(Transfer.class));

        // when / then
        assertThatNoException().isThrownBy(() -> transferService.deleteTransfer(sampleTransferId));
        assertThat(sampleTransfer.getFromAccount().getBalance()).isEqualTo(fromAccountBalance.add(sampleTransfer.getAmount()));
        assertThat(sampleTransfer.getToAccount().getBalance()).isEqualTo(toAccountBalance.subtract(sampleTransfer.getAmount()));

        verify(transferRepository, times(1)).findById(any(UUID.class));
        verify(transferRepository, times(1)).delete(any(Transfer.class));
    }

    @Test
    @DisplayName("Should throw exception on delete a non-existing transfer")
    void shouldThrowExceptionOnDeleteNonExistingTransfer() {
        // given
        when(transferRepository.findById(any(UUID.class))).thenReturn(null);

        // when / then
        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() -> transferService.deleteTransfer(sampleTransferId));

        verify(transferRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Should get a transfer")
    void shouldGetTransfer() {
        // given
        when(transferRepository.findById(any(UUID.class))).thenReturn(sampleTransfer);

        // when
        TransferDTO transfer = transferService.getTransfer(sampleTransferId);

        // then
        assertThat(transfer.getTransferId()).isEqualTo(sampleTransfer.getTransferId());
        assertThat(transfer.getTransferDescription()).isEqualTo(sampleTransfer.getTransferDescription());
        assertThat(transfer.getAmount()).isEqualTo(sampleTransfer.getAmount());
        assertThat(transfer.getFromAccount().getAccountId()).isEqualTo(sampleTransfer.getFromAccount().getAccountId());
        assertThat(transfer.getToAccount().getAccountId()).isEqualTo(sampleTransfer.getToAccount().getAccountId());
        assertThat(transfer.getCreatedAt()).isEqualTo(sampleTransfer.getCreatedAt());
        assertThat(transfer.getUpdatedAt()).isEqualTo(sampleTransfer.getUpdatedAt());

        verify(transferRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Should throw exception on get a non-existing transfer")
    void shouldThrowExceptionOnGetNonExistingTransfer() {
        // given
        when(transferRepository.findById(any(UUID.class))).thenReturn(null);

        // when / then
        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() -> transferService.getTransfer(sampleTransferId));

        verify(transferRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Should get all transfers")
    void shouldGetTransfers() {
        // given
        when(transferRepository.listAll()).thenReturn(List.of(sampleTransfer));

        // when
        List<TransferDTO> transfers = transferService.getTransfers();

        // then
        assertThat(transfers).hasSize(1);
        assertThat(transfers.getFirst().getTransferId()).isEqualTo(sampleTransfer.getTransferId());

        verify(transferRepository, times(1)).listAll();
    }
}