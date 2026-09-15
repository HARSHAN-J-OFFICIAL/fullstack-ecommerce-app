package com.ecommerce.service.impl;

import com.ecommerce.dto.*;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderStatus;
import com.ecommerce.entity.Product;
import com.ecommerce.enums.RoleName;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public AnalyticsResponse getAnalytics() {
        // 1. Sales Analytics
        long totalOrders = orderRepository.count();
        long totalCompletedOrders = orderRepository.countByStatus(OrderStatus.DELIVERED);
        long totalCancelledOrders = orderRepository.countByStatus(OrderStatus.CANCELLED);
        Double rawRevenue = orderRepository.getTotalRevenue();
        double totalRevenue = rawRevenue != null ? rawRevenue : 0.0;
        double averageOrderValue = totalOrders > 0 ? (totalRevenue / totalOrders) : 0.0;

        SalesAnalyticsDTO salesAnalytics = SalesAnalyticsDTO.builder()
                .totalRevenue(roundTwoDecimals(totalRevenue))
                .totalOrders(totalOrders)
                .totalCompletedOrders(totalCompletedOrders)
                .totalCancelledOrders(totalCancelledOrders)
                .averageOrderValue(roundTwoDecimals(averageOrderValue))
                .build();

        // 2. Product Analytics
        long totalProducts = productRepository.count();

        List<Object[]> topSellingRaw = orderRepository.findTopSellingProducts(PageRequest.of(0, 5));
        List<TopSellingProductDTO> topSellingProducts = new ArrayList<>();
        for (Object[] row : topSellingRaw) {
            topSellingProducts.add(TopSellingProductDTO.builder()
                    .productId((Long) row[0])
                    .productName((String) row[1])
                    .imageUrl((String) row[2])
                    .totalQuantitySold(row[3] != null ? ((Number) row[3]).longValue() : 0L)
                    .totalRevenueGenerated(row[4] != null ? roundTwoDecimals(((Number) row[4]).doubleValue()) : 0.0)
                    .build());
        }

        List<Product> lowStockEntities = productRepository.findByStockQuantityGreaterThanAndStockQuantityLessThan(0, 5);
        List<ProductResponse> lowStockProducts = lowStockEntities.stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());

        long outOfStockCount = productRepository.countByStockQuantityLessThanEqual(0);

        ProductAnalyticsDTO productAnalytics = ProductAnalyticsDTO.builder()
                .totalProducts(totalProducts)
                .topSellingProducts(topSellingProducts)
                .lowStockProducts(lowStockProducts)
                .outOfStockCount(outOfStockCount)
                .build();

        // 3. User Analytics
        long totalUsers = userRepository.count();
        long totalAdmins = userRepository.countByRoles_Name(RoleName.ROLE_ADMIN);

        UserAnalyticsDTO userAnalytics = UserAnalyticsDTO.builder()
                .totalUsers(totalUsers)
                .newUsersCount(totalUsers)
                .totalAdmins(totalAdmins)
                .build();

        // 4. Order Analytics
        OrderAnalyticsDTO orderAnalytics = OrderAnalyticsDTO.builder()
                .pendingOrders(orderRepository.countByStatus(OrderStatus.PENDING))
                .confirmedOrders(orderRepository.countByStatus(OrderStatus.CONFIRMED))
                .shippedOrders(orderRepository.countByStatus(OrderStatus.SHIPPED))
                .outForDeliveryOrders(orderRepository.countByStatus(OrderStatus.OUT_FOR_DELIVERY))
                .deliveredOrders(orderRepository.countByStatus(OrderStatus.DELIVERED))
                .cancelledOrders(orderRepository.countByStatus(OrderStatus.CANCELLED))
                .build();

        // 5. Revenue Trends
        List<Order> allOrders = orderRepository.findAll();
        RevenueTrendsDTO revenueTrends = calculateRevenueTrends(allOrders);

        // 6. Recent Orders
        List<Order> recentOrders = orderRepository.findRecentOrders(PageRequest.of(0, 5));

        return AnalyticsResponse.builder()
                .salesAnalytics(salesAnalytics)
                .productAnalytics(productAnalytics)
                .userAnalytics(userAnalytics)
                .orderAnalytics(orderAnalytics)
                .revenueTrends(revenueTrends)
                .recentOrders(recentOrders)
                .build();
    }

    private RevenueTrendsDTO calculateRevenueTrends(List<Order> orders) {
        Map<String, double[]> dailyMap = new LinkedHashMap<>();
        Map<String, double[]> monthlyMap = new LinkedHashMap<>();
        Map<String, double[]> yearlyMap = new LinkedHashMap<>();

        DateTimeFormatter dailyFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter monthlyFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        DateTimeFormatter yearlyFormatter = DateTimeFormatter.ofPattern("yyyy");

        for (Order o : orders) {
            if (o.getStatus() == OrderStatus.CANCELLED || o.getOrderDate() == null) {
                continue;
            }

            String dayKey = o.getOrderDate().format(dailyFormatter);
            String monthKey = o.getOrderDate().format(monthlyFormatter);
            String yearKey = o.getOrderDate().format(yearlyFormatter);

            double amt = o.getTotalAmount();

            dailyMap.computeIfAbsent(dayKey, k -> new double[]{0.0, 0.0});
            dailyMap.get(dayKey)[0] += amt;
            dailyMap.get(dayKey)[1] += 1;

            monthlyMap.computeIfAbsent(monthKey, k -> new double[]{0.0, 0.0});
            monthlyMap.get(monthKey)[0] += amt;
            monthlyMap.get(monthKey)[1] += 1;

            yearlyMap.computeIfAbsent(yearKey, k -> new double[]{0.0, 0.0});
            yearlyMap.get(yearKey)[0] += amt;
            yearlyMap.get(yearKey)[1] += 1;
        }

        List<RevenueTrendItemDTO> dailyTrends = dailyMap.entrySet().stream()
                .map(e -> new RevenueTrendItemDTO(e.getKey(), roundTwoDecimals(e.getValue()[0]), (long) e.getValue()[1]))
                .collect(Collectors.toList());

        List<RevenueTrendItemDTO> monthlyTrends = monthlyMap.entrySet().stream()
                .map(e -> new RevenueTrendItemDTO(e.getKey(), roundTwoDecimals(e.getValue()[0]), (long) e.getValue()[1]))
                .collect(Collectors.toList());

        List<RevenueTrendItemDTO> yearlyTrends = yearlyMap.entrySet().stream()
                .map(e -> new RevenueTrendItemDTO(e.getKey(), roundTwoDecimals(e.getValue()[0]), (long) e.getValue()[1]))
                .collect(Collectors.toList());

        return RevenueTrendsDTO.builder()
                .dailyTrends(dailyTrends)
                .monthlyTrends(monthlyTrends)
                .yearlyTrends(yearlyTrends)
                .build();
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .imageUrl(product.getImageUrl())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .build();
    }

    private double roundTwoDecimals(double val) {
        return Math.round(val * 100.0) / 100.0;
    }
}
