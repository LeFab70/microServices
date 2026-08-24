package org.lefab.payment.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.lefab.payment.dtos.OrderConfirmation;
import org.lefab.payment.services.PaymentServices;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentConsumer {

    private final PaymentServices paymentServices;

    @KafkaListener(
            topics = "order-topic",
            groupId = "payment-group"
    )
    public void consumeOrderConfirmation(
            OrderConfirmation orderConfirmation
    ) {

        log.info(
                "Received order {}",
                orderConfirmation.orderReference()
        );

        paymentServices.createPayment(
                orderConfirmation
        );
    }
}