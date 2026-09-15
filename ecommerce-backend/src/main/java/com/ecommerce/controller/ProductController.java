package com.ecommerce.controller;

import com.ecommerce.dto.ProductRequest;
import com.ecommerce.dto.ProductResponse;
import com.ecommerce.service.ImageUploadService;
import com.ecommerce.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Products", description = "Endpoints for product catalog browsing, searching, and admin management")
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ImageUploadService imageUploadService;
    private final ProductService productService;

    @Operation(summary = "Create product (Admin)", description = "Adds a new product to the catalog. Requires ROLE_ADMIN authority.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product created successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ProductResponse createProduct(
            @Valid @RequestBody ProductRequest request
    ) {
        return productService.createProduct(request);
    }

    @Operation(summary = "Get all products", description = "Retrieves a paginated list of products with optional search keyword and custom sorting.")
    @GetMapping
    public Page<ProductResponse> getAllProducts(
            @Parameter(description = "Zero-based page index") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size limit") @RequestParam(defaultValue = "5") int size,
            @Parameter(description = "Sort property field") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Optional search keyword filter") @RequestParam(required = false) String keyword
    ) {
        return productService.getAllProducts(page, size, sortBy, keyword);
    }

    @Operation(summary = "Get product by ID", description = "Retrieves detailed product information by ID.")
    @GetMapping("/{id}")
    public ProductResponse getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @Operation(summary = "Update product (Admin)", description = "Updates product information by ID. Requires ROLE_ADMIN authority.")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ProductResponse updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request
    ) {
        return productService.updateProduct(id, request);
    }

    @Operation(summary = "Delete product (Admin)", description = "Deletes a product by ID. Requires ROLE_ADMIN authority.")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable Long id) {
        return productService.deleteProduct(id);
    }

    @Operation(summary = "Upload product image (Admin)", description = "Uploads a multipart image file to storage and returns the image URL.")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/upload-image", consumes = "multipart/form-data")
    public String uploadImage(@RequestParam("file") MultipartFile file) throws Exception {
        return imageUploadService.uploadImage(file);
    }

    @Operation(summary = "Search products by keyword", description = "Retrieves a paginated list of products matching the keyword search filter.")
    @GetMapping("/search")
    public Page<ProductResponse> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return productService.searchProducts(keyword, page, size);
    }

    @Operation(summary = "Get products by category ID", description = "Retrieves a paginated list of products belonging to a specific category.")
    @GetMapping("/category/{categoryId}")
    public Page<ProductResponse> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return productService.getProductsByCategory(categoryId, page, size);
    }
}