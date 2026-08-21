package org.lefab.customer.custumer.services;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.jspecify.annotations.Nullable;
import org.lefab.customer.custumer.dtos.CustomerRequestDto;
import org.lefab.customer.custumer.dtos.CustomerResponseDto;
import org.lefab.customer.custumer.entities.CustomerEntity;
import org.lefab.customer.custumer.enums.CustomerStatus;
import org.lefab.customer.custumer.mappers.CustomerMapper;
import org.lefab.customer.custumer.repositories.CustomerRepository;
import org.lefab.customer.exceptions.CustomerAlreadyExistsException;
import org.lefab.customer.exceptions.CustomerNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public  CustomerResponseDto createCustomer(CustomerRequestDto customerRequestDto) {
        if(customerRepository.existsByEmail(customerRequestDto.email())){
            throw new CustomerAlreadyExistsException("Customer already exists with email: "+customerRequestDto.email());
        }
        CustomerEntity customer=customerMapper.toCustomerEntity(customerRequestDto);
        return customerMapper.toCustomerResponse(customerRepository.save(customer));
    }

    public  CustomerResponseDto updateCustomer(CustomerRequestDto customerRequestDto,String id) {

        CustomerEntity existCustomer=customerRepository.findById(id).orElseThrow(
                ()->new CustomerNotFoundException("Customer not found with id: "+id)
        );

         customerRepository.findByEmail(customerRequestDto.email())
                 .ifPresent(customer -> {
                    if(!customer.getId().equals(id)){
                        throw new CustomerAlreadyExistsException("Customer already exists with email: "+customerRequestDto.email());
                    }
                 });
         CustomerEntity customer=customerMapper.toCustomerEntity(customerRequestDto);
         customer.setId(id);
         return customerMapper.toCustomerResponse(customerRepository.save(customer));
    }

    public  List<CustomerResponseDto> getAllCustomer() {
        return customerRepository.findAll()
                .stream()
                .map(customerMapper::toCustomerResponse)
                .toList();
    }
    public  CustomerResponseDto getCustomerById(String id) {
        return customerMapper.toCustomerResponse(customerRepository.findById(id).orElseThrow(
                ()->new CustomerNotFoundException("Customer not found with id: "+id)
        ));
    }
    public  void deleteCustomer(String id) {
        CustomerEntity customer=customerRepository.findById(id).orElseThrow(
                ()->new CustomerNotFoundException("Customer not found with id: "+id)
        );
        //customerRepository.deleteById(id);
        customer.setStatus(CustomerStatus.INACTIVE);
        customerRepository.save(customer);
    }
}
