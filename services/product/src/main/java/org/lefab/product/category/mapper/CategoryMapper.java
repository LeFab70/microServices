package org.lefab.product.category.mapper;

import org.lefab.product.category.dtos.CategoryRequestDto;
import org.lefab.product.category.dtos.CategoryResponseDto;
import org.lefab.product.category.entities.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    CategoryEntity toCategoryEntity(CategoryRequestDto request);
    CategoryResponseDto toCategoryResponse(CategoryEntity category);
}
