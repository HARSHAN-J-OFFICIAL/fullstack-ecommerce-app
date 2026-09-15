package com.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplyCouponResponse {

    private String couponCode;

    private String description;

    private double originalAmount;

    private double discountAmount;

    private double finalAmount;
}
