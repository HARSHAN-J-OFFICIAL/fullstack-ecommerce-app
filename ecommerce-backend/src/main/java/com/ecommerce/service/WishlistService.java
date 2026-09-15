package com.ecommerce.service;

import com.ecommerce.dto.WishlistItemResponse;
import com.ecommerce.dto.WishlistResponse;
import com.ecommerce.entity.*;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.repository.WishlistItemRepository;
import com.ecommerce.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartService cartService;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalArgumentException("User is not authenticated");
        }
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    private Wishlist getOrCreateWishlist(User user) {
        Wishlist wishlist = wishlistRepository.findByUser(user)
                .orElseGet(() -> wishlistRepository.save(
                        Wishlist.builder()
                                .user(user)
                                .items(new ArrayList<>())
                                .build()
                ));

        if (wishlist.getItems() == null) {
            wishlist.setItems(new ArrayList<>());
        }

        return wishlist;
    }

    // GET USER WISHLIST
    @Transactional(readOnly = true)
    public WishlistResponse getWishlist() {
        User user = getCurrentUser();
        Wishlist wishlist = getOrCreateWishlist(user);
        return mapToResponse(wishlist);
    }

    // ADD PRODUCT TO WISHLIST
    @Transactional
    public WishlistResponse addToWishlist(Long productId) {
        User user = getCurrentUser();
        Wishlist wishlist = getOrCreateWishlist(user);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        boolean alreadyExists = wishlistItemRepository.existsByWishlistIdAndProductId(wishlist.getId(), productId);
        if (alreadyExists) {
            throw new IllegalArgumentException("Product is already in your wishlist.");
        }

        WishlistItem item = WishlistItem.builder()
                .wishlist(wishlist)
                .product(product)
                .build();

        item = wishlistItemRepository.save(item);
        wishlist.getItems().add(item);
        Wishlist saved = wishlistRepository.save(wishlist);
        return mapToResponse(saved);
    }

    // REMOVE PRODUCT FROM WISHLIST
    @Transactional
    public WishlistResponse removeFromWishlist(Long productId) {
        User user = getCurrentUser();
        Wishlist wishlist = getOrCreateWishlist(user);

        wishlistItemRepository.deleteByWishlistIdAndProductId(wishlist.getId(), productId);
        wishlist.getItems().removeIf(item -> item.getProduct() != null && item.getProduct().getId().equals(productId));
        Wishlist saved = wishlistRepository.save(wishlist);
        return mapToResponse(saved);
    }

    // MOVE PRODUCT FROM WISHLIST TO CART
    @Transactional
    public WishlistResponse moveToCart(Long productId) {
        User user = getCurrentUser();
        Wishlist wishlist = getOrCreateWishlist(user);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        if (product.getStockQuantity() != null && product.getStockQuantity() <= 0) {
            throw new IllegalArgumentException("Product is out of stock.");
        }

        // 1. Add to cart
        cartService.addToCart(productId, 1);

        // 2. Remove from wishlist
        wishlistItemRepository.deleteByWishlistIdAndProductId(wishlist.getId(), productId);
        wishlist.getItems().removeIf(item -> item.getProduct() != null && item.getProduct().getId().equals(productId));

        Wishlist saved = wishlistRepository.save(wishlist);
        return mapToResponse(saved);
    }

    private WishlistResponse mapToResponse(Wishlist wishlist) {
        List<WishlistItemResponse> itemResponses = wishlist.getItems() != null
                ? wishlist.getItems().stream().map(item -> {
                    Product p = item.getProduct();
                    return WishlistItemResponse.builder()
                            .id(item.getId())
                            .productId(p.getId())
                            .productName(p.getName())
                            .productDescription(p.getDescription())
                            .productPrice(p.getPrice() != null ? p.getPrice() : 0.0)
                            .stockQuantity(p.getStockQuantity() != null ? p.getStockQuantity() : 0)
                            .imageUrl(p.getImageUrl())
                            .categoryName(p.getCategory() != null ? p.getCategory().getName() : null)
                            .build();
                }).collect(Collectors.toList())
                : new ArrayList<>();

        return WishlistResponse.builder()
                .id(wishlist.getId())
                .userId(wishlist.getUser().getId())
                .items(itemResponses)
                .build();
    }
}
