package org.lefab.product.product.entities;

import jakarta.persistence.*;
import lombok.*;
import org.lefab.product.category.entities.CategoryEntity;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
@Builder
@Entity
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq_generator")

    @SequenceGenerator(name = "product_seq_generator", sequenceName = "product_seq", allocationSize = 50)
    private Long id;
    @Column(unique = true, nullable = false)
    private String name;
    private String description;
    @Column(nullable = false)
    private BigDecimal price;
    @Column(nullable = false)
    private  Double availableQuantity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;
}
