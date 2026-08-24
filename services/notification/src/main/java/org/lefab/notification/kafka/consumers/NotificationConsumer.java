package org.lefab.notification.kafka.consumers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lefab.notification.entities.Notification;
import org.lefab.notification.enums.NotificationType;
import org.lefab.notification.kafka.OrderConfirmation;
import org.lefab.notification.kafka.PaymentConfirmation;
import org.lefab.notification.repository.NotificationRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final NotificationRepository notificationRepository;

    @KafkaListener(
            topics = "order-topic",
            groupId = "notification-group"
    )
    public void consumeOrderConfirmation(
            OrderConfirmation orderConfirmation
    ) {

        log.info(
                "Received order confirmation {}",
                orderConfirmation.orderReference()
        );

        Notification notification =
                Notification.builder()
                        .notificationTime(LocalDateTime.now())
                        .notificationType(
                                NotificationType.ORDER_CONFIRMATION
                        )
                        .orderConfirmation(orderConfirmation)
                        .build();

        notificationRepository.save(notification);
        //Todo: send email
    }

    @KafkaListener(
            topics = "payment-topic",
            groupId = "notification-group"
    )
    public void consumePaymentConfirmation(
            PaymentConfirmation paymentConfirmation
    ) {

        log.info(
                "Received payment confirmation {}",
                paymentConfirmation.orderReference()
        );

        Notification notification =
                Notification.builder()
                        .notificationTime(LocalDateTime.now())
                        .notificationType(
                                NotificationType.PAYMENT_CONFIRMATION
                        )
                        .paymentConfirmation(paymentConfirmation)
                        .build();

        notificationRepository.save(notification);
        //Todo: send email
    }
}