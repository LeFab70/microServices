package org.lefab.billingservice.controllers;

import lombok.RequiredArgsConstructor;
import org.lefab.billingservice.dtos.BillResponseDto;
import org.lefab.billingservice.services.BillingSeederService;
import org.lefab.billingservice.services.BillingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("billing")
@RequiredArgsConstructor
public class BillingController {
    private final BillingService billingService;
    private final BillingSeederService billingSeederService;

    @PostMapping(value = "/seed")
    public ResponseEntity<String> seedBilling(){
        billingSeederService.seedCustomers(10);
        return ResponseEntity.ok("Billing seeded");
    }

    @GetMapping
    public ResponseEntity<List<BillResponseDto>> getAllBilling(){
        return ResponseEntity.ok(billingService.getAllBilling());
    }
}
