package org.lefab.customer.custumer.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Customer", description = "Gestion des clients")
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping
    @Operation(summary = "Créer un client")
    public  ResponseEntity<CustomerResponseDto> createCustomer(@Valid @RequestBody CustomerRequestDto customerRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createCustomer(customerRequestDto));
    }

    @PutMapping(value = "/{id}")
    @Operation(summary = "Mettre à jour un client")
    public ResponseEntity<CustomerResponseDto> updateCustomer(@Valid @RequestBody CustomerRequestDto customerRequestDto,@PathVariable("id")  @NotBlank String id) {
        return ResponseEntity.ok().body(customerService.updateCustomer(customerRequestDto,id));
    }

    @GetMapping
    @Operation(summary = "Lister tous les clients")
    public ResponseEntity<List<CustomerResponseDto>> getAllCustomers() {
        return  ResponseEntity.ok().body(customerService.getAllCustomer());
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "Récupérer un client par son id")
    public ResponseEntity<CustomerResponseDto> getCustomerById(@PathVariable("id")  @NotBlank String id) {
        return ResponseEntity.ok().body(customerService.getCustomerById(id));
    }


    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Suppression logique d'un client (status = INACTIVE)")
    public ResponseEntity<Void> deleteCustomer(@PathVariable("id")  @NotBlank String id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
