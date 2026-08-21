package org.lefab.product.product.services;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.lefab.product.category.entities.CategoryEntity;
import org.lefab.product.category.repositories.CategoryRepository;
import org.lefab.product.exceptions.InsufficientStockException;
import org.lefab.product.exceptions.ProductNotFoundException;
import org.lefab.product.exceptions.category.CategoryNotFoundException;
import org.lefab.product.product.dtos.ProductPurchaseResponseDto;
import org.lefab.product.product.dtos.ProductRequestDto;
import org.lefab.product.product.dtos.ProductRequestPurchaseDto;
import org.lefab.product.product.dtos.ProductResponseDto;
import org.lefab.product.product.entities.ProductEntity;
import org.lefab.product.product.mapper.ProductMapper;
import org.lefab.product.product.repositories.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;

    //get all products
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAll(pageable).map(productMapper::toProductResponse);
    }

    //get product by id
    @Transactional(readOnly = true)
    public ProductResponseDto getProductById(Long id) {
        return productMapper.toProductResponse(productRepository.findById(id).orElseThrow(
                () -> new ProductNotFoundException("Product not found with id: " + id)
        ));
    }

    //Create product
    public ProductResponseDto createProduct(ProductRequestDto productRequestDto) {

        CategoryEntity category = categoryRepository.findById(productRequestDto.categoryId()).orElseThrow(
                () -> new CategoryNotFoundException("Category not found with id: " + productRequestDto.categoryId())
        );
        ProductEntity product = productMapper.toProductEntity(productRequestDto);
        product.setCategory(category);
        return productMapper.toProductResponse(productRepository.save(product));

    }

    @Transactional
    public List<ProductPurchaseResponseDto> createProductPurchase(List<ProductRequestPurchaseDto> requestPurchaseDto) {

        // agréger les doublons : somme des quantités par productId
        var aggregatedRequests = requestPurchaseDto.stream()
                .collect(Collectors.groupingBy(
                        ProductRequestPurchaseDto::productId,
                        Collectors.summingDouble(ProductRequestPurchaseDto::quantity)
                ))
                .entrySet().stream()
                .map(entry -> new ProductRequestPurchaseDto(entry.getKey(), entry.getValue()))
                .toList();

        var productIds = aggregatedRequests.stream().map(ProductRequestPurchaseDto::productId).toList();

        var storedProducts = productRepository.findAllByIdInOrderById(productIds);
        if (storedProducts.size() != productIds.size()) {
            throw new ProductNotFoundException("Product not found for some ids");
        }
        var productsById = storedProducts.stream()
                .collect(Collectors.toMap(ProductEntity::getId, p -> p));

        // 1. valider le stock pour tout le monde avant de toucher quoi que ce soit
        for (ProductRequestPurchaseDto request : aggregatedRequests) {
            ProductEntity product = productsById.get(request.productId());
            if (product.getAvailableQuantity() < request.quantity()) {
                throw new InsufficientStockException(
                        "Insufficient stock for product '" + product.getName() +
                                "'. Requested: " + request.quantity() +
                                ", Available: " + product.getAvailableQuantity()
                );
            }
        }

        // 2. décrémenter et construire les réponses
        List<ProductPurchaseResponseDto> responses = new ArrayList<>();
        for (ProductRequestPurchaseDto request : aggregatedRequests) {
            ProductEntity product = productsById.get(request.productId());
            product.setAvailableQuantity(product.getAvailableQuantity() - request.quantity());
            responses.add(new ProductPurchaseResponseDto(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    request.quantity().intValue()
            ));
        }
        productRepository.saveAll(storedProducts);

        return responses;
    }
}
