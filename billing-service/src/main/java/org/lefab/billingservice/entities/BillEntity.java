package org.lefab.billingservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.lefab.billingservice.model.Customer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "bill")

public class BillEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    @Column(nullable = false, name = "customer_id")
    private Long customerId;
    @OneToMany(mappedBy = "bill")
    private List<ProductItemEntity> productsItems=new ArrayList<>();
    @Transient private Customer customer;
}
