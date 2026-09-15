package com.ecommerce.service;

import com.ecommerce.dto.WishlistResponse;
import com.ecommerce.entity.*;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.repository.WishlistItemRepository;
import com.ecommerce.repository.WishlistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishlistServiceTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private WishlistItemRepository wishlistItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartService cartService;

    @InjectMocks
    private WishlistService wishlistService;

    private User user;
    private Product product;
    private Wishlist wishlist;
    private WishlistItem wishlistItem;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("john@example.com").build();
        product = new Product(10L, "Smartwatch", "Description", 199.99, 10);

        wishlist = Wishlist.builder()
                .id(1L)
                .user(user)
                .items(new ArrayList<>())
                .build();

        wishlistItem = WishlistItem.builder()
                .id(100L)
                .wishlist(wishlist)
                .product(product)
                .build();

        // Security Context Mock
        Authentication authentication = mock(Authentication.class);
        lenient().when(authentication.getName()).thenReturn("john@example.com");
        lenient().when(authentication.isAuthenticated()).thenReturn(true);
        SecurityContext securityContext = mock(SecurityContext.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void addToWishlist_WhenNewItem_ShouldAddProductToWishlist() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(wishlistRepository.findByUser(user)).thenReturn(Optional.of(wishlist));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(wishlistItemRepository.save(any(WishlistItem.class))).thenAnswer(i -> i.getArguments()[0]);
        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(wishlist);

        // Act
        WishlistResponse response = wishlistService.addToWishlist(10L);

        // Assert
        assertNotNull(response);
        verify(wishlistRepository, times(1)).save(any(Wishlist.class));
    }

    @Test
    void addToWishlist_WhenItemAlreadyExists_ShouldPreventDuplicate() {
        // Arrange
        wishlist.getItems().add(wishlistItem);
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(wishlistRepository.findByUser(user)).thenReturn(Optional.of(wishlist));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(wishlistItemRepository.existsByWishlistIdAndProductId(1L, 10L)).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> wishlistService.addToWishlist(10L));
        verify(wishlistRepository, never()).save(any(Wishlist.class));
    }

    @Test
    void removeFromWishlist_WhenItemExists_ShouldRemoveSuccessfully() {
        // Arrange
        wishlist.getItems().add(wishlistItem);
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(wishlistRepository.findByUser(user)).thenReturn(Optional.of(wishlist));
        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(wishlist);

        // Act
        WishlistResponse response = wishlistService.removeFromWishlist(10L);

        // Assert
        assertNotNull(response);
        verify(wishlistRepository, times(1)).save(any(Wishlist.class));
    }

    @Test
    void moveToCart_WhenItemInWishlist_ShouldAddToCartAndRemoveFromWishlist() {
        // Arrange
        wishlist.getItems().add(wishlistItem);
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(wishlistRepository.findByUser(user)).thenReturn(Optional.of(wishlist));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(cartService.addToCart(10L, 1)).thenReturn(null);
        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(wishlist);

        // Act
        WishlistResponse response = wishlistService.moveToCart(10L);

        // Assert
        assertNotNull(response);
        verify(cartService, times(1)).addToCart(10L, 1);
        verify(wishlistRepository, times(1)).save(any(Wishlist.class));
    }
}
