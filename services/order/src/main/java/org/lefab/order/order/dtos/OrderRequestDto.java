package org.lefab.order.order.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.lefab.order.order.enums.PaymentMethod;
import org.lefab.order.orderItem.dtos.OrderLineRequestDto;

import java.util.List;

@Builder
public record OrderRequestDto(
        @NotNull(message = "Customer id is required")
        String customerId,

        PaymentMethod paymentMethod,

        @NotEmpty(message = "Order must contain at least one line")
        @Valid
        List<OrderLineRequestDto> orderLines
) {
}
