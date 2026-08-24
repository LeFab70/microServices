package org.lefab.payment.dtos;

import org.lefab.payment.enums.PaymentMethod;
import org.lefab.payment.enums.PaymentStatus;

import java.math.BigDecimal;
import java.util.List;


public record PaymentResponseDto(
        Long paymentId,
        Long orderId,
        String orderReference,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        PaymentStatus status,
        CustomerSummaryDto customer,
        List<PaymentItemResponseDto> items
) {
}
