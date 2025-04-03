package org.example.newbot.service.impl;

import lombok.extern.log4j.Log4j2;
import org.example.newbot.dto.ResponseDto;
import org.example.newbot.model.Category;
import org.example.newbot.repository.CategoryRepository;
import org.example.newbot.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class CategoryServiceImpl implements CategoryService {
    @Override
    public ResponseDto<List<Category>> findAllByBotId(Long botId) {
        try {
            return new ResponseDto<>(true, "Ok",
                    categoryRepository.findAllByBotIdAndStatusAndActiveOrderById(botId, "open", true)
            );
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<Category> draftCategory(Long botId) {
        try {
            Category category = categoryRepository.findByStatusAndBotId("draft", botId);
            if (category == null) {
                return new ResponseDto<>(false, "Not found category");
            }
            return new ResponseDto<>(true, "Ok", category);
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<Category> findByNameUz(Long botId, String nameUz) {
        try {
            Category category = categoryRepository.findByNameUzAndBotId(nameUz, botId);
            if (category == null) {
                return new ResponseDto<>(false, "Not found category");
            }
            return new ResponseDto<>(true, "Ok", category);
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<Category> findById(Long id) {
        try {
            Optional<Category>cOp = categoryRepository.findById(id);
            return cOp.map(category -> new ResponseDto<>(true, "Ok", category)).orElseGet(() -> new ResponseDto<>(false, "Not found category"));
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<Category> findByNameRu(Long botId, String nameRu) {
        try {
            Category category = categoryRepository.findByNameRuAndBotId(nameRu, botId);
            if (category == null) {
                return new ResponseDto<>(false, "Not found category");
            }
            return new ResponseDto<>(true, "Ok", category);
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    private final CategoryRepository categoryRepository;

    @Override
    public ResponseDto<Void> save(Long botId, Category category, String type) {
        try {
            Category nameUz = categoryRepository.findByNameUzAndBotId(category.getNameUz(), botId);
            Category nameRu = categoryRepository.findByNameRuAndBotId(category.getNameRu(), botId);
            if ((nameUz != null && type.equals("uz")) || (nameRu != null && type.equals("ru"))) {
                return new ResponseDto<>(false, "Duplicate category");
            }
            categoryRepository.save(category);
            return new ResponseDto<>(true, "Ok");
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<Void> save(Category category) {
        try {
            categoryRepository.save(category);
            return new ResponseDto<>(true, "Ok");
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }


    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
}
