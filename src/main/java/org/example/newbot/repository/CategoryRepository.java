package org.example.newbot.repository;

import org.example.newbot.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByBotIdAndStatusAndActiveOrderById(Long botId, String status, Boolean active);

    Category findByNameUzAndBotId(String nameUz , Long botId);

    Category findByNameRuAndBotId(String nameRu , Long botId);

    Category findByStatusAndBotId(String status , Long botId);
    Category findByIdAndBotId(Long id , Long botId);

    Category findByNameRu(String nameRu);
}
