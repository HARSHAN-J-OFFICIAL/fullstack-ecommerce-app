package com.ecommerce.entity;

import com.ecommerce.enums.DiscountType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType discountType;

    @Column(nullable = false)
    private Double discountValue;

    private Double minimumOrderAmount;

    private Double maximumDiscountAmount;

    private LocalDateTime expiryDate;

    private Integer usageLimit;

    @Builder.Default
    private Integer usedCount = 0;

    @Builder.Default
    private Boolean active = true;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (code != null) {
            code = code.toUpperCase().trim();
        }
    }

    @PreUpdate
    public void onUpdate() {
        if (code != null) {
            code = code.toUpperCase().trim();
        }
    }
}
