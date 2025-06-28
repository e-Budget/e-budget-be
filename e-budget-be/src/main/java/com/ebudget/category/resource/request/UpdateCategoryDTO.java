package com.ebudget.category.resource.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateCategoryDTO(
        @NotBlank
        String categoryName
) {
}
