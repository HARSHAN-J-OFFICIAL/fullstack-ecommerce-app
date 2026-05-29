package com.ecommerce.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    private Long id;

    private String name;

    private String description;

    private double price;

    private int stockQuantity;

    private String imageUrl;

    private Long categoryId;

    private String categoryName;
}