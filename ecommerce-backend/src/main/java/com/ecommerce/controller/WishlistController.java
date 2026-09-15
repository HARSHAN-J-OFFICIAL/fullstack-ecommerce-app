package com.ecommerce.controller;

import com.ecommerce.dto.WishlistResponse;
import com.ecommerce.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Wishlist", description = "Endpoints for managing user product wishlist")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @Operation(summary = "Get user wishlist", description = "Retrieves all saved items in the authenticated user's wishlist.")
    @GetMapping
    public ResponseEntity<WishlistResponse> getWishlist() {
        WishlistResponse response = wishlistService.getWishlist();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Add product to wishlist", description = "Adds a product to the user's wishlist.")
    @PostMapping("/add/{productId}")
    public ResponseEntity<WishlistResponse> addToWishlist(@PathVariable Long productId) {
        WishlistResponse response = wishlistService.addToWishlist(productId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Remove product from wishlist", description = "Removes a product from the user's wishlist by product ID.")
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<WishlistResponse> removeFromWishlist(@PathVariable Long productId) {
        WishlistResponse response = wishlistService.removeFromWishlist(productId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Move product to cart", description = "Adds a wishlist item to the user's shopping cart and removes it from the wishlist.")
    @PostMapping("/move-to-cart/{productId}")
    public ResponseEntity<WishlistResponse> moveToCart(@PathVariable Long productId) {
        WishlistResponse response = wishlistService.moveToCart(productId);
        return ResponseEntity.ok(response);
    }
}
