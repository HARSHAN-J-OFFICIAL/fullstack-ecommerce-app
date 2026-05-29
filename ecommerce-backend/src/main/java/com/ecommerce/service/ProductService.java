package com.ecommerce.service;

import com.ecommerce.dto.ProductRequest;
import com.ecommerce.dto.ProductResponse;

import com.ecommerce.entity.Category;
import com.ecommerce.entity.Product;

import com.ecommerce.exception.ResourceNotFoundException;

import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    // CREATE PRODUCT
    public ProductResponse createProduct(
            ProductRequest request
    ) {

        Category category =
                categoryRepository
                        .findById(
                                request.getCategoryId()
                        )
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Category not found"
                                )
                        );

        Product product = new Product();

        product.setCategory(category);

        product.setName(
                request.getName()
        );

        product.setDescription(
                request.getDescription()
        );

        product.setPrice(
                request.getPrice()
        );

        product.setStockQuantity(
                request.getStockQuantity()
        );

        product.setImageUrl(
                request.getImageUrl()
        );

        product.setCategory(
                category
        );

        Product savedProduct =
                productRepository.save(product);

        return mapToResponse(savedProduct);
    }

    // GET ALL PRODUCTS
    public Page<ProductResponse> getAllProducts(

            int page,

            int size,

            String sortBy,

            String keyword
    ) {

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(sortBy)
                );

        Page<Product> products;

        if (keyword != null &&
                !keyword.isBlank()) {

            products =
                    productRepository
                            .findByNameContainingIgnoreCase(
                                    keyword,
                                    pageable
                            );

        } else {

            products =
                    productRepository.findAll(
                            pageable
                    );
        }

        return products.map(
                this::mapToResponse
        );
    }

    // GET PRODUCT BY ID
    public ProductResponse getProductById(
            Long id
    ) {

        Product product =
                productRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product not found"
                                ));

        return mapToResponse(product);
    }

    // UPDATE PRODUCT
    public ProductResponse updateProduct(

            Long id,

            ProductRequest request
    ) {

        Product existingProduct =
                productRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product not found"
                                ));

        Category category =
                categoryRepository
                        .findById(
                                request.getCategoryId()
                        )
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Category not found"
                                )
                        );

        existingProduct.setName(
                request.getName()
        );

        existingProduct.setDescription(
                request.getDescription()
        );

        existingProduct.setPrice(
                request.getPrice()
        );

        existingProduct.setStockQuantity(
                request.getStockQuantity()
        );

        existingProduct.setImageUrl(
                request.getImageUrl()
        );

        existingProduct.setCategory(
                category
        );

        Product updatedProduct =
                productRepository.save(
                        existingProduct
                );

        return mapToResponse(
                updatedProduct
        );
    }

    // DELETE PRODUCT
    public String deleteProduct(
            Long id
    ) {

        if (!productRepository.existsById(id)) {

            throw new ResourceNotFoundException(
                    "Product not found"
            );
        }

        productRepository.deleteById(id);

        return "Product deleted successfully";
    }

    // SEARCH PRODUCTS
    public Page<ProductResponse> searchProducts(

            String keyword,

            int page,

            int size
    ) {

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by("id")
                                .descending()
                );

        Page<Product> products =
                productRepository
                        .findByNameContainingIgnoreCase(
                                keyword,
                                pageable
                        );

        return products.map(
                this::mapToResponse
        );
    }

    // ENTITY -> DTO
    private ProductResponse mapToResponse(
            Product product
    ) {

        return ProductResponse.builder()

                .id(
                        product.getId()
                )

                .name(
                        product.getName()
                )

                .description(
                        product.getDescription()
                )

                .price(
                        product.getPrice()
                )

                .stockQuantity(
                        product.getStockQuantity()
                )

                .imageUrl(
                        product.getImageUrl()
                )

                .categoryId(
                        product.getCategory() != null
                                ? product.getCategory().getId()
                                : null
                )

                .categoryName(
                        product.getCategory() != null
                                ? product.getCategory().getName()
                                : null
                )

                .build();
    }

    public Page<ProductResponse> getProductsByCategory(

            Long categoryId,

            int page,

            int size
    ) {

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by("id")
                                .descending()
                );

        Page<Product> products =
                productRepository
                        .findByCategoryId(
                                categoryId,
                                pageable
                        );

        return products.map(
                this::mapToResponse
        );
    }
}