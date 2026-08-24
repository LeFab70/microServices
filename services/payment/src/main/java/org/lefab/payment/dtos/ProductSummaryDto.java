package org.lefab.payment.dtos;

import java.math.BigDecimal;

public record ProductSummaryDto(
        Long productId,
        String name,
        BigDecimal price,
        Integer quantityPurchased
) {
}
