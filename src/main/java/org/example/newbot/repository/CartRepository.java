package org.example.newbot.repository;

import org.example.newbot.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByStatusAndUserIdAndTypeAndActiveIsTrue(String status, Long userId, String type);
    List<Cart> findByStatusAndUserIdAndActiveIsTrue(String status, Long userId);
    List<Cart> findAllByStatusAndBotIdAndActiveIsTrueOrderByIdAsc(String status, Long botId);
}
