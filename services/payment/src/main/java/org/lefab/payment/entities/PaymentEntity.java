package org.lefab.payment.entities;

import jakarta.persistence.*;
import lombok.*;
import org.lefab.payment.enums.PaymentMethod;
import org.lefab.payment.enums.PaymentStatus;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Table(name = "payments")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class PaymentEntity {
    @Id
    @GeneratedValue(
    strategy = GenerationType.SEQUENCE,
    generator = "payment_seq_generator")
    @SequenceGenerator(
    name = "payment_seq_generator",
    sequenceName = "payment_seq",
    allocationSize = 50)
       private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;
    @Column(name = "order_reference", nullable = false)
    private String orderReference;
    private BigDecimal amount;

    @Column(name = "customer_id", nullable = false)
    private String customerId;
    @Column(name = "customer_first_name", nullable = false)
    private String customerFirstName;
    @Column(name = "customer_last_name", nullable = false)
    private String customerLastName;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PaymentItemEntity> items;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus= PaymentStatus.PENDING;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod=PaymentMethod.CREDIT_CARD;
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createAt;
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updateAt;
}
