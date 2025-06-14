package com.ebudget.category.resource.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record CategoryDTO(
        UUID categoryId,
        String categoryName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
