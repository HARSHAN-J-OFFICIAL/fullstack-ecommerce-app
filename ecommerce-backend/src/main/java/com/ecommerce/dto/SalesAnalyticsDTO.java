package com.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesAnalyticsDTO {

    private double totalRevenue;

    private long totalOrders;

    private long totalCompletedOrders;

    private long totalCancelledOrders;

    private double averageOrderValue;
}
