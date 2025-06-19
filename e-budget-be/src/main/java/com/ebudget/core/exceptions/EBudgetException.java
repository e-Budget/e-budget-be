package com.ebudget.core.exceptions;

import com.ebudget.core.response.ExceptionDTO;
import lombok.Getter;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;
import java.util.Map;

public class EBudgetException extends RuntimeException {
    private final String exception;
    private final String message;
    private final transient Map<String, Object> details;
    @Getter
    private final RestResponse.Status status;

    private static final String KEY = "key";
    private static final String VALUE = "value";

    public EBudgetException(Class<?> exception, String message, Map<String, Object> details, RestResponse.Status status) {
        this.exception = exception.getSimpleName();
        this.message = message;
        this.details = details;
        this.status = status;
    }

    public ExceptionDTO get() {
        return new ExceptionDTO(
                exception,
                message,
                buildDetails(details)
        );
    }

    private List<Map<String, String>> buildDetails(Map<String, Object> details) {
        return details.entrySet().stream()
                .map(detail -> Map.of(
                        KEY, detail.getKey(),
                        VALUE, detail.getValue().toString()
                ))
                .toList();
    }
}
