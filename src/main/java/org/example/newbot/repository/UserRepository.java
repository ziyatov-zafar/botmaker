package org.example.newbot.repository;

import org.example.newbot.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAllByOrderByIdDesc() ;
    User findByChatId(Long chatId);

    List<User> findAllByRoleOrderByIdDesc(String role);
    Page<User>findAllByOrderByIdDesc(Pageable pageable);
    Page<User> findByUsernameContainingIgnoreCaseOrderByIdDesc(String username, Pageable pageable);
    Page<User> findByNicknameContainingIgnoreCaseOrderByIdDesc(String nickname, Pageable pageable);
    Page<User> findAllByRoleOrderByIdAsc(String role, Pageable pageable);

}
