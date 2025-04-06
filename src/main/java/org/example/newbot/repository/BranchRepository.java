package org.example.newbot.repository;


import org.example.newbot.model.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BranchRepository extends JpaRepository<Branch, Integer> {
    Branch findByStatus(String status);

    @Query("select b from Branch b where b.status = 'draft'")
    Branch draftBranch();
//    Branch findByNameAndActiveIsTrue(String name);

    List<Branch> findAllByActiveIsTrueAndStatusAndBotIdOrderByIdAsc(String status, Long botId);
    List<Branch> findAll();

    Branch findByNameAndBotIdAndActiveIsTrue(String name,Long botId);
}
