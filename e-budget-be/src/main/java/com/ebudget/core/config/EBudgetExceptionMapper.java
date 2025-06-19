package com.ebudget.core.config;

import com.ebudget.core.exceptions.EBudgetException;
import com.ebudget.core.response.ExceptionDTO;
import io.quarkus.hibernate.validator.runtime.jaxrs.ResteasyReactiveViolationException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidationException;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class EBudgetExceptionMapper {
    private static final String VALIDATION_EXCEPTION_MESSAGE = "Request contains validation errors";
    private static final String KEY = "key";
    private static final String VALUE = "value";

    @ServerExceptionMapper
    public RestResponse<ExceptionDTO> mapEBudgetException(EBudgetException eBudgetException) {
        return RestResponse.status(
                eBudgetException.getStatus(),
                eBudgetException.get()
        );
    }

    @ServerExceptionMapper
    public RestResponse<ExceptionDTO> mapValidationException(ValidationException validationException) {
        ResteasyReactiveViolationException resteasyReactiveViolationException = (ResteasyReactiveViolationException) validationException;

        return RestResponse.status(
                RestResponse.Status.BAD_REQUEST,
                new ExceptionDTO(
                        ValidationException.class.getSimpleName(),
                        VALIDATION_EXCEPTION_MESSAGE,
                        buildValidationErrors(resteasyReactiveViolationException.getConstraintViolations())
                )
        );
    }

    private List<Map<String, String>> buildValidationErrors(Set<ConstraintViolation<?>> validationErrors) {
        return validationErrors.stream()
                .map(validation -> Map.of(
                        KEY, validation.getPropertyPath().toString(),
                        VALUE, validation.getMessage()
                ))
                .toList();
    }
}
