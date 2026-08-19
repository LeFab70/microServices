package org.lefab.billingservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.datafaker.Faker;
import org.lefab.billingservice.entities.BillEntity;
import org.lefab.billingservice.entities.ProductItemEntity;
import org.lefab.billingservice.feign.CustomerRestClient;
import org.lefab.billingservice.feign.ProductItemRestClient;
import org.lefab.billingservice.model.Customer;
import org.lefab.billingservice.model.Product;
import org.lefab.billingservice.repositories.BillRepository;
import org.lefab.billingservice.repositories.ProductItemRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillingSeederService {

    private final ProductItemRepository productItemRepository;
    private final BillRepository billRepository;
    private final CustomerRestClient customerRestClient;
    private final ProductItemRestClient productItemRestClient;

    public void seedCustomers(int numberOfCustomers) {
      Faker faker = new Faker();
        List<Customer> customers=customerRestClient.findAllCustomers().stream().toList();
        Collection<Product> products=productItemRestClient.findAllProducts().stream().toList();
        //one bill per customer
        customers.forEach(customer -> {
            BillEntity billEntity=BillEntity.builder()
                    .date(LocalDate.from(faker.timeAndDate().birthday()))
                    .customerId(customer.getId())
                    .customer(customer)
                    .build();
            billRepository.save(billEntity);
            products.forEach(product -> {
                ProductItemEntity productItemEntity=ProductItemEntity.builder()
                        .bill(billEntity)
                        .productId(product.getId())
                        .product(product)
                        .price(product.getPrice())
                        .quantity((long) faker.number().numberBetween(2, 100))
                        .build();
                productItemRepository.save(productItemEntity);
            });
        });


        log.info("{} Seeding billing", numberOfCustomers);
    }
}
