package org.lefab.order.order.dtos;

import org.lefab.order.order.enums.OrderStatus;
import org.lefab.order.orderItem.dtos.OrderLineResponseDto;


import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDto(
        Long id,
        LocalDateTime orderDate,
        LocalDateTime lastUpdate,
        String reference,
        OrderStatus status,
        CustomerSummaryDto customer,
        List<OrderLineResponseDto> orderLine
) {
}
