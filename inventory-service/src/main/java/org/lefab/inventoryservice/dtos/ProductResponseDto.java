package org.lefab.inventoryservice.dtos;

import java.math.BigDecimal;

public record ProductResponseDto(
        Long id,

        String name,

        String description,

        BigDecimal price,

        Integer quantity
) {}
