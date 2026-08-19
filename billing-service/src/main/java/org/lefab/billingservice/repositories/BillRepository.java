package org.lefab.billingservice.repositories;

import org.lefab.billingservice.entities.BillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "bills", path = "bills")
public interface BillRepository extends JpaRepository<BillEntity, Long> {

}
