package com.ecommerce.service;

import com.ecommerce.dto.ReviewRequest;
import com.ecommerce.dto.ReviewResponse;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.Review;
import com.ecommerce.entity.User;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.ReviewRepository;
import com.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private ReviewService reviewService;

    private User user;
    private Product product;
    private Review review;
    private ReviewRequest reviewRequest;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("john@example.com").username("JohnDoe").build();
        product = new Product(10L, "Camera", "Description", 400.0, 5);

        review = Review.builder()
                .id(100L)
                .rating(5)
                .comment("Great camera!")
                .user(user)
                .product(product)
                .build();

        reviewRequest = new ReviewRequest();
        reviewRequest.setRating(5);
        reviewRequest.setComment("Great camera!");

        // Security Context Mock
        Authentication authentication = mock(Authentication.class);
        lenient().when(authentication.getName()).thenReturn("john@example.com");
        lenient().when(authentication.isAuthenticated()).thenReturn(true);
        SecurityContext securityContext = mock(SecurityContext.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void addReview_WhenUserHasPurchasedAndNotReviewed_ShouldAddReview() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(orderRepository.hasUserPurchasedProduct(1L, 10L)).thenReturn(true);
        when(reviewRepository.existsByProductIdAndUserId(10L, 1L)).thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        // Act
        ReviewResponse response = reviewService.addReview(10L, reviewRequest);

        // Assert
        assertNotNull(response);
        assertEquals(5, response.getRating());
        assertEquals("Great camera!", response.getComment());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void addReview_WhenUserAlreadyReviewed_ShouldThrowException() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(orderRepository.hasUserPurchasedProduct(1L, 10L)).thenReturn(true);
        when(reviewRepository.existsByProductIdAndUserId(10L, 1L)).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> reviewService.addReview(10L, reviewRequest));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void updateReview_WhenOwnReview_ShouldUpdateSuccessfully() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(reviewRepository.findById(100L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        reviewRequest.setRating(4);
        reviewRequest.setComment("Good camera.");

        // Act
        ReviewResponse response = reviewService.updateReview(100L, reviewRequest);

        // Assert
        assertNotNull(response);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void deleteReview_WhenOwnReview_ShouldDeleteSuccessfully() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(reviewRepository.findById(100L)).thenReturn(Optional.of(review));
        doNothing().when(reviewRepository).delete(review);

        // Act
        reviewService.deleteReview(100L);

        // Assert
        verify(reviewRepository, times(1)).delete(review);
    }
}
