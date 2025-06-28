package com.ebudget.expense.resource;

import com.ebudget.expense.resource.request.NewExpenseDTO;
import com.ebudget.expense.resource.request.UpdateExpenseDTO;
import com.ebudget.expense.resource.response.ExpenseDTO;
import com.ebudget.expense.service.interfaces.IExpenseService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
@Path("/expense")
@RequiredArgsConstructor
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ExpenseResource {
    private final IExpenseService expenseService;

    @POST
    public RestResponse<ExpenseDTO> addExpense(@Valid NewExpenseDTO newExpenseDTO) {
        ExpenseDTO expense = expenseService.addExpense(newExpenseDTO);

        return RestResponse.status(RestResponse.Status.CREATED, expense);
    }

    @PUT
    @Path("{expenseId}")
    public RestResponse<Void> updateExpense(UUID expenseId, @Valid UpdateExpenseDTO updateExpenseDTO) {
        expenseService.updateExpense(expenseId, updateExpenseDTO);

        return RestResponse.status(RestResponse.Status.NO_CONTENT);
    }

    @GET
    @Path("{expenseId}")
    public RestResponse<ExpenseDTO> getExpense(UUID expenseId) {
        ExpenseDTO expense = expenseService.getExpense(expenseId);

        return RestResponse.status(RestResponse.Status.OK, expense);
    }

    @GET
    public RestResponse<List<ExpenseDTO>> getExpenses() {
        List<ExpenseDTO> expenses = expenseService.getExpenses();

        return RestResponse.status(RestResponse.Status.OK, expenses);
    }

    @DELETE
    @Path("{expenseId}")
    public RestResponse<Void> deleteExpense(UUID expenseId) {
        expenseService.deleteExpense(expenseId);

        return RestResponse.status(RestResponse.Status.OK);
    }
}
