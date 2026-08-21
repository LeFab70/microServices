package org.lefab.product.product.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ProductRequestPurchaseDto(
        @NotNull(message = "Product id is required")
        Long productId,
        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be greater than or equal to 1")
        Double quantity
) {
}
