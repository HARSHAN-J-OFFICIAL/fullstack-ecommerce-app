package com.ecommerce.service;

import com.ecommerce.dto.ApplyCouponResponse;
import com.ecommerce.entity.Coupon;
import com.ecommerce.enums.DiscountType;
import com.ecommerce.repository.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private CartService cartService;

    @InjectMocks
    private CouponService couponService;

    private Coupon validCoupon;
    private Coupon expiredCoupon;
    private Coupon usageLimitedCoupon;
    private Coupon minOrderCoupon;

    @BeforeEach
    void setUp() {
        validCoupon = Coupon.builder()
                .id(1L)
                .code("SAVE20")
                .discountType(DiscountType.PERCENTAGE)
                .discountValue(20.0)
                .minimumOrderAmount(100.0)
                .maximumDiscountAmount(50.0)
                .active(true)
                .expiryDate(LocalDateTime.now().plusDays(5))
                .usageLimit(100)
                .usedCount(5)
                .build();

        expiredCoupon = Coupon.builder()
                .id(2L)
                .code("EXPIRED")
                .discountType(DiscountType.FIXED)
                .discountValue(50.0)
                .active(true)
                .expiryDate(LocalDateTime.now().minusDays(1))
                .build();

        usageLimitedCoupon = Coupon.builder()
                .id(3L)
                .code("LIMITREACHED")
                .discountType(DiscountType.FIXED)
                .discountValue(50.0)
                .active(true)
                .usageLimit(10)
                .usedCount(10)
                .build();

        minOrderCoupon = Coupon.builder()
                .id(4L)
                .code("MIN500")
                .discountType(DiscountType.FIXED)
                .discountValue(100.0)
                .minimumOrderAmount(500.0)
                .active(true)
                .build();
    }

    @Test
    void validateCouponForAmount_WhenValidCoupon_ShouldCalculateDiscountCorrectly() {
        // Arrange
        when(couponRepository.findByCodeIgnoreCase("SAVE20")).thenReturn(Optional.of(validCoupon));

        // Act
        ApplyCouponResponse response = couponService.validateCouponForAmount("SAVE20", 200.0);

        // Assert
        assertNotNull(response);
        assertEquals("SAVE20", response.getCouponCode());
        assertEquals(200.0, response.getOriginalAmount());
        assertEquals(40.0, response.getDiscountAmount()); // 20% of 200 = 40
        assertEquals(160.0, response.getFinalAmount());
    }

    @Test
    void validateCouponForAmount_WhenExpired_ShouldThrowException() {
        // Arrange
        when(couponRepository.findByCodeIgnoreCase("EXPIRED")).thenReturn(Optional.of(expiredCoupon));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> couponService.validateCouponForAmount("EXPIRED", 200.0));
    }

    @Test
    void validateCouponForAmount_WhenUsageLimitExceeded_ShouldThrowException() {
        // Arrange
        when(couponRepository.findByCodeIgnoreCase("LIMITREACHED")).thenReturn(Optional.of(usageLimitedCoupon));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> couponService.validateCouponForAmount("LIMITREACHED", 200.0));
    }

    @Test
    void validateCouponForAmount_WhenSubtotalBelowMinimum_ShouldThrowException() {
        // Arrange
        when(couponRepository.findByCodeIgnoreCase("MIN500")).thenReturn(Optional.of(minOrderCoupon));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> couponService.validateCouponForAmount("MIN500", 300.0));
    }
}
