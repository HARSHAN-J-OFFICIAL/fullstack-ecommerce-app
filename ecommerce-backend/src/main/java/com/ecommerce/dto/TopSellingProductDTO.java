package com.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopSellingProductDTO {

    private Long productId;

    private String productName;

    private String imageUrl;

    private long totalQuantitySold;

    private double totalRevenueGenerated;
}
