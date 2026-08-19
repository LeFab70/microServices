package org.lefab.billingservice.mappers;



import org.lefab.billingservice.dtos.ProductItemRequestDto;
import org.lefab.billingservice.dtos.ProductItemResponseDto;

import org.lefab.billingservice.entities.ProductItemEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductItemMapper {

    ProductItemEntity toProductItemMapperEntity(ProductItemRequestDto productItemRequestDto);

    ProductItemResponseDto toProductItemResponseDto(ProductItemEntity productItemEntity);
}