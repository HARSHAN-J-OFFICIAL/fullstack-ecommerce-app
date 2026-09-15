package com.ecommerce.service;

import com.ecommerce.dto.ApplyCouponResponse;
import com.ecommerce.dto.CouponRequest;
import com.ecommerce.dto.CouponResponse;
import com.ecommerce.entity.Cart;
import com.ecommerce.entity.CartItem;
import com.ecommerce.entity.Coupon;
import com.ecommerce.enums.DiscountType;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    private final CartService cartService;

    // VALIDATE & APPLY COUPON FOR LOGGED-IN USER
    public ApplyCouponResponse applyCoupon(String couponCode) {
        Cart cart = cartService.getCart();
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty.");
        }

        double subtotal = 0;
        for (CartItem item : cart.getItems()) {
            subtotal += item.getQuantity() * item.getProduct().getPrice();
        }

        return validateCouponForAmount(couponCode, subtotal);
    }

    // INTERNAL & BACKEND VERIFICATION OF COUPON DISCOUNT
    public ApplyCouponResponse validateCouponForAmount(String couponCode, double subtotal) {
        if (couponCode == null || couponCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Coupon code cannot be empty.");
        }

        Coupon coupon = couponRepository.findByCodeIgnoreCase(couponCode.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid coupon code: " + couponCode));

        if (!Boolean.TRUE.equals(coupon.getActive())) {
            throw new IllegalArgumentException("Coupon '" + coupon.getCode() + "' is inactive.");
        }

        if (coupon.getExpiryDate() != null && coupon.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Coupon '" + coupon.getCode() + "' has expired.");
        }

        if (coupon.getUsageLimit() != null && coupon.getUsedCount() >= coupon.getUsageLimit()) {
            throw new IllegalArgumentException("Coupon '" + coupon.getCode() + "' usage limit reached.");
        }

        if (coupon.getMinimumOrderAmount() != null && subtotal < coupon.getMinimumOrderAmount()) {
            throw new IllegalArgumentException("Minimum order amount of ₹" + coupon.getMinimumOrderAmount() + " required to use coupon '" + coupon.getCode() + "'.");
        }

        double discount = 0.0;
        if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {
            discount = (subtotal * coupon.getDiscountValue()) / 100.0;
            if (coupon.getMaximumDiscountAmount() != null && discount > coupon.getMaximumDiscountAmount()) {
                discount = coupon.getMaximumDiscountAmount();
            }
        } else if (coupon.getDiscountType() == DiscountType.FIXED) {
            discount = Math.min(coupon.getDiscountValue(), subtotal);
        }

        discount = Math.round(discount * 100.0) / 100.0;
        double finalAmount = Math.max(0.0, Math.round((subtotal - discount) * 100.0) / 100.0);

        return ApplyCouponResponse.builder()
                .couponCode(coupon.getCode())
                .description(coupon.getDescription())
                .originalAmount(Math.round(subtotal * 100.0) / 100.0)
                .discountAmount(discount)
                .finalAmount(finalAmount)
                .build();
    }

    // INCREMENT COUPON USAGE COUNT ON ORDER PLACEMENT
    @Transactional
    public void incrementCouponUsage(String couponCode) {
        if (couponCode != null && !couponCode.trim().isEmpty()) {
            couponRepository.findByCodeIgnoreCase(couponCode.trim()).ifPresent(coupon -> {
                coupon.setUsedCount(coupon.getUsedCount() + 1);
                couponRepository.save(coupon);
            });
        }
    }

    // ADMIN: CREATE COUPON
    @Transactional
    public CouponResponse createCoupon(CouponRequest request) {
        if (request.getCode() == null || request.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Coupon code is required.");
        }

        String formattedCode = request.getCode().trim().toUpperCase();
        if (couponRepository.existsByCodeIgnoreCase(formattedCode)) {
            throw new IllegalArgumentException("Coupon code '" + formattedCode + "' already exists.");
        }

        Coupon coupon = Coupon.builder()
                .code(formattedCode)
                .description(request.getDescription())
                .discountType(request.getDiscountType() != null ? request.getDiscountType() : DiscountType.PERCENTAGE)
                .discountValue(request.getDiscountValue() != null ? request.getDiscountValue() : 0.0)
                .minimumOrderAmount(request.getMinimumOrderAmount() != null ? request.getMinimumOrderAmount() : 0.0)
                .maximumDiscountAmount(request.getMaximumDiscountAmount())
                .expiryDate(request.getExpiryDate())
                .usageLimit(request.getUsageLimit())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        Coupon saved = couponRepository.save(coupon);
        return mapToResponse(saved);
    }

    // ADMIN: GET ALL COUPONS
    public List<CouponResponse> getAllCoupons() {
        return couponRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ADMIN: UPDATE COUPON
    @Transactional
    public CouponResponse updateCoupon(Long id, CouponRequest request) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with id: " + id));

        if (request.getCode() != null && !request.getCode().trim().isEmpty()) {
            String formattedCode = request.getCode().trim().toUpperCase();
            if (!formattedCode.equalsIgnoreCase(coupon.getCode()) && couponRepository.existsByCodeIgnoreCase(formattedCode)) {
                throw new IllegalArgumentException("Coupon code '" + formattedCode + "' already exists.");
            }
            coupon.setCode(formattedCode);
        }

        if (request.getDescription() != null) coupon.setDescription(request.getDescription());
        if (request.getDiscountType() != null) coupon.setDiscountType(request.getDiscountType());
        if (request.getDiscountValue() != null) coupon.setDiscountValue(request.getDiscountValue());
        if (request.getMinimumOrderAmount() != null) coupon.setMinimumOrderAmount(request.getMinimumOrderAmount());
        coupon.setMaximumDiscountAmount(request.getMaximumDiscountAmount());
        coupon.setExpiryDate(request.getExpiryDate());
        coupon.setUsageLimit(request.getUsageLimit());
        if (request.getActive() != null) coupon.setActive(request.getActive());

        Coupon updated = couponRepository.save(coupon);
        return mapToResponse(updated);
    }

    // ADMIN: DELETE COUPON
    @Transactional
    public void deleteCoupon(Long id) {
        if (!couponRepository.existsById(id)) {
            throw new ResourceNotFoundException("Coupon not found with id: " + id);
        }
        couponRepository.deleteById(id);
    }

    private CouponResponse mapToResponse(Coupon coupon) {
        return CouponResponse.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .description(coupon.getDescription())
                .discountType(coupon.getDiscountType())
                .discountValue(coupon.getDiscountValue())
                .minimumOrderAmount(coupon.getMinimumOrderAmount())
                .maximumDiscountAmount(coupon.getMaximumDiscountAmount())
                .expiryDate(coupon.getExpiryDate())
                .usageLimit(coupon.getUsageLimit())
                .usedCount(coupon.getUsedCount())
                .active(coupon.getActive())
                .createdAt(coupon.getCreatedAt())
                .build();
    }
}
