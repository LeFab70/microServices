package org.lefab.payment.kafka;

import java.math.BigDecimal;
import lombok.Builder;
import org.lefab.payment.dtos.CustomerSummaryDto;
import org.lefab.payment.dtos.ProductSummaryDto;
import org.lefab.payment.enums.PaymentMethod;
import org.lefab.payment.enums.PaymentStatus;


@Builder
public record PaymentConfirmation(
        Long paymentId,
        Long orderId,
        String orderReference,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        CustomerSummaryDto customer,
        ProductSummaryDto products
) {}
