package com.ecommerce.repository;

import com.ecommerce.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {

    Optional<WishlistItem> findByWishlistIdAndProductId(Long wishlistId, Long productId);

    boolean existsByWishlistIdAndProductId(Long wishlistId, Long productId);

    @Modifying
    void deleteByProductId(Long productId);

    @Modifying
    void deleteByWishlistIdAndProductId(Long wishlistId, Long productId);
}
