package org.lefab.inventoryservice.mappers;


import org.lefab.inventoryservice.dtos.ProductResponseDto;
import org.lefab.inventoryservice.entities.ProductEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductEntity toProductEntity(ProductResponseDto productResponseDto);

    ProductResponseDto toProductResponseDto(ProductEntity productEntity);
}