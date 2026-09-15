package com.ecommerce.controller;

import com.ecommerce.dto.ApplyCouponRequest;
import com.ecommerce.dto.ApplyCouponResponse;
import com.ecommerce.dto.CouponRequest;
import com.ecommerce.dto.CouponResponse;
import com.ecommerce.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Coupons", description = "Endpoints for coupon redemption and admin discount management")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @Operation(summary = "Apply coupon code", description = "Validates coupon code against cart subtotal, active status, usage limits, and expiration date.")
    @PostMapping("/coupons/apply")
    public ResponseEntity<ApplyCouponResponse> applyCoupon(@RequestBody ApplyCouponRequest request) {
        ApplyCouponResponse response = couponService.applyCoupon(request.getCouponCode());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Remove coupon", description = "Removes applied coupon code from cart state.")
    @DeleteMapping("/coupons/remove")
    public ResponseEntity<?> removeCoupon() {
        return ResponseEntity.ok(Map.of("message", "Coupon removed successfully"));
    }

    @Operation(summary = "Create coupon (Admin)", description = "Creates a new percentage or fixed discount coupon. Requires ROLE_ADMIN authority.")
    @PostMapping("/admin/coupons")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CouponResponse> createCoupon(@RequestBody CouponRequest request) {
        CouponResponse response = couponService.createCoupon(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get all coupons (Admin)", description = "Retrieves all promotional coupons. Requires ROLE_ADMIN authority.")
    @GetMapping("/admin/coupons")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CouponResponse>> getAllCoupons() {
        List<CouponResponse> response = couponService.getAllCoupons();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update coupon (Admin)", description = "Updates coupon parameters or status by ID. Requires ROLE_ADMIN authority.")
    @PutMapping("/admin/coupons/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CouponResponse> updateCoupon(
            @PathVariable Long id,
            @RequestBody CouponRequest request
    ) {
        CouponResponse response = couponService.updateCoupon(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete coupon (Admin)", description = "Deletes a coupon by ID. Requires ROLE_ADMIN authority.")
    @DeleteMapping("/admin/coupons/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.ok(Map.of("message", "Coupon deleted successfully"));
    }
}
