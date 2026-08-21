package org.lefab.product.product.repositories;

import jakarta.validation.constraints.NotNull;
import org.lefab.product.product.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    List<ProductEntity> findAllByIdInOrderById(List<@NotNull(message = "Product id is required") Long> productIds);
}
