package com.ecommerce.service;

import com.ecommerce.entity.*;
import com.ecommerce.repository.AddressRepository;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.impl.OrderServiceImpl;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private CartService cartService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CouponService couponService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private Product product;
    private Cart cart;
    private CartItem cartItem;
    private Address address;
    private Order order;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("john@example.com").build();
        product = new Product(10L, "Tablet", "High performance tablet", 300.0, 10);

        cartItem = CartItem.builder()
                .id(100L)
                .product(product)
                .quantity(2)
                .build();

        cart = Cart.builder()
                .id(1L)
                .user(user)
                .items(new ArrayList<>(List.of(cartItem)))
                .build();

        address = Address.builder()
                .id(5L)
                .fullName("John Doe")
                .phoneNumber("9876543210")
                .street("Main St")
                .city("Springfield")
                .state("IL")
                .pincode("62701")
                .user(user)
                .build();

        order = Order.builder()
                .id(500L)
                .user(user)
                .totalAmount(600.0)
                .status(OrderStatus.PENDING)
                .items(new ArrayList<>())
                .build();

        // Security Context Mock
        Authentication authentication = mock(Authentication.class);
        lenient().when(authentication.getName()).thenReturn("john@example.com");
        SecurityContext securityContext = mock(SecurityContext.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void placeOrder_WhenValidCartAndAddress_ShouldPlaceOrderSuccessfully() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(cartService.getCart()).thenReturn(cart);
        when(addressRepository.findById(5L)).thenReturn(Optional.of(address));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> {
            Order o = (Order) i.getArguments()[0];
            if (o.getId() == null) o.setId(500L);
            if (o.getStatusHistory() == null) o.setStatusHistory(new ArrayList<>());
            return o;
        });

        // Act
        Order createdOrder = orderService.placeOrder(5L, null);

        // Assert
        assertNotNull(createdOrder);
        assertEquals(600.0, createdOrder.getTotalAmount());
        assertEquals(8, product.getStockQuantity()); // Stock reduced from 10 to 8
        assertTrue(cart.getItems().isEmpty()); // Cart cleared
        verify(emailService, times(1)).sendOrderConfirmationEmail(any(Order.class));
        verify(emailService, times(1)).sendAdminOrderNotificationEmail(any(Order.class));
    }

    @Test
    void placeOrder_WhenCartIsEmpty_ShouldThrowException() {
        // Arrange
        cart.getItems().clear();
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(cartService.getCart()).thenReturn(cart);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> orderService.placeOrder(5L, null));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void getMyOrders_ShouldReturnUserOrders() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(orderRepository.findByUser(user)).thenReturn(List.of(order));

        // Act
        List<Order> orders = orderService.getMyOrders();

        // Assert
        assertNotNull(orders);
        assertEquals(1, orders.size());
        assertEquals(500L, orders.get(0).getId());
    }

    @Test
    void getOrderById_WhenOrderExists_ShouldReturnOrder() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(orderRepository.findById(500L)).thenReturn(Optional.of(order));

        // Act
        Order result = orderService.getOrderById(500L);

        // Assert
        assertNotNull(result);
        assertEquals(500L, result.getId());
    }
}
