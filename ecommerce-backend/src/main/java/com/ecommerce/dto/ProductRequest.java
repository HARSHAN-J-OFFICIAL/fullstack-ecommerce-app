package com.ecommerce.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Product creation and update payload")
public class ProductRequest {

    @Schema(description = "Product title / name", example = "Wireless Mechanical Keyboard", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Product name is required")
    private String name;

    @Schema(description = "Detailed product description", example = "RGB backlit wireless mechanical keyboard with blue switches", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Description is required")
    private String description;

    @Schema(description = "Price per unit (INR)", example = "2999.0", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Price is required")
    @Min(value = 1, message = "Price must be greater than 0")
    private Double price;

    @Schema(description = "Initial inventory stock count", example = "50", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stockQuantity;

    @Schema(description = "Image URL or path", example = "https://images.unsplash.com/photo-1587829741301-dc798b83add3")
    private String imageUrl;

    @Schema(description = "Target category ID", example = "1")
    private Long categoryId;

    public ProductRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getImageUrl(){
        return imageUrl;
    }

    public void setImageUrl(String imageUrl){
        this.imageUrl=imageUrl;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
