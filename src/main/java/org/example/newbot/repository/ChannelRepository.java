package org.example.newbot.repository;

import org.example.newbot.model.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChannelRepository extends JpaRepository<Channel, Integer> {
    List<Channel> findAllByActiveIsTrueAndStatusOrderByIdAsc(String status);

    Channel findByNameAndActiveIsTrue(String name);
    @Query("select b from Channel  b where b.status='draft'")
    Channel getDraft();
}
