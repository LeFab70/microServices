package org.lefab.payment.dtos;



import lombok.Builder;
import org.lefab.payment.enums.PaymentMethod;
import java.math.BigDecimal;
import java.util.List;

@Builder
public record OrderConfirmation(
        Long orderId,
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerSummaryDto customerSummaryDto,
        List<ProductSummaryDto> productPurchases
) {
}