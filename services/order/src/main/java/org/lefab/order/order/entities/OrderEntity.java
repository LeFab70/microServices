package org.lefab.order.order.entities;

import jakarta.persistence.*;
import lombok.*;
import org.lefab.order.order.enums.OrderStatus;
import org.lefab.order.order.enums.PaymentMethod;
import org.lefab.order.orderItem.entities.OrderLineEntity;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Table(name = "orders")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class OrderEntity {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "order_seq_generator"
    )
    @SequenceGenerator(
            name = "order_seq_generator",
            sequenceName = "order_seq",
            allocationSize = 50
    )
       private Long id;
    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(nullable = false,unique = true)
     private String reference;
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus= OrderStatus.PENDING;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod=PaymentMethod.CREDIT_CARD;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderLineEntity> orderLine;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updateAt;
}
