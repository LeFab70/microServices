package org.lefab.payment.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.lefab.payment.enums.PaymentMethod;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record PaymentRequestDto(

        @NotNull(message = "Order id is required")
        Long orderId,

        @NotBlank(message = "Order reference is required")
        String orderReference,

        @NotNull(message = "Amount is required")
        @DecimalMin(
                value = "0.01",
                inclusive = true,
                message = "Amount must be greater than 0"
        )
        BigDecimal amount,

        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod,

       @NotNull(message = "Customer is required")
       CustomerSummaryDto customer,

        List<ProductSummaryDto> products



) {
}