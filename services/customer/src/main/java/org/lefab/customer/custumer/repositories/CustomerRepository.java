package org.lefab.customer.custumer.repositories;

import org.lefab.customer.custumer.entities.CustomerEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CustomerRepository extends MongoRepository <CustomerEntity, String>{
    boolean existsByEmail(String email);
    Optional<CustomerEntity> findByEmail(String email);
}
