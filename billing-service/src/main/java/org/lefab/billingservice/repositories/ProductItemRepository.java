package org.lefab.billingservice.repositories;

import org.lefab.billingservice.entities.ProductItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "productItems", path = "productItems")
public interface ProductItemRepository extends JpaRepository<ProductItemEntity, Long> {}
