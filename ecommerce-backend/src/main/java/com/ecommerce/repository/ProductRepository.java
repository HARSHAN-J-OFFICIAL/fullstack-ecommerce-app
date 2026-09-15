package com.ecommerce.repository;

import com.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByNameContainingIgnoreCase(
            String keyword,
            Pageable pageable
    );

    Page<Product> findByCategoryId(
            Long categoryId,
            Pageable pageable
    );

    boolean existsByCategoryId(Long categoryId);

    boolean existsByNameIgnoreCase(String name);

    java.util.List<Product> findByStockQuantityGreaterThanAndStockQuantityLessThan(int min, int max);

    long countByStockQuantityLessThanEqual(int max);
}
