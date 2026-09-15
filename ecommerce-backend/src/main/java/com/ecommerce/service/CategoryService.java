package com.ecommerce.service;

import com.ecommerce.entity.Category;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    public Category createCategory(Category category) {
        if (category.getName() == null || category.getName().isBlank()) {
            throw new IllegalArgumentException("Category name is required.");
        }
        if (categoryRepository.existsByNameIgnoreCase(category.getName().trim())) {
            throw new IllegalArgumentException("Category with name '" + category.getName().trim() + "' already exists.");
        }
        category.setName(category.getName().trim());
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, Category updatedCategory) {
        Category existingCategory = getCategoryById(id);

        if (updatedCategory.getName() != null && !updatedCategory.getName().isBlank()) {
            String newName = updatedCategory.getName().trim();
            if (!existingCategory.getName().equalsIgnoreCase(newName) && categoryRepository.existsByNameIgnoreCase(newName)) {
                throw new IllegalArgumentException("Category with name '" + newName + "' already exists.");
            }
            existingCategory.setName(newName);
        }

        if (updatedCategory.getDescription() != null) {
            existingCategory.setDescription(updatedCategory.getDescription());
        }

        if (updatedCategory.getImageUrl() != null) {
            existingCategory.setImageUrl(updatedCategory.getImageUrl());
        }

        return categoryRepository.save(existingCategory);
    }

    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);

        if (productRepository.existsByCategoryId(id)) {
            throw new IllegalArgumentException("Cannot delete category '" + category.getName() + "' because it has associated products.");
        }

        categoryRepository.delete(category);
    }
}