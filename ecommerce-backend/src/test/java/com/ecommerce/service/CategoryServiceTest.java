package com.ecommerce.service;

import com.ecommerce.entity.Category;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category(1L, "Electronics", "Devices and gadgets", "http://example.com/elec.jpg");
    }

    @Test
    void getAllCategories_ShouldReturnCategoryList() {
        // Arrange
        when(categoryRepository.findAll()).thenReturn(List.of(category));

        // Act
        List<Category> categories = categoryService.getAllCategories();

        // Assert
        assertNotNull(categories);
        assertEquals(1, categories.size());
        assertEquals("Electronics", categories.get(0).getName());
    }

    @Test
    void getCategoryById_WhenExists_ShouldReturnCategory() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        // Act
        Category result = categoryService.getCategoryById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Electronics", result.getName());
    }

    @Test
    void createCategory_WhenValid_ShouldSaveAndReturn() {
        // Arrange
        Category newCat = new Category(null, "Gadgets");
        when(categoryRepository.existsByNameIgnoreCase("Gadgets")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(new Category(2L, "Gadgets"));

        // Act
        Category result = categoryService.createCategory(newCat);

        // Assert
        assertNotNull(result);
        assertEquals("Gadgets", result.getName());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void createCategory_WhenDuplicateName_ShouldThrowException() {
        // Arrange
        Category dupCat = new Category(null, "Electronics");
        when(categoryRepository.existsByNameIgnoreCase("Electronics")).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> categoryService.createCategory(dupCat));
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void deleteCategory_WhenHasProducts_ShouldThrowException() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.existsByCategoryId(1L)).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> categoryService.deleteCategory(1L));
        verify(categoryRepository, never()).delete(any(Category.class));
    }

    @Test
    void deleteCategory_WhenNoProducts_ShouldDeleteSuccessfully() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.existsByCategoryId(1L)).thenReturn(false);
        doNothing().when(categoryRepository).delete(category);

        // Act
        categoryService.deleteCategory(1L);

        // Assert
        verify(categoryRepository, times(1)).delete(category);
    }
}
