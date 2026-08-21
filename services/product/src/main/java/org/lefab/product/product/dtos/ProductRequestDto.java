package org.lefab.product.product.dtos;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductRequestDto(
        @NotBlank(message = "Product name is required")
        @Size(min = 3, max = 50, message = "Product name must be between 3 and 50 characters")
        String name,

        @Size(min = 5, max = 100, message = "Product description must be between 5 and 100 characters")
        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        BigDecimal price,

        @NotNull(message = "Available quantity is required")
        @Min(value = 0, message = "Available quantity must be greater than or equal to 0")
        @Max(value = 1000, message = "Available quantity must be less than or equal to 1000")
        Double availableQuantity,

        @NotNull(message = "Category id is required")
        Long categoryId)
{
}