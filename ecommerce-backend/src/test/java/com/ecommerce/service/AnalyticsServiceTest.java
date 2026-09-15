package com.ecommerce.service;

import com.ecommerce.dto.AnalyticsResponse;
import com.ecommerce.entity.OrderStatus;
import com.ecommerce.enums.RoleName;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.impl.AnalyticsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AnalyticsServiceImpl analyticsService;

    @Test
    void getAnalytics_ShouldReturnCalculatedAnalyticsResponse() {
        // Arrange
        when(orderRepository.count()).thenReturn(10L);
        when(orderRepository.countByStatus(OrderStatus.DELIVERED)).thenReturn(6L);
        when(orderRepository.countByStatus(OrderStatus.CANCELLED)).thenReturn(1L);
        when(orderRepository.countByStatus(OrderStatus.PENDING)).thenReturn(2L);
        when(orderRepository.countByStatus(OrderStatus.CONFIRMED)).thenReturn(1L);
        when(orderRepository.countByStatus(OrderStatus.SHIPPED)).thenReturn(0L);
        when(orderRepository.countByStatus(OrderStatus.OUT_FOR_DELIVERY)).thenReturn(0L);

        when(orderRepository.getTotalRevenue()).thenReturn(5000.0);
        when(orderRepository.findTopSellingProducts(any(Pageable.class))).thenReturn(Collections.emptyList());
        when(orderRepository.findRecentOrders(any(Pageable.class))).thenReturn(Collections.emptyList());
        when(orderRepository.findAll()).thenReturn(Collections.emptyList());

        when(productRepository.count()).thenReturn(25L);
        when(productRepository.findByStockQuantityGreaterThanAndStockQuantityLessThan(0, 5)).thenReturn(Collections.emptyList());
        when(productRepository.countByStockQuantityLessThanEqual(0)).thenReturn(2L);

        when(userRepository.count()).thenReturn(50L);
        when(userRepository.countByRoles_Name(RoleName.ROLE_ADMIN)).thenReturn(2L);

        // Act
        AnalyticsResponse response = analyticsService.getAnalytics();

        // Assert
        assertNotNull(response);
        assertEquals(5000.0, response.getSalesAnalytics().getTotalRevenue());
        assertEquals(10L, response.getSalesAnalytics().getTotalOrders());
        assertEquals(500.0, response.getSalesAnalytics().getAverageOrderValue()); // 5000 / 10 = 500
        assertEquals(25L, response.getProductAnalytics().getTotalProducts());
        assertEquals(2L, response.getProductAnalytics().getOutOfStockCount());
        assertEquals(50L, response.getUserAnalytics().getTotalUsers());
        assertEquals(2L, response.getUserAnalytics().getTotalAdmins());
    }
}
