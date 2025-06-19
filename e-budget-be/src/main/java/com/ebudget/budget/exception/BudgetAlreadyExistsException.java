package com.ebudget.budget.exception;

import com.ebudget.core.exceptions.EBudgetException;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.Map;

public class BudgetAlreadyExistsException extends EBudgetException {
    private static final String MESSAGE = "Budget already exists for provided category, month, and year";

    public BudgetAlreadyExistsException(Map<String, Object> details) {
        super(
                BudgetAlreadyExistsException.class,
                MESSAGE,
                details,
                RestResponse.Status.BAD_REQUEST
        );
    }
}
