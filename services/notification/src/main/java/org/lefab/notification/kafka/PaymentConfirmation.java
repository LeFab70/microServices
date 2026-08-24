package org.lefab.notification.kafka;

import org.lefab.notification.dtos.CustomerSummaryDto;
import org.lefab.notification.dtos.ProductSummaryDto;
import org.lefab.notification.enums.PaymentMethod;
import org.lefab.notification.enums.PaymentStatus;

import java.math.BigDecimal;
import java.util.List;

public record PaymentConfirmation(

        Long paymentId,

        Long orderId,

        String orderReference,

        BigDecimal amount,

        PaymentMethod paymentMethod,

        PaymentStatus paymentStatus,

        CustomerSummaryDto customer,

        List<ProductSummaryDto> products

) {
}