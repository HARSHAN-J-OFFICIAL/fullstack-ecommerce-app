package com.ecommerce.repository;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.User;

import com.ecommerce.entity.OrderStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository
        extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user);

    Optional<Order> findByPaymentId(String paymentId);

    Optional<Order> findByRazorpayOrderId(String razorpayOrderId);

    @Query("SELECT o FROM Order o WHERE " +
           "(:status IS NULL OR o.status = :status) AND " +
           "(:search IS NULL OR :search = '' OR " +
           " CAST(o.id AS string) LIKE CONCAT('%', :search, '%') OR " +
           " LOWER(o.user.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Order> searchOrders(@Param("status") OrderStatus status,
                             @Param("search") String search,
                             Pageable pageable);

    @Query("SELECT COUNT(o) > 0 FROM Order o JOIN o.items i WHERE o.user.id = :userId AND i.product.id = :productId")
    boolean hasUserPurchasedProduct(@Param("userId") Long userId, @Param("productId") Long productId);

    long countByStatus(OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0.0) FROM Order o WHERE o.status != com.ecommerce.entity.OrderStatus.CANCELLED")
    Double getTotalRevenue();

    @Query("SELECT o FROM Order o ORDER BY o.orderDate DESC")
    List<Order> findRecentOrders(Pageable pageable);

    @Query("SELECT i.product.id, i.product.name, i.product.imageUrl, SUM(i.quantity), SUM(i.quantity * i.price) " +
           "FROM Order o JOIN o.items i WHERE o.status != com.ecommerce.entity.OrderStatus.CANCELLED " +
           "GROUP BY i.product.id, i.product.name, i.product.imageUrl " +
           "ORDER BY SUM(i.quantity) DESC")
    List<Object[]> findTopSellingProducts(Pageable pageable);
}