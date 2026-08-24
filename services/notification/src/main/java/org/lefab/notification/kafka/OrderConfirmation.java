package org.lefab.notification.kafka;

import org.lefab.notification.dtos.CustomerSummaryDto;
import org.lefab.notification.dtos.ProductSummaryDto;
import org.lefab.notification.enums.OrderStatus;
import org.lefab.notification.enums.PaymentMethod;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation(

        Long orderId,

        String orderReference,

        BigDecimal totalAmount,

        PaymentMethod paymentMethod,

        CustomerSummaryDto customerSummaryDto,

        List<ProductSummaryDto> productPurchases,

        OrderStatus status

) {
}