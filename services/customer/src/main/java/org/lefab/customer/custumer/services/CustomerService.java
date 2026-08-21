package org.lefab.customer.custumer.services;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.lefab.customer.custumer.dtos.CustomerRequestDto;
import org.lefab.customer.custumer.dtos.CustomerResponseDto;
import org.lefab.customer.custumer.repositories.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public  CustomerResponseDto createCustomer(CustomerRequestDto customerRequestDto) {

        return null;
    }
}
