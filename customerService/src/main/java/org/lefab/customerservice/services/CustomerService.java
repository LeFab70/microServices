package org.lefab.customerservice.services;

import lombok.RequiredArgsConstructor;
import org.lefab.customerservice.dtos.CustomerRequestDto;
import org.lefab.customerservice.dtos.CustomerResponseDto;
import org.lefab.customerservice.entities.CustomerEntity;
import org.lefab.customerservice.exceptions.CustomerAlreadyExistsException;
import org.lefab.customerservice.mappers.CustomerMapper;
import org.lefab.customerservice.repositories.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;


    public CustomerResponseDto saveCustomer(CustomerRequestDto customerRequestDto) {
       boolean customerExists = customerRepository.existsByName(customerRequestDto.name());
       if (customerExists) {
           throw new CustomerAlreadyExistsException("Customer already exists");
       }
       CustomerEntity savedCustomer = CustomerEntity.builder().name(customerRequestDto.name()).build();
       return customerMapper.toCustomerResponseDto(savedCustomer);
    }

    //getAllCustomers
    public List<CustomerResponseDto> getAllCustomers() {
        return customerRepository.findAll().stream().map(customerMapper::toCustomerResponseDto).toList();
    }
}
