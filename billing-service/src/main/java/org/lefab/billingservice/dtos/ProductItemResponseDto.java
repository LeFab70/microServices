package org.lefab.billingservice.dtos;

import org.lefab.billingservice.model.Product;

import java.math.BigDecimal;

public record ProductItemResponseDto(Long id,
                                     Long productId,
                                     Long quantity,
                                     BigDecimal price,
                                     Product product) {}
