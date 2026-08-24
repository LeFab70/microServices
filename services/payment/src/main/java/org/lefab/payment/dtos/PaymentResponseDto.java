package org.lefab.payment.dtos;

import org.lefab.payment.enums.PaymentMethod;
import org.lefab.payment.enums.PaymentStatus;

import java.math.BigDecimal;


public record PaymentResponseDto(
        Long paymentId,
        Long orderId,
        String orderReference,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        PaymentStatus status,
        CustomerSummaryDto customer
) {
}
