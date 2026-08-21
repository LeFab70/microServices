package org.lefab.product.product.mapper;


import org.lefab.product.category.mapper.CategoryMapper;
import org.lefab.product.product.dtos.ProductRequestDto;
import org.lefab.product.product.dtos.ProductResponseDto;
import org.lefab.product.product.entities.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = CategoryMapper.class)
public interface ProductMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    ProductEntity toProductEntity(ProductRequestDto request);
    ProductResponseDto toProductResponse(ProductEntity product);
}
