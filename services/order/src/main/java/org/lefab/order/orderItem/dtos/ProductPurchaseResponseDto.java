package org.lefab.order.orderItem.dtos;

import java.math.BigDecimal;

public record ProductPurchaseResponseDto(
        Long productId,
        String name,
        BigDecimal price,
        Integer quantityPurchased
) {
}
