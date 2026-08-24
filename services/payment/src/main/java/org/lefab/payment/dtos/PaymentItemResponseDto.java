package org.lefab.payment.dtos;

import java.math.BigDecimal;

public record PaymentItemResponseDto(
        Long id,
        Long productId,
        String productName,
        Double quantity,
        BigDecimal price
) {
}
