package org.lefab.order.orderItem.entities;

import jakarta.persistence.*;
import lombok.*;
import org.lefab.order.order.entities.OrderEntity;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "order_line")
public class OrderLineEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_line_seq_generator")
    @SequenceGenerator(name = "order_line_seq_generator", sequenceName = "order_line_seq", allocationSize = 50)
    private Long id;
    @Column(name = "product_id", nullable = false)
    private Long productId;
    @Column( nullable = false)
    private Double quantity;
    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;
    @Column(name = "product_name", nullable = false)
    private String productName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;
}
