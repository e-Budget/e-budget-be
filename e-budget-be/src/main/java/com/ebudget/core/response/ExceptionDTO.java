package com.ebudget.core.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor(force = true)
public class ExceptionDTO {
    private final String exception;
    private final String message;
    private final List<Map<String, String>> details;

    public ExceptionDTO(String exception, String message, List<Map<String, String>> details) {
        this.exception = exception;
        this.message = message;
        this.details = details;
    }
}