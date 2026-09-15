package com.ecommerce.service.impl;

import com.ecommerce.entity.*;

import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.AddressRepository;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.UserRepository;

import com.ecommerce.service.CartService;
import com.ecommerce.service.OrderService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

import com.ecommerce.service.CouponService;
import com.ecommerce.service.EmailService;
import com.ecommerce.dto.ApplyCouponResponse;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final CartRepository cartRepository;

    private final OrderRepository orderRepository;

    private final AddressRepository addressRepository;

    private final CartService cartService;

    private final UserRepository userRepository;

    private final CouponService couponService;

    private final EmailService emailService;

    @Override
    @Transactional
    public Order placeOrder() {
        return placeOrder(null, null);
    }

    @Override
    @Transactional
    public Order placeOrder(Long addressId) {
        return placeOrder(addressId, null);
    }

    @Override
    @Transactional
    public Order placeOrder(Long addressId, String couponCode) {

        // Get logged-in user email
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        // Get user from DB
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        // Get user's cart
        Cart cart = cartService.getCart();

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        // Create new order
        Order order = new Order();

        order.setUser(user);

        order.setOrderDate(LocalDateTime.now());

        order.setStatus(OrderStatus.PENDING);

        // Associate address if provided
        if (addressId != null) {
            Address address = addressRepository.findById(addressId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Address not found"));

            if (!address.getUser().getId().equals(user.getId())) {
                throw new IllegalArgumentException("Address does not belong to user");
            }

            order.setShippingAddress(address);
        }

        List<OrderItem> orderItems = new ArrayList<>();

        double total = 0;

        // Copy cart items -> order items
        for (CartItem cartItem : cart.getItems()) {

            Product product = cartItem.getProduct();

            if (product.getStockQuantity()
                    < cartItem.getQuantity()) {

                throw new RuntimeException(
                        "Insufficient stock for product: "
                                + product.getName()
                );
            }

            product.setStockQuantity(

                    product.getStockQuantity()
                            - cartItem.getQuantity()
            );

            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);

            orderItem.setProduct(cartItem.getProduct());

            orderItem.setQuantity(cartItem.getQuantity());

            orderItem.setPrice(
                    cartItem.getProduct().getPrice()
            );

            total += cartItem.getQuantity()
                    * cartItem.getProduct().getPrice();

            orderItems.add(orderItem);
        }

        order.setItems(orderItems);

        // Apply coupon if provided
        double finalTotal = total;
        if (couponCode != null && !couponCode.trim().isEmpty()) {
            ApplyCouponResponse couponRes = couponService.validateCouponForAmount(couponCode, total);
            order.setCouponCode(couponRes.getCouponCode());
            order.setDiscountAmount(couponRes.getDiscountAmount());
            finalTotal = couponRes.getFinalAmount();
            couponService.incrementCouponUsage(couponRes.getCouponCode());
        }

        order.setTotalAmount(finalTotal);

        // Save order
        Order savedOrder = orderRepository.save(order);

        // Record initial status history
        OrderStatusHistory history = OrderStatusHistory.builder()
                .order(savedOrder)
                .status(OrderStatus.PENDING)
                .timestamp(LocalDateTime.now())
                .comment("Order placed")
                .build();
        savedOrder.getStatusHistory().add(history);
        savedOrder = orderRepository.save(savedOrder);

        // Clear cart after order placed
        cart.getItems().clear();
        cartRepository.save(cart);

        // Send confirmation email to customer & notification email to admin
        emailService.sendOrderConfirmationEmail(savedOrder);
        emailService.sendAdminOrderNotificationEmail(savedOrder);

        return savedOrder;
    }

    @Override
    public List<Order> getMyOrders() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        return orderRepository.findByUser(user);
    }

    @Override
    @Transactional
    public Order placeRazorpayOrder(Long addressId, String razorpayOrderId, String razorpayPaymentId, String paymentStatus) {
        return placeRazorpayOrder(addressId, razorpayOrderId, razorpayPaymentId, paymentStatus, null);
    }

    @Override
    @Transactional
    public Order placeRazorpayOrder(Long addressId, String razorpayOrderId, String razorpayPaymentId, String paymentStatus, String couponCode) {
        if (razorpayPaymentId != null && !razorpayPaymentId.isEmpty()) {
            java.util.Optional<Order> existingOrder = orderRepository.findByPaymentId(razorpayPaymentId);
            if (existingOrder.isPresent()) {
                return existingOrder.get();
            }
        }

        Order order = placeOrder(addressId, couponCode);
        order.setRazorpayOrderId(razorpayOrderId);
        order.setPaymentId(razorpayPaymentId);
        order.setPaymentStatus(paymentStatus != null ? paymentStatus : "PAID");
        order.setPaymentMethod("RAZORPAY");
        order.setStatus(OrderStatus.CONFIRMED);

        OrderStatusHistory history = OrderStatusHistory.builder()
                .order(order)
                .status(OrderStatus.CONFIRMED)
                .timestamp(LocalDateTime.now())
                .comment("Payment confirmed via Razorpay")
                .build();
        order.getStatusHistory().add(history);

        Order savedOrder = orderRepository.save(order);

        // Send payment success email
        emailService.sendPaymentSuccessEmail(savedOrder, razorpayPaymentId);

        return savedOrder;
    }

    @Override
    public Order getOrderById(Long id) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to view this order.");
        }

        return order;
    }

    @Override
    public org.springframework.data.domain.Page<Order> getAllOrders(OrderStatus status, String search, org.springframework.data.domain.Pageable pageable) {
        return orderRepository.searchOrders(status, search, pageable);
    }

    @Override
    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus, String comment) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        OrderStatus oldStatus = order.getStatus();
        order.setStatus(newStatus);

        OrderStatusHistory historyEntry = OrderStatusHistory.builder()
                .order(order)
                .status(newStatus)
                .timestamp(LocalDateTime.now())
                .comment(comment != null && !comment.trim().isEmpty() ? comment : "Status updated to " + newStatus)
                .build();

        order.getStatusHistory().add(historyEntry);

        Order updatedOrder = orderRepository.save(order);

        // Send status update email if status changed
        if (oldStatus != newStatus) {
            emailService.sendOrderStatusUpdateEmail(updatedOrder, oldStatus, newStatus);
        }

        return updatedOrder;
    }
}