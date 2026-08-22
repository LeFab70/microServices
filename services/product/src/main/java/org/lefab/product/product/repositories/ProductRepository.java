package org.lefab.product.product.repositories;

import jakarta.validation.constraints.NotNull;
import org.lefab.product.product.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    List<ProductEntity> findAllByIdInOrderById(List<@NotNull(message = "Product id is required") Long> productIds);

    // decrement atomique : la vérification et l'écriture se font en une seule requête SQL,
    // donc aucune fenêtre de course possible entre deux achats concurrents sur le même produit.
    @Modifying
    @Query("UPDATE ProductEntity p SET p.availableQuantity = p.availableQuantity - :quantity " +
            "WHERE p.id = :id AND p.availableQuantity >= :quantity")
    int decrementStock(@Param("id") Long id, @Param("quantity") Double quantity);
}
