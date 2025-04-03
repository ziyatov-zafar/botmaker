package org.example.newbot.repository;

import org.example.newbot.model.BotInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BotInfoRepository extends JpaRepository<BotInfo, Long> {
    List<BotInfo> findAllByActiveIsTrueOrderByIdDesc();
    BotInfo findByType(String type);
}
