package com.ebudget.transfer.service.interfaces;

import com.ebudget.transfer.resource.request.NewTransferDTO;
import com.ebudget.transfer.resource.response.TransferDTO;

import java.util.List;
import java.util.UUID;

public interface ITransferService {
    TransferDTO addTransfer(NewTransferDTO newTransferDTO);
    void deleteTransfer(UUID transferId);
    TransferDTO getTransfer(UUID transferId);
    List<TransferDTO> getTransfers();
}
