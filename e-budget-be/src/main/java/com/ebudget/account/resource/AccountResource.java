package com.ebudget.account.resource;

import com.ebudget.account.resource.request.NewAccountDTO;
import com.ebudget.account.resource.request.UpdateAccountDTO;
import com.ebudget.account.resource.response.AccountDTO;
import com.ebudget.account.service.interfaces.IAccountService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
@Path("/account")
@RequiredArgsConstructor
public class AccountResource {
    private final IAccountService accountService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse<AccountDTO> addAccount(@Valid NewAccountDTO newAccountDTO) {
        AccountDTO account = accountService.addAccount(newAccountDTO);

        return RestResponse.status(RestResponse.Status.CREATED, account);
    }

    @PUT
    @Path("/{accountId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse<Void> updateAccount(@PathParam("accountId") UUID accountId, @Valid UpdateAccountDTO updateAccountDTO) {
        accountService.updateAccount(accountId, updateAccountDTO);

        return RestResponse.status(RestResponse.Status.NO_CONTENT);
    }

    @GET
    @Path("/{accountId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse<AccountDTO> getAccount(@PathParam("accountId") UUID accountId) {
        AccountDTO account = accountService.getAccount(accountId);

        return RestResponse.status(RestResponse.Status.OK, account);
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse<List<AccountDTO>> getAccounts() {
        List<AccountDTO> accounts = accountService.getAccounts();

        return RestResponse.status(RestResponse.Status.OK, accounts);
    }

    @DELETE
    @Path("/{accountId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse<Void> deleteAccount(@PathParam("accountId") UUID accountId) {
        accountService.deleteAccount(accountId);

        return RestResponse.status(RestResponse.Status.OK);
    }
}
