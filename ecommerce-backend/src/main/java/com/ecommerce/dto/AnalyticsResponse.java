package com.ecommerce.dto;

import com.ecommerce.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsResponse {

    private SalesAnalyticsDTO salesAnalytics;

    private ProductAnalyticsDTO productAnalytics;

    private UserAnalyticsDTO userAnalytics;

    private OrderAnalyticsDTO orderAnalytics;

    private RevenueTrendsDTO revenueTrends;

    private List<Order> recentOrders;
}
