package com.ebudget.account.resource;

import com.ebudget.account.resource.request.NewAccountDTO;
import com.ebudget.account.resource.response.AccountDTO;
import com.ebudget.account.service.AccountService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.reactive.RestResponse;

@ApplicationScoped
@Path("/account")
@RequiredArgsConstructor
public class AccountResource {
    private final AccountService accountService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse<AccountDTO> addAccount(@Valid NewAccountDTO newAccountDTO) {
        AccountDTO account = accountService.addAccount(newAccountDTO);

        return RestResponse.status(RestResponse.Status.CREATED, account);
    }
}
