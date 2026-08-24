package org.lefab.notification.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.lefab.notification.enums.NotificationType;
import org.lefab.notification.kafka.OrderConfirmation;
import org.lefab.notification.kafka.PaymentConfirmation;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "notifications")
public class Notification {
    @Id

    private String id;
    private LocalDateTime notificationTime;
    private NotificationType notificationType;
    private OrderConfirmation orderConfirmation;
    private PaymentConfirmation paymentConfirmation;
}
