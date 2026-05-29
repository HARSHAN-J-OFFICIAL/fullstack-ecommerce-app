package com.ecommerce.controller;

import com.ecommerce.dto.ProductRequest;
import com.ecommerce.dto.ProductResponse;

import com.ecommerce.entity.Product;
import com.ecommerce.service.ImageUploadService;
import com.ecommerce.service.ProductService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ImageUploadService imageUploadService;

    private final ProductService productService;

    // CREATE PRODUCT
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ProductResponse createProduct(

            @Valid
            @RequestBody ProductRequest request
    ) {

        return productService.createProduct(request);
    }

    // GET ALL PRODUCTS
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping
    public Page<ProductResponse> getAllProducts(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "5")
            int size,

            @RequestParam(defaultValue = "id")
            String sortBy,

            @RequestParam(required = false)
            String keyword
    ) {

        return productService.getAllProducts(
                page,
                size,
                sortBy,
                keyword
        );
    }

    // GET PRODUCT BY ID
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{id}")
    public ProductResponse getProductById(
            @PathVariable Long id
    ) {

        return productService.getProductById(id);
    }

    // UPDATE PRODUCT
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ProductResponse updateProduct(

            @PathVariable Long id,

            @Valid
            @RequestBody ProductRequest request
    ) {

        return productService.updateProduct(id, request);
    }

    // DELETE PRODUCT
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public String deleteProduct(
            @PathVariable Long id
    ) {

        return productService.deleteProduct(id);
    }

    // IMAGE UPLOAD
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(
            value = "/upload-image",
            consumes = "multipart/form-data"
    )
    public String uploadImage(
            @RequestParam("file") MultipartFile file
    ) throws Exception {

        return imageUploadService.uploadImage(file);
    }

    @GetMapping("/search")
    public Page<ProductResponse> searchProducts(

            @RequestParam String keyword,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "5")
            int size
    ) {

        return productService.searchProducts(
                keyword,
                page,
                size
        );
    }

    @GetMapping("/category/{categoryId}")
    public Page<ProductResponse> getProductsByCategory(

            @PathVariable Long categoryId,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "5")
            int size
    ) {

        return productService
                .getProductsByCategory(
                        categoryId,
                        page,
                        size
                );
    }
}