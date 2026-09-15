package com.ecommerce.controller;

import com.ecommerce.dto.ReviewRequest;
import com.ecommerce.dto.ReviewResponse;
import com.ecommerce.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Reviews", description = "Endpoints for product ratings and customer reviews")
@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "Add product review", description = "Allows a verified buyer to submit a rating (1-5) and review for a product.")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/products/{productId}/reviews")
    public ResponseEntity<ReviewResponse> addReview(
            @PathVariable Long productId,
            @RequestBody ReviewRequest request
    ) {
        ReviewResponse response = reviewService.addReview(productId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get reviews for product", description = "Retrieves all public reviews and average rating for a product.")
    @GetMapping("/products/{productId}/reviews")
    public ResponseEntity<List<ReviewResponse>> getReviewsByProduct(
            @PathVariable Long productId
    ) {
        List<ReviewResponse> reviews = reviewService.getReviewsByProduct(productId);
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "Update review", description = "Allows a user to edit their existing review rating and comment.")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long reviewId,
            @RequestBody ReviewRequest request
    ) {
        ReviewResponse response = reviewService.updateReview(reviewId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete review", description = "Deletes a review by review ID. Users can delete their own reviews; Admins can delete any review.")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<?> deleteReview(
            @PathVariable Long reviewId
    ) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok(Map.of("message", "Review deleted successfully"));
    }
}
