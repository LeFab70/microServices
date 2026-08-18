package org.lefab.customerservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.lefab.customerservice.entities.CustomerEntity;
import org.lefab.customerservice.repositories.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerSeederService {

    private final CustomerRepository customerRepository;
    public void seedCustomers(int numberOfCustomers) {
      Faker faker = new Faker();
        Set<String> names=new HashSet<String>();
        while(names.size()<numberOfCustomers){
            names.add(faker.name().name());
        }
        names.forEach(name->
                {
                    CustomerEntity customerEntity=CustomerEntity.builder().name(name)
                            .build();
                    customerRepository.save(customerEntity);
                }

        );

        log.info(numberOfCustomers+" Seeding customers");
    }
}
