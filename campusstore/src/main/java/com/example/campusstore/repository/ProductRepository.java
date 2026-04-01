package com.example.campusstore.repository;

import com.example.campusstore.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.isActive = true " +
           "AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
           "AND (:inStock IS NULL OR (:inStock = true AND p.stockQty > 0) OR (:inStock = false AND p.stockQty = 0))")
    Page<Product> searchActive(
        @Param("name") String name,
        @Param("categoryId") Long categoryId,
        @Param("inStock") Boolean inStock,
        Pageable pageable
    );
}
