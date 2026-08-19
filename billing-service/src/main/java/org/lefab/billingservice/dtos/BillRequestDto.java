package org.lefab.billingservice.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BillRequestDto(@NotNull(message = "Customer ID is required")
                              Long customerId,

                             @NotEmpty(message = "Products items are required")
                             List<@Valid ProductItemRequestDto> productsItems) {}
