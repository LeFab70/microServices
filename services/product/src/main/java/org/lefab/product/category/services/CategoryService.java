package org.lefab.product.category.services;

import lombok.RequiredArgsConstructor;
import org.lefab.product.category.dtos.CategoryRequestDto;
import org.lefab.product.category.dtos.CategoryResponseDto;
import org.lefab.product.category.entities.CategoryEntity;
import org.lefab.product.category.mapper.CategoryMapper;
import org.lefab.product.category.repositories.CategoryRepository;
import org.lefab.product.exceptions.category.CategoryAlreadyExistsException;
import org.lefab.product.exceptions.category.CategoryNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    public Page<CategoryResponseDto> getAllCategories(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return categoryRepository.findAllByActiveTrue(pageable).map(categoryMapper::toCategoryResponse);
    }

    @Transactional(readOnly = true)
    public CategoryResponseDto getCategoryById(Long id) {
        return categoryMapper.toCategoryResponse(findActiveOrThrow(id));
    }

    public CategoryResponseDto createCategory(CategoryRequestDto request) {
        if (categoryRepository.existsByName(request.name())) {
            throw new CategoryAlreadyExistsException("Category already exists with name: " + request.name());
        }
        CategoryEntity category = categoryMapper.toCategoryEntity(request);
        return categoryMapper.toCategoryResponse(categoryRepository.save(category));
    }

    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto request) {
        CategoryEntity category = findActiveOrThrow(id);
        if (categoryRepository.existsByNameAndIdNot(request.name(), id)) {
            throw new CategoryAlreadyExistsException("Category already exists with name: " + request.name());
        }
        category.setName(request.name());
        category.setDescription(request.description());
        return categoryMapper.toCategoryResponse(categoryRepository.save(category));
    }

    public void softDeleteCategory(Long id) {
        CategoryEntity category = findActiveOrThrow(id);
        category.setActive(false);
        categoryRepository.save(category);
    }

    private CategoryEntity findActiveOrThrow(Long id) {
        return categoryRepository.findByIdAndActiveTrue(id).orElseThrow(
                () -> new CategoryNotFoundException("Category not found with id: " + id)
        );
    }
}
