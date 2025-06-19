package com.ebudget.transfer.exception;

import com.ebudget.core.exceptions.EBudgetException;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.Map;

public class RecipientAccountNotFoundException extends EBudgetException {
    private static final String MESSAGE = "Recipient account not found";

    public RecipientAccountNotFoundException(Map<String, Object> details) {
        super(
                RecipientAccountNotFoundException.class,
                MESSAGE,
                details,
                RestResponse.Status.NOT_FOUND
        );
    }
}
