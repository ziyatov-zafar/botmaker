package org.example.newbot.repository;

import org.example.newbot.model.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    List<ProductVariant> findAllByProductIdAndActiveIsTrueAndStatusOrderByIdAsc(Long productId, String status);
    ProductVariant findByStatusAndProductId(String status, Long productId);
}
