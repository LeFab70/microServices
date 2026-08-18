package org.lefab.customerservice.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.lefab.customerservice.dtos.CustomerRequestDto;
import org.lefab.customerservice.dtos.CustomerResponseDto;
import org.lefab.customerservice.services.CustomerSeederService;
import org.lefab.customerservice.services.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;
    private final CustomerSeederService customerSeederService;

    @PostMapping(value = "/seed")
    public ResponseEntity<Void> seedCustomers() {
        customerSeederService.seedCustomers(10);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponseDto>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @PostMapping
    public ResponseEntity<CustomerResponseDto> createCustomer(@RequestBody @Valid CustomerRequestDto customerRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.saveCustomer(customerRequestDto));
    }

}
