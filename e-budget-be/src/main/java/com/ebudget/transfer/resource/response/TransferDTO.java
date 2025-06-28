package com.ebudget.transfer.resource.response;

import com.ebudget.account.resource.response.AccountDTO;
import com.ebudget.transfer.model.Transfer;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(force = true)
public class TransferDTO {
    private final UUID transferId;
    private final String transferDescription;
    private final BigDecimal amount;
    private final AccountDTO fromAccount;
    private final AccountDTO toAccount;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public TransferDTO(Transfer transfer) {
        this.transferId = transfer.getTransferId();
        this.transferDescription = transfer.getTransferDescription();
        this.amount = transfer.getAmount();
        this.fromAccount = new AccountDTO(transfer.getFromAccount());
        this.toAccount = new AccountDTO(transfer.getToAccount());
        this.createdAt = transfer.getCreatedAt();
        this.updatedAt = transfer.getUpdatedAt();
    }
}
