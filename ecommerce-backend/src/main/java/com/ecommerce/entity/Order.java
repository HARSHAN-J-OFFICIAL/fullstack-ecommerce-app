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
}