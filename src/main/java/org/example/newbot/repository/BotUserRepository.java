package org.example.newbot.repository;

import org.example.newbot.model.BotUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BotUserRepository extends JpaRepository<BotUser, Long> {
    @Query("SELECT u FROM BotUser u JOIN u.bots b WHERE b.id = :botId ORDER BY u.id DESC")
    List<BotUser> findUsersByBotId(@Param("botId") Long botId);

    @Query("SELECT u FROM BotUser u JOIN u.bots b WHERE b.id = :botId " +
            "AND LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%')) " +
            "ORDER BY u.id DESC")
    Page<BotUser> findByBotIdAndUsername(
            @Param("botId") Long botId,
            @Param("username") String username,
            Pageable pageable);

    @Query("SELECT u FROM BotUser u JOIN u.bots b WHERE b.id = :botId " +
            "AND LOWER(u.nickname) LIKE LOWER(CONCAT('%', :nickname, '%')) " +
            "ORDER BY u.id DESC")
    Page<BotUser> findByBotIdAndNickname(
            @Param("botId") Long botId,
            @Param("nickname") String nickname,
            Pageable pageable);

    @Query("SELECT u FROM BotUser u JOIN u.bots b WHERE b.id = :botId " +
            "AND (LOWER(u.phone) LIKE LOWER(CONCAT('%', :phone, '%')) " +
            "OR LOWER(u.helperPhone) LIKE LOWER(CONCAT('%', :phone, '%'))) " +
            "ORDER BY u.id DESC")
    Page<BotUser> findByBotIdAndPhone(
            @Param("botId") Long botId,
            @Param("phone") String phone,
            Pageable pageable);

    @Query("SELECT u FROM BotUser u JOIN u.bots b WHERE b.id = :botId ORDER BY u.id DESC")
    Page<BotUser> findUsersByBotId(@Param("botId") Long botId, Pageable pageable);

    @Query("SELECT u FROM BotUser u JOIN u.bots b WHERE b.id = :botId AND u.role=:role ORDER BY u.id DESC")
    Page<BotUser> findUsersByBotIdAndRole(@Param("botId") Long botId, @Param("role") String role, Pageable pageable);

    @Query("SELECT u FROM BotUser u JOIN FETCH u.bots b WHERE u.chatId = :chatId AND b.id = :botId")
    Optional<BotUser> findUserInBot(@Param("botId") Long botId, @Param("chatId") Long chatId);

    @EntityGraph(attributePaths = {"bots"})
    @Query("SELECT u FROM BotUser u WHERE u.id = :id AND EXISTS " +
            "(SELECT 1 FROM u.bots b WHERE b.id = :botId)")
    Optional<BotUser> findByUserIdAndBotId(@Param("botId") Long botId, @Param("id") Long id);


    @Query("SELECT u FROM BotUser u JOIN u.bots b WHERE b.id = :botId " +
            "AND (LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(u.phone) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(u.helperPhone) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "ORDER BY u.id DESC")
    Page<BotUser> searchUsers(
            @Param("botId") Long botId,
            @Param("query") String query,
            Pageable pageable);
}
