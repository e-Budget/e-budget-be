package com.ebudget.income.resource;

import com.ebudget.income.resource.request.NewIncomeDTO;
import com.ebudget.income.resource.response.IncomeDTO;
import com.ebudget.income.service.interfaces.IIncomeService;
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
@Path("/income")
@RequiredArgsConstructor
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class IncomeResource {
    private final IIncomeService incomeService;

    @POST
    public RestResponse<IncomeDTO> addIncome(@Valid NewIncomeDTO newIncomeDTO) {
        IncomeDTO income = incomeService.addIncome(newIncomeDTO);

        return RestResponse.status(RestResponse.Status.CREATED, income);
    }

    @PUT
    @Path("{incomeId}")
    public RestResponse<Void> updateIncome(@PathParam("incomeId") UUID incomeId, @Valid NewIncomeDTO updateIncomeDTO) {
        incomeService.updateIncome(incomeId, updateIncomeDTO);

        return RestResponse.status(RestResponse.Status.NO_CONTENT);
    }

    @GET
    @Path("{incomeId}")
    public RestResponse<IncomeDTO> getIncome(@PathParam("incomeId") UUID incomeId) {
        IncomeDTO income = incomeService.getIncome(incomeId);

        return RestResponse.status(RestResponse.Status.OK, income);
    }

    @GET
    public RestResponse<List<IncomeDTO>> getIncomes() {
        List<IncomeDTO> incomes = incomeService.getIncomes();

        return RestResponse.status(RestResponse.Status.OK, incomes);
    }

    @DELETE
    @Path("{incomeId}")
    public RestResponse<Void> deleteIncome(@PathParam("incomeId") UUID incomeId) {
        incomeService.deleteIncome(incomeId);

        return RestResponse.status(RestResponse.Status.OK);
    }
}
