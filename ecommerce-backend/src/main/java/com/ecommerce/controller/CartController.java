package com.ecommerce.controller;

import com.ecommerce.entity.Cart;
import com.ecommerce.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Cart", description = "Endpoints for managing user shopping cart items")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @Operation(summary = "View user shopping cart", description = "Retrieves the current authenticated user's cart and itemized products.")
    @GetMapping
    public Cart getCart() {
        return cartService.getCart();
    }

    @Operation(summary = "Add item to cart", description = "Adds a product to the user's cart or increments item quantity.")
    @PostMapping("/add")
    public Cart addToCart(
            @Parameter(description = "Product ID") @RequestParam Long productId,
            @Parameter(description = "Quantity to add") @RequestParam int quantity
    ) {
        return cartService.addToCart(productId, quantity);
    }

    @Operation(summary = "Remove item from cart", description = "Removes an item from the user's cart by cart item ID.")
    @DeleteMapping("/remove/{itemId}")
    public Cart removeItem(@PathVariable Long itemId) {
        return cartService.removeItem(itemId);
    }
}