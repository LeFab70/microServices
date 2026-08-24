package org.lefab.payment.services;


import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.lefab.payment.dtos.CustomerSummaryDto;
import org.lefab.payment.dtos.OrderConfirmation;
import org.lefab.payment.dtos.PaymentResponseDto;
import org.lefab.payment.dtos.ProductSummaryDto;
import org.lefab.payment.entities.PaymentEntity;
import org.lefab.payment.entities.PaymentItemEntity;
import org.lefab.payment.exceptions.PaymentNotFoundException;
import org.lefab.payment.kafka.PaymentConfirmation;
import org.lefab.payment.kafka.PaymentProducer;
import org.lefab.payment.mapper.PaymentMapper;
import org.lefab.payment.repositories.PaymentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentServices {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentProducer paymentProducer;

    //get all payment (paginé)
    @Transactional(readOnly = true)
    public Page<PaymentResponseDto> getAllPayment(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        return paymentRepository.findAll(pageable).map(this::toResponseWithCustomer);
    }

    @Transactional
    public PaymentResponseDto createPayment(
            OrderConfirmation orderConfirmation
    ) {
        CustomerSummaryDto customer = orderConfirmation.customerSummaryDto();

        PaymentEntity payment =
                PaymentEntity.builder()
                        .orderId(orderConfirmation.orderId())
                        .orderReference(
                                orderConfirmation.orderReference()
                        )
                        .amount(orderConfirmation.totalAmount())
                        .paymentMethod(
                                orderConfirmation.paymentMethod()
                        )
                        .customerId(customer.id())
                        .customerFirstName(customer.firstName())
                        .customerLastName(customer.lastName())
                        .build();

        payment.setItems(orderConfirmation.productPurchases().stream()
                .map(p -> PaymentItemEntity.builder()
                        .productId(p.productId())
                        .productName(p.name())
                        .quantity(p.quantityPurchased() == null ? null : p.quantityPurchased().doubleValue())
                        .unitPrice(p.price())
                        .payment(payment)
                        .build())
                .toList());

        PaymentEntity saved =
                paymentRepository.save(payment);

        PaymentConfirmation paymentConfirmation =
                PaymentConfirmation.builder()
                        .paymentId(saved.getId())
                        .orderId(saved.getOrderId())
                        .orderReference(saved.getOrderReference())
                        .amount(saved.getAmount())
                        .paymentMethod(saved.getPaymentMethod())
                        .paymentStatus(saved.getPaymentStatus())
                        .customer(customer)
                        .products(
                                orderConfirmation.productPurchases()
                        )
                        .build();

        paymentProducer.sendPaymentConfirmation(
                paymentConfirmation
        );

        return toResponseWithCustomer(saved);
    }

    @Transactional(readOnly = true)
    public @Nullable PaymentResponseDto getPaymentById(@NotNull Long id) {
        PaymentEntity payment = paymentRepository.findById(id).orElseThrow(
                () -> new PaymentNotFoundException("Payment not found with id: " + id)
        );
        return toResponseWithCustomer(payment);
    }

    private PaymentResponseDto toResponseWithCustomer(PaymentEntity payment) {
        PaymentResponseDto mapped = paymentMapper.toResponse(payment);
        CustomerSummaryDto customer = new CustomerSummaryDto(
                payment.getCustomerId(),
                payment.getCustomerFirstName(),
                payment.getCustomerLastName(),
                null,
                null
        );
        return new PaymentResponseDto(
                mapped.paymentId(),
                mapped.orderId(),
                mapped.orderReference(),
                mapped.amount(),
                mapped.paymentMethod(),
                mapped.status(),
                customer,
                mapped.items()
        );
    }
}
