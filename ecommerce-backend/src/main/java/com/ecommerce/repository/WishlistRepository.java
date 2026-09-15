package com.ecommerce.repository;

import com.ecommerce.entity.User;
import com.ecommerce.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    Optional<Wishlist> findByUser(User user);

    Optional<Wishlist> findByUserId(Long userId);
}
