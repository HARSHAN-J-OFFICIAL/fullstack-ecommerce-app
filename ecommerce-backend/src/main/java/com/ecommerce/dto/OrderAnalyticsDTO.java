package com.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderAnalyticsDTO {

    private long pendingOrders;

    private long confirmedOrders;

    private long shippedOrders;

    private long outForDeliveryOrders;

    private long deliveredOrders;

    private long cancelledOrders;
}
