package com.ebudget.core.exceptions;

import org.jboss.resteasy.reactive.RestResponse;

import java.util.Map;
import java.util.UUID;

public class EntityNotFoundException extends EBudgetException {
    private static final String PROPERTY_NAME = "entityId";

    public EntityNotFoundException(Class<?> entity, UUID entityId) {
        super(
                EntityNotFoundException.class,
                entity.getSimpleName() + " not found",
                Map.of(PROPERTY_NAME, entityId),
                RestResponse.Status.NOT_FOUND
        );
    }
}
