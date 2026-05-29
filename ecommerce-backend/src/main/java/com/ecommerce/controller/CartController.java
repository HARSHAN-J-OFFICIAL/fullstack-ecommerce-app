package com.ecommerce.controller;

import com.ecommerce.entity.Cart;
import com.ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public Cart getCart() {
        return cartService.getCart();
    }

    @PostMapping("/add")
    public Cart addToCart(
            @RequestParam Long productId,
            @RequestParam int quantity
    ) {
        return cartService.addToCart(productId, quantity);
    }

    @DeleteMapping("/remove/{itemId}")
    public Cart removeItem(@PathVariable Long itemId) {
        return cartService.removeItem(itemId);
    }
}