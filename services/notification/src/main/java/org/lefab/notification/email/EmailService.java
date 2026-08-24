package org.lefab.notification.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender emailSender;
    private final SpringTemplateEngine templateEngine;


    @Async
    public void sendOrderConfirmationEmail(
            String email,
            String customerName,
            String orderReference,
            BigDecimal totalAmount
    ) {
        try {

            Context context = new Context();

            context.setVariable(
                    "customerName",
                    customerName
            );

            context.setVariable(
                    "orderReference",
                    orderReference
            );

            context.setVariable(
                    "totalAmount",
                    totalAmount
            );

            String htmlContent =
                    templateEngine.process(
                            "order-confirmation",
                            context
                    );

            MimeMessage message =
                    emailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true,
                            "UTF-8"
                    );

            helper.setFrom("noreply@lefab.com");

            helper.setTo(email);

            helper.setSubject(
                    "Order Confirmation"
            );

            helper.setText(
                    htmlContent,
                    true
            );

            emailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Async
    public void sendPaymentSuccessEmail(
            String email,
            String orderReference,
            BigDecimal amount,
            String customerName
    ) {

        try {

            Context context = new Context();

            context.setVariable(
                    "customerName",
                    customerName
            );

            context.setVariable(
                    "orderReference",
                    orderReference
            );

            context.setVariable(
                    "amount",
                    amount
            );

            String htmlContent =
                    templateEngine.process(
                            "payment-success",
                            context
                    );

            MimeMessage message =
                    emailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true,
                            "UTF-8"
                    );
            helper.setFrom("noreply@lefab.com");
            helper.setTo(email);

            helper.setSubject(
                    "Payment Confirmation"
            );

            helper.setText(
                    htmlContent,
                    true
            );

            emailSender.send(message);

            log.info(
                    "Payment confirmation email sent to {}",
                    email
            );

        } catch (MessagingException e) {

            log.error(
                    "Failed to send payment email",
                    e
            );
        }
    }
}