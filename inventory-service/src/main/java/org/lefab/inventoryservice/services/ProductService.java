package org.lefab.inventoryservice.services;

import lombok.RequiredArgsConstructor;
import org.lefab.inventoryservice.dtos.ProductRequestDto;
import org.lefab.inventoryservice.dtos.ProductResponseDto;
import org.lefab.inventoryservice.entities.ProductEntity;
import org.lefab.inventoryservice.exceptions.ProductAlreadyExistsException;
import org.lefab.inventoryservice.mappers.ProductMapper;
import org.lefab.inventoryservice.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
private final ProductRepository productRepository;
private final ProductMapper productMapper;
    //save product
    public ProductResponseDto saveProduct(ProductRequestDto productRequestDto) {
        boolean productExists=productRepository.existsByName(productRequestDto.name());
        if(productExists){
            throw new ProductAlreadyExistsException("Product already exists");
        }
        return productMapper.toProductResponseDto(productRepository.save(ProductEntity.builder().name(productRequestDto.name()).description(productRequestDto.description()).price(productRequestDto.price()).quantity(productRequestDto.quantity()).build()));

    }

    //getAllProducts
    public List<ProductResponseDto> getAllProducts(){
        return productRepository.findAll().stream().map(productMapper::toProductResponseDto).toList();
    }
}
