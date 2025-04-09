package org.example.newbot.repository;

import org.example.newbot.model.BotInfo;
import org.example.newbot.model.BotUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BotInfoRepository extends JpaRepository<BotInfo, Long> {
    Page<BotInfo> findAllByActiveOrderByIdDesc(Boolean active,Pageable pageable);
    List<BotInfo> findAllByActiveIsTrueOrderByIdDesc();
    BotInfo findByType(String type);
    BotInfo findByBotTokenAndActiveIsTrue(String token);
    @Query("SELECT b.users FROM BotInfo b WHERE b.id = :botId")
    List<BotUser> findAllUsersByBotId(@Param("botId") Long botId);
    @Query("SELECT b.users FROM BotInfo b WHERE b.botUsername = :username")
    Page<BotUser> findUsersByBotUsername(@Param("username") String username, Pageable pageable);
    @Query("SELECT b FROM BotInfo b WHERE " +
            "(LOWER(b.botUsername) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(b.botToken) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(b.type) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "ORDER BY b.id DESC")
    Page<BotInfo> searchBot(@Param("query") String query, Pageable pageable);

    List<BotInfo>findAllByActiveIsTrueOrderByIdAsc();
}
