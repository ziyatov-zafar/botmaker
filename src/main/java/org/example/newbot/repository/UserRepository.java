package org.example.newbot.repository;

import org.example.newbot.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAllByOrderByIdDesc();

    User findByChatId(Long chatId);

    List<User> findAllByRoleOrderByIdDesc(String role);

    Page<User> findAllByOrderByIdDesc(Pageable pageable);

    Page<User> findByUsernameContainingIgnoreCaseOrderByIdDesc(String username, Pageable pageable);

    Page<User> findByNicknameContainingIgnoreCaseOrderByIdDesc(String nickname, Pageable pageable);

    Page<User> findAllByRoleOrderByIdAsc(String role, Pageable pageable);

    @Query("SELECT b FROM User b WHERE " +
            "(LOWER(b.firstname) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(b.lastname) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(b.username) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(b.phone) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(b.helperPhone) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(b.nickname) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "ORDER BY b.id DESC")
    Page<User> searchUser(String query, Pageable pageable);
}
