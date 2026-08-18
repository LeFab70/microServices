package org.lefab.inventoryservice.controllers;

import lombok.RequiredArgsConstructor;
import org.lefab.inventoryservice.dtos.ProductResponseDto;
import org.lefab.inventoryservice.services.ProductSeederService;
import org.lefab.inventoryservice.services.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductSeederService productSeederService;
    private final ProductService productService;
    @RequestMapping("/seed")
    public ResponseEntity<String> seedProducts(){
        productSeederService.seedCustomers(10);
        return ResponseEntity.ok("Products seeded");
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts(){
        return ResponseEntity.ok(productService.getAllProducts());
    }

}
