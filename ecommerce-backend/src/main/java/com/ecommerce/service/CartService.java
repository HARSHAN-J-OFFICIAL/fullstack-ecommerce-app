package com.ecommerce.service;

import com.ecommerce.entity.Cart;

public interface CartService {

    Cart getCart();

    Cart addToCart(Long productId, int quantity);

    Cart removeItem(Long cartItemId);
}