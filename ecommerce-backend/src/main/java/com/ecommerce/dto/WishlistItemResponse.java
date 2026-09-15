package com.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistItemResponse {

    private Long id;

    private Long productId;

    private String productName;

    private String productDescription;

    private double productPrice;

    private int stockQuantity;

    private String imageUrl;

    private String categoryName;
}
