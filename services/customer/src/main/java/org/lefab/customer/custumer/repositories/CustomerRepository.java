package org.lefab.customer.custumer.repositories;

import org.lefab.customer.custumer.entities.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomerRepository extends MongoRepository <Customer, String>{
}
