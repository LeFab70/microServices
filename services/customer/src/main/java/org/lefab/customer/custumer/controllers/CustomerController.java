package org.lefab.customer.custumer.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

import org.lefab.customer.custumer.dtos.CustomerRequestDto;
import org.lefab.customer.custumer.dtos.CustomerResponseDto;
import org.lefab.customer.custumer.services.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping
    public  ResponseEntity<CustomerResponseDto> createCustomer(@Valid @RequestBody CustomerRequestDto customerRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createCustomer(customerRequestDto));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<CustomerResponseDto> updateCustomer(@Valid @RequestBody CustomerRequestDto customerRequestDto,@PathVariable("id")  @NotBlank String id) {
        return ResponseEntity.ok().body(customerService.updateCustomer(customerRequestDto,id));
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponseDto>> getAllCustomers() {
        return  ResponseEntity.ok().body(customerService.getAllCustomer());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<CustomerResponseDto> getCustomerById(@PathVariable("id")  @NotBlank String id) {
        return ResponseEntity.ok().body(customerService.getCustomerById(id));
    }
}
