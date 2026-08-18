package org.lefab.customerservice.repositories;

import org.lefab.customerservice.entities.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "customers", path = "customers")
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
 boolean existsByName(String name);
}
