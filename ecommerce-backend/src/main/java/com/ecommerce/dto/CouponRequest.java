package com.ecommerce.dto;

import com.ecommerce.enums.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponRequest {

    private String code;

    private String description;

    private DiscountType discountType;

    private Double discountValue;

    private Double minimumOrderAmount;

    private Double maximumDiscountAmount;

    private LocalDateTime expiryDate;

    private Integer usageLimit;

    private Boolean active;
}
