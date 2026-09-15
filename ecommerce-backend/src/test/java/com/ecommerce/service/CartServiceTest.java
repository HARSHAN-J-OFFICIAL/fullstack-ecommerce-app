package com.ecommerce.service;

import com.ecommerce.entity.Cart;
import com.ecommerce.entity.CartItem;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.CartItemRepository;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.impl.CartServiceImpl;
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
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    private User user;
    private Product product;
    private Cart cart;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("john@example.com").build();
        product = new Product(10L, "Phone", "Description", 500.0, 20);

        cartItem = CartItem.builder()
                .id(100L)
                .product(product)
                .quantity(1)
                .build();

        cart = Cart.builder()
                .id(1L)
                .user(user)
                .items(new ArrayList<>())
                .build();

        cartItem.setCart(cart);

        // Security Context Mock
        Authentication authentication = mock(Authentication.class);
        lenient().when(authentication.getName()).thenReturn("john@example.com");
        SecurityContext securityContext = mock(SecurityContext.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void addToCart_WhenProductNew_ShouldAddItemToCart() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // Act
        Cart result = cartService.addToCart(10L, 2);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(2, result.getItems().get(0).getQuantity());
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void addToCart_WhenProductAlreadyInCart_ShouldIncreaseQuantity() {
        // Arrange
        cart.getItems().add(cartItem);
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // Act
        Cart result = cartService.addToCart(10L, 3);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(4, result.getItems().get(0).getQuantity());
    }

    @Test
    void removeItem_WhenItemExists_ShouldRemoveSuccessfully() {
        // Arrange
        cart.getItems().add(cartItem);
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // Act
        Cart result = cartService.removeItem(100L);

        // Assert
        assertNotNull(result);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void addToCart_WhenProductNotFound_ShouldThrowException() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> cartService.addToCart(999L, 1));
    }
}
