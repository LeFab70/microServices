package org.lefab.product.category.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CategoryRequestDto(
        @NotBlank(message = "category Name is required")
        @Size(min = 3, max = 50, message = "category Name must be between 3 and 50 characters")
        String name,
        @Size(min = 5, max = 100, message = "category Description must be between 5 and 100 characters")
        String description
) {
}
