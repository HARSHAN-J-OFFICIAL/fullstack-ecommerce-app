package com.ecommerce.controller;

import com.ecommerce.entity.Category;

import com.ecommerce.service.CategoryService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService
            categoryService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Category createCategory(
            @RequestBody Category category
    ) {

        return categoryService
                .createCategory(category);
    }

    @GetMapping
    public List<Category> getAllCategories() {

        return categoryService
                .getAllCategories();
    }
}