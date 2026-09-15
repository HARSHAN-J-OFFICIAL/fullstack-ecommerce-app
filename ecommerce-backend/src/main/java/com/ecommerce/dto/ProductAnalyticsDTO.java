package com.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductAnalyticsDTO {

    private long totalProducts;

    private List<TopSellingProductDTO> topSellingProducts;

    private List<ProductResponse> lowStockProducts;

    private long outOfStockCount;
}
