package com.ebudget.category.resource.request;

import jakarta.validation.constraints.NotBlank;

public record NewCategoryDTO(
        @NotBlank
        String categoryName
) {
}
