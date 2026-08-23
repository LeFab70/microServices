package org.lefab.order.kafka;

import lombok.Builder;
import org.lefab.order.order.dtos.CustomerSummaryDto;
import org.lefab.order.order.enums.OrderStatus;
import org.lefab.order.order.enums.PaymentMethod;
import org.lefab.order.orderItem.dtos.ProductPurchaseResponseDto;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record OrderConfirmation(
        Long orderId,
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerSummaryDto customerSummaryDto,
        List<ProductPurchaseResponseDto> productPurchases,
        OrderStatus status

) {
}
