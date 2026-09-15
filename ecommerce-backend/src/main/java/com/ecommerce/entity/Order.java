package com.ecommerce.entity;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User who placed the order
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Order items
    @Builder.Default
    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderItem> items = new ArrayList<>();

    // Total order amount
    private double totalAmount;

    // Order status
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    // Order creation time
    private LocalDateTime orderDate;

    // Shipping address for the order
    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address shippingAddress;

    // Payment details
    private String paymentId;
    private String razorpayOrderId;
    private String paymentStatus;
    private String paymentMethod;

    // Coupon details
    private String couponCode;
    @Builder.Default
    private double discountAmount = 0.0;

    // Status history tracking
    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("timestamp ASC")
    private List<OrderStatusHistory> statusHistory = new ArrayList<>();
}