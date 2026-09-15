package com.ecommerce.service;

import com.ecommerce.dto.ProductRequest;
import com.ecommerce.dto.ProductResponse;
import com.ecommerce.entity.Category;
import com.ecommerce.entity.Product;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ImageUploadService imageUploadService;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private Category category;
    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        product = new Product(100L, "Laptop", "High performance laptop", 999.99, 10);
        product.setCategory(category);
        product.setImageUrl("http://example.com/laptop.jpg");

        productRequest = new ProductRequest();
        productRequest.setName("Laptop");
        productRequest.setDescription("High performance laptop");
        productRequest.setPrice(999.99);
        productRequest.setStockQuantity(10);
        productRequest.setCategoryId(1L);
    }

    @Test
    void createProduct_WhenValidRequest_ShouldReturnProductResponse() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        ProductResponse response = productService.createProduct(productRequest);

        // Assert
        assertNotNull(response);
        assertEquals("Laptop", response.getName());
        assertEquals(999.99, response.getPrice());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void getProductById_WhenProductNotFound_ShouldThrowException() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(999L));
    }

    @Test
    void updateProduct_WhenProductExists_ShouldUpdateAndReturn() {
        // Arrange
        when(productRepository.findById(100L)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        productRequest.setName("Updated Laptop");

        // Act
        ProductResponse response = productService.updateProduct(100L, productRequest);

        // Assert
        assertNotNull(response);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void deleteProduct_WhenProductExists_ShouldDeleteSuccessfully() {
        // Arrange
        when(productRepository.existsById(100L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(100L);

        // Act
        productService.deleteProduct(100L);

        // Assert
        verify(productRepository, times(1)).deleteById(100L);
    }

    @Test
    void searchProducts_ShouldReturnPagedProducts() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        Page<Product> page = new PageImpl<>(List.of(product));
        when(productRepository.findByNameContainingIgnoreCase("Laptop", pageable)).thenReturn(page);

        // Act
        Page<ProductResponse> result = productService.searchProducts("Laptop", 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Laptop", result.getContent().get(0).getName());
    }
}
