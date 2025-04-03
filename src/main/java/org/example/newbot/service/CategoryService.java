package org.example.newbot.service;

import org.example.newbot.dto.ResponseDto;
import org.example.newbot.model.Category;

import java.util.List;

public interface CategoryService {
    ResponseDto<Void> save(Long botId, Category category, String type);

    ResponseDto<Void> save(Category category);

    ResponseDto<List<Category>> findAllByBotId(Long botId);

    ResponseDto<Category> draftCategory(Long botId);

    ResponseDto<Category> findByNameUz(Long botId, String nameUz);

    ResponseDto<Category> findByNameRu(Long botId, String nameRu);
    ResponseDto<Category> findById(Long id);


}
