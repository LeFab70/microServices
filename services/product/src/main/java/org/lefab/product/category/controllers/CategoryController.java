package org.lefab.product.category.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.lefab.product.category.dtos.CategoryRequestDto;
import org.lefab.product.category.dtos.CategoryResponseDto;
import org.lefab.product.category.services.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/category")
@RequiredArgsConstructor
@Tag(name = "Category", description = "Gestion des catégories de produits")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Lister les catégories actives (paginé)")
    public ResponseEntity<Page<CategoryResponseDto>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok().body(categoryService.getAllCategories(page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une catégorie par son id")
    public ResponseEntity<CategoryResponseDto> getCategoryById(@PathVariable @NotNull Long id) {
        return ResponseEntity.ok().body(categoryService.getCategoryById(id));
    }

    @PostMapping
    @Operation(summary = "Créer une catégorie")
    public ResponseEntity<CategoryResponseDto> createCategory(@RequestBody @Valid CategoryRequestDto categoryRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(categoryRequestDto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une catégorie")
    public ResponseEntity<CategoryResponseDto> updateCategory(
            @PathVariable @NotNull Long id,
            @RequestBody @Valid CategoryRequestDto categoryRequestDto
    ) {
        return ResponseEntity.ok().body(categoryService.updateCategory(id, categoryRequestDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer (soft delete) une catégorie")
    public ResponseEntity<Void> deleteCategory(@PathVariable @NotNull Long id) {
        categoryService.softDeleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
