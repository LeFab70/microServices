package org.lefab.billingservice.feign;

import org.lefab.billingservice.model.Customer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

//for communication for other microservices
@FeignClient(name = "customer-service")
public interface CustomerRestClient {
    @GetMapping("/customers/{id}")
    Customer findCustomerById(@PathVariable("id") Long id);
    @GetMapping("/customers")
    List<Customer> findAllCustomers();
    //PagedModel<Customer> findAllCustomers();
}
