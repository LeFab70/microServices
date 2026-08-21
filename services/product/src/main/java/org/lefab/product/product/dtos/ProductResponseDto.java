package org.lefab.product.product.dtos;

import org.lefab.product.category.dtos.CategoryResponseDto;

import java.math.BigDecimal;

public record ProductResponseDto(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Double availableQuantity,
        CategoryResponseDto category
) {
}
