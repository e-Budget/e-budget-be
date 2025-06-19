package com.ebudget.transfer.exception;

import com.ebudget.core.exceptions.EBudgetException;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.Map;

public class SenderAccountNotFoundException extends EBudgetException {
    private static final String MESSAGE = "Sender account not found";

    public SenderAccountNotFoundException(Map<String, Object> details) {
        super(
                SenderAccountNotFoundException.class,
                MESSAGE,
                details,
                RestResponse.Status.NOT_FOUND
        );
    }
}
