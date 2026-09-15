package com.ecommerce.service;

import com.ecommerce.entity.Order;

import java.util.List;

public interface OrderService {

    // Place new order
    Order placeOrder();

    // Place new order with shipping address
    Order placeOrder(Long addressId);

    // Place new order with shipping address and coupon
    Order placeOrder(Long addressId, String couponCode);

    // Place order after payment verification
    Order placeRazorpayOrder(Long addressId, String razorpayOrderId, String razorpayPaymentId, String paymentStatus);

    // Place order after payment verification with coupon
    Order placeRazorpayOrder(Long addressId, String razorpayOrderId, String razorpayPaymentId, String paymentStatus, String couponCode);

    // Get logged-in user's orders
    List<Order> getMyOrders();

    // Get order by ID
    Order getOrderById(Long id);

    // Admin: Get all orders paginated & filtered
    org.springframework.data.domain.Page<Order> getAllOrders(com.ecommerce.entity.OrderStatus status, String search, org.springframework.data.domain.Pageable pageable);

    // Admin: Update order status
    Order updateOrderStatus(Long orderId, com.ecommerce.entity.OrderStatus status, String comment);
}