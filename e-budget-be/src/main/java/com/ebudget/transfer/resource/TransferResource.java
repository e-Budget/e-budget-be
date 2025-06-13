package com.ebudget.transfer.resource;

import com.ebudget.transfer.resource.request.NewTransferDTO;
import com.ebudget.transfer.resource.response.TransferDTO;
import com.ebudget.transfer.service.interfaces.ITransferService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
@Path("/transfer")
@RequiredArgsConstructor
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TransferResource {
    private final ITransferService transferService;

    @POST
    public RestResponse<TransferDTO> makeTransfer(@Valid NewTransferDTO newTransferDTO) {
        TransferDTO transfer = transferService.makeTransfer(newTransferDTO);

        return RestResponse.status(RestResponse.Status.OK, transfer);
    }

    @DELETE
    @Path("/{transferId}")
    public RestResponse<Void> cancelTransfer(@PathParam("transferId") UUID transferId) {
        transferService.cancelTransfer(transferId);

        return RestResponse.status(RestResponse.Status.OK);
    }

    @GET
    @Path("{transferId}")
    public RestResponse<TransferDTO> getTransfer(@PathParam("transferId") UUID transferId) {
        TransferDTO transfer = transferService.getTransfer(transferId);

        return RestResponse.status(RestResponse.Status.OK, transfer);
    }

    @GET
    public RestResponse<List<TransferDTO>> getTransfers() {
        List<TransferDTO> transfers = transferService.getTransfers();

        return RestResponse.status(RestResponse.Status.OK, transfers);
    }
}
