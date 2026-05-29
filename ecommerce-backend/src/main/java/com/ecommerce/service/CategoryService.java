package com.ecommerce.service;

import com.ecommerce.entity.Category;

import com.ecommerce.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository
            categoryRepository;

    public Category createCategory(
            Category category
    ) {

        return categoryRepository
                .save(category);
    }

    public List<Category> getAllCategories() {

        return categoryRepository
                .findAll();
    }
}