package org.lefab.payment.dtos;

import java.math.BigDecimal;

public record ProductSummaryDto(
        Long productId,
        String productName,
        Double quantity,
        BigDecimal unitPrice
) {
}
