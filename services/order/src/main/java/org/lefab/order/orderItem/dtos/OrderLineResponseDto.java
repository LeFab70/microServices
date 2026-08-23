package org.lefab.order.orderItem.dtos;

import java.math.BigDecimal;

public record OrderLineResponseDto(
        Long id,
        Long productId,
        String productName,
        Double quantity,
        BigDecimal price
) {
}
