package org.lefab.product.product.dtos;

import java.math.BigDecimal;

public record ProductPurchaseResponseDto(
        Long productId,
        String name,
        BigDecimal price,
        Integer quantityPurchased
) {
}
