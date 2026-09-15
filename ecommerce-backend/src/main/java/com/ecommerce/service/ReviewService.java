package com.ecommerce.service;

import com.ecommerce.dto.ReviewRequest;
import com.ecommerce.dto.ReviewResponse;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.Review;
import com.ecommerce.entity.User;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.ReviewRepository;
import com.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final ProductRepository productRepository;

    private final UserRepository userRepository;

    private final OrderRepository orderRepository;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    // ADD REVIEW
    @Transactional
    public ReviewResponse addReview(Long productId, ReviewRequest request) {
        User user = getCurrentUser();

        // 1. Validate rating
        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be an integer between 1 and 5.");
        }

        // 2. Fetch Product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        // 3. Check if user has purchased the product
        boolean hasPurchased = orderRepository.hasUserPurchasedProduct(user.getId(), productId);
        if (!hasPurchased) {
            throw new IllegalArgumentException("You can only review products you have purchased.");
        }

        // 4. Check if user already reviewed this product
        boolean alreadyReviewed = reviewRepository.existsByProductIdAndUserId(productId, user.getId());
        if (alreadyReviewed) {
            throw new IllegalArgumentException("You have already submitted a review for this product.");
        }

        // 5. Create & Save Review
        Review review = Review.builder()
                .rating(request.getRating())
                .comment(request.getComment())
                .createdAt(LocalDateTime.now())
                .user(user)
                .product(product)
                .build();

        Review saved = reviewRepository.save(review);
        return mapToResponse(saved);
    }

    // GET REVIEWS BY PRODUCT
    public List<ReviewResponse> getReviewsByProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }
        List<Review> reviews = reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
        return reviews.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // EDIT REVIEW
    @Transactional
    public ReviewResponse updateReview(Long reviewId, ReviewRequest request) {
        User user = getCurrentUser();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You can only edit your own review.");
        }

        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be an integer between 1 and 5.");
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review updated = reviewRepository.save(review);
        return mapToResponse(updated);
    }

    // DELETE REVIEW (Owner or Admin)
    @Transactional
    public void deleteReview(Long reviewId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = getCurrentUser();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isOwner = review.getUser().getId().equals(user.getId());

        if (!isAdmin && !isOwner) {
            throw new IllegalArgumentException("You are not authorized to delete this review.");
        }

        reviewRepository.delete(review);
    }

    private ReviewResponse mapToResponse(Review review) {
        String userName = review.getUser().getUsername();
        if (userName == null || userName.isBlank()) {
            userName = review.getUser().getEmail();
        }

        return ReviewResponse.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .userId(review.getUser().getId())
                .userName(userName)
                .productId(review.getProduct().getId())
                .build();
    }
}
