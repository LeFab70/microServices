package org.lefab.inventoryservice.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "product")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 100, name = "name")
    private String name;
    private String description;
    @Column(nullable = false, name = "price", precision = 10, scale = 2)
    private BigDecimal price;
    @Column(nullable = false, name = "quantity")
    private Integer quantity;
}
