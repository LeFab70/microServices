package org.lefab.product.product.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.lefab.product.product.dtos.ProductPurchaseResponseDto;
import org.lefab.product.product.dtos.ProductRequestDto;
import org.lefab.product.product.dtos.ProductRequestPurchaseDto;
import org.lefab.product.product.dtos.ProductResponseDto;
import org.lefab.product.product.services.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("api/v1/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts(){
        return ResponseEntity.ok().body(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable @NotNull Long id){
        return ResponseEntity.ok().body(productService.getProductById(id));
    }
    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(
            @Valid @RequestBody ProductRequestDto productRequestDto
            ){
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(productRequestDto));
    }

    @PostMapping(value = "/purchase")
    public ResponseEntity<List<ProductPurchaseResponseDto>> createProduct(
            @Valid @RequestBody List<ProductRequestPurchaseDto> requestPurchaseDto
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(Collections.singletonList(productService.createProductPurchase(requestPurchaseDto)));
    }
}
