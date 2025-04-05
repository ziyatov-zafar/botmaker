package org.example.newbot.repository;

import org.example.newbot.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findAllByActiveIsTrueAndCartId(Long cartId);
}
