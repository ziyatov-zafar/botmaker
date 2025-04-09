package org.example.newbot.repository;

import org.example.newbot.model.BotPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BotPriceRepository extends JpaRepository<BotPrice, Long> {
    List<BotPrice> findAllByActiveIsTrueAndStatus(String status);
    BotPrice findByTypeText(String typeText);
    BotPrice findByType(String type);
}
