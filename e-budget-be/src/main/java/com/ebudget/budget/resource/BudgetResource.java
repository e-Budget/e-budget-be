package com.ebudget.budget.resource;

import com.ebudget.budget.resource.request.NewBudgetDTO;
import com.ebudget.budget.resource.request.UpdateBudgetDTO;
import com.ebudget.budget.resource.response.BudgetDTO;
import com.ebudget.budget.service.interfaces.IBudgetService;
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
@Path("/budget")
@RequiredArgsConstructor
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BudgetResource {
    private final IBudgetService budgetService;

    @POST
    public RestResponse<BudgetDTO> addBudget(@Valid NewBudgetDTO newBudgetDTO) {
        BudgetDTO budget = budgetService.addBudget(newBudgetDTO);

        return RestResponse.status(RestResponse.Status.CREATED, budget);
    }

    @PUT
    @Path("{budgetId}")
    public RestResponse<Void> updateBudget(@PathParam("budgetId") UUID budgetId, @Valid UpdateBudgetDTO updateBudgetDTO) {
        budgetService.updateBudget(budgetId, updateBudgetDTO);

        return RestResponse.status(RestResponse.Status.NO_CONTENT);
    }

    @GET
    @Path("{budgetId}")
    public RestResponse<BudgetDTO> getBudget(@PathParam("budgetId") UUID budgetId) {
        BudgetDTO budget = budgetService.getBudget(budgetId);

        return RestResponse.status(RestResponse.Status.OK, budget);
    }

    @GET
    public RestResponse<List<BudgetDTO>> getBudgets() {
        List<BudgetDTO> budgets = budgetService.getBudgets();

        return RestResponse.status(RestResponse.Status.OK, budgets);
    }

    @DELETE
    @Path("{budgetId}")
    public RestResponse<Void> deleteBudget(@PathParam("budgetId") UUID budgetId) {
        budgetService.deleteBudget(budgetId);

        return RestResponse.status(RestResponse.Status.OK);
    }
}
