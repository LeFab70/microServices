package org.lefab.inventoryservice.services;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;

import org.lefab.inventoryservice.entities.ProductEntity;
import org.lefab.inventoryservice.repositories.ProductRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductSeederService {

    private final ProductRepository productRepository;
    public void seedCustomers(int numberOfCustomers) {
      Faker faker = new Faker();
        Set<String> names=new HashSet<String>();
        while(names.size()<numberOfCustomers){
            names.add(faker.name().name());
        }
        names.forEach(name->
                {
                    ProductEntity productEntity=ProductEntity.builder().name(name)
                            .description(faker.lorem().sentence(20))
                            .price(BigDecimal.valueOf(faker.number().randomDouble(1, 2, 100)))
                            .quantity(faker.number().numberBetween(2, 100))
                            .build();

                   productRepository.save(productEntity);
                }

        );

        log.info(numberOfCustomers+" Seeding products");
    }
}
