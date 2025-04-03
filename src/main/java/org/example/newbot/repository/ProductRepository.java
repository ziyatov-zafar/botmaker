package org.example.newbot.repository;

import org.example.newbot.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByNameUzAndCategoryIdAndActiveTrue(String nameUz, Long categoryId);

    Product findByNameRuAndCategoryIdAndActiveTrue(String nameRu, Long categoryId);

    List<Product> findAllByCategoryIdAndActiveIsTrueAndStatusOrderByIdAsc(Long categoryId, String status);
    Product findByStatusAndCategoryId(String status, Long categoryId);
}
