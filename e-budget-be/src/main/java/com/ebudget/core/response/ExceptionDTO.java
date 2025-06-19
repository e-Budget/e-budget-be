package com.ebudget.core.response;

import java.util.List;
import java.util.Map;

public record ExceptionDTO(
        String exception,
        String message,
        List<Map<String, String>> details
) {
}