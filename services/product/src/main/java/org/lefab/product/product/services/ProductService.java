package org.lefab.product.product.services;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.lefab.product.category.entities.CategoryEntity;
import org.lefab.product.category.repositories.CategoryRepository;
import org.lefab.product.exceptions.ProductNotFoundException;
import org.lefab.product.exceptions.category.CategoryNotFoundException;
import org.lefab.product.product.dtos.ProductPurchaseResponseDto;
import org.lefab.product.product.dtos.ProductRequestDto;
import org.lefab.product.product.dtos.ProductRequestPurchaseDto;
import org.lefab.product.product.dtos.ProductResponseDto;
import org.lefab.product.product.entities.ProductEntity;
import org.lefab.product.product.mapper.ProductMapper;
import org.lefab.product.product.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;

    //get all products
    public List<ProductResponseDto> getAllProducts(){
        return productRepository.findAll().stream().map(productMapper::toProductResponse).toList();
    }
    //get product by id
    public ProductResponseDto getProductById(Long id){
        return productMapper.toProductResponse(productRepository.findById(id).orElseThrow(
                ()-> new ProductNotFoundException("Product not found with id: "+id)
        ));
    }

    //Create product
    public ProductResponseDto createProduct(ProductRequestDto productRequestDto){

        CategoryEntity category=categoryRepository.findById(productRequestDto.categoryId()).orElseThrow(
                ()-> new CategoryNotFoundException("Category not found with id: "+productRequestDto.categoryId())
        );
        ProductEntity product=productMapper.toProductEntity(productRequestDto);
        product.setCategory(category);
        return productMapper.toProductResponse(productRepository.save(product));

    }

    public  ProductPurchaseResponseDto createProductPurchase( List<ProductRequestPurchaseDto> requestPurchaseDto) {
        return null;
    }
}
