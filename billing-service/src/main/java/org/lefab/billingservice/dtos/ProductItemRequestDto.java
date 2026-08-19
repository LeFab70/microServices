package org.lefab.billingservice.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductItemRequestDto(@NotNull(message = "Product ID is required")
                                     Long productId,

                                    @NotNull(message = "Quantity is required")
                                     @Positive(message = "Quantity must be greater than 0")
                                     Long quantity,

                                    @NotNull(message = "Price is required")
                                     @Positive(message = "Price must be greater than 0")
                                    BigDecimal price) {}
