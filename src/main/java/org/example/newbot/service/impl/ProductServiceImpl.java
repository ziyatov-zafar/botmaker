package org.example.newbot.service.impl;

import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.example.newbot.dto.ResponseDto;
import org.example.newbot.model.Product;
import org.example.newbot.repository.CategoryRepository;
import org.example.newbot.repository.ProductRepository;
import org.example.newbot.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ResponseDto<Void> save(Product product) {
        try {
            productRepository.save(product);
            return new ResponseDto<>(true, "Ok");
        } catch (Exception e) {
            return exception(e);
        }
    }

    @Override
    public ResponseDto<Product> findByNameUz(String nameUz, Long categoryId) {
        try {
            Product product = productRepository.findByNameUzAndCategoryIdAndActiveTrue(nameUz, categoryId);
            if (product == null) {
                return new ResponseDto<>(false, "Not found");
            }
            return new ResponseDto<>(true, "Ok", product);
        } catch (Exception e) {
            return exception(e);
        }
    }

    @Override
    public ResponseDto<Product> findByNameRu(String nameRu, Long categoryId) {
        try {
            Product product = productRepository.findByNameRuAndCategoryIdAndActiveTrue(nameRu, categoryId);
            if (product == null) {
                return new ResponseDto<>(false, "Not found");
            }
            return new ResponseDto<>(true, "Ok", product);
        } catch (Exception e) {
            return exception(e);
        }
    }

    @Override
    public ResponseDto<Product> draftProduct(Long categoryId) {
        try {
            Product draft = productRepository.findByStatusAndCategoryId("draft", categoryId);
            if (draft == null) {
                return new ResponseDto<>(false, "Not found");
            }
            return new ResponseDto<>(true, "Ok", draft);
        } catch (Exception e) {
            return exception(e);
        }
    }

    @Override
    public ResponseDto<Product> findById(Long productId) {
        try {
            Optional<Product> byId = productRepository.findById(productId);
            return byId.map(product -> new ResponseDto<>(true, "ok", product)).orElseGet(() -> new ResponseDto<>(false, "Not found"));
        } catch (Exception e) {
            return exception(e);
        }
    }

    @Override
    public ResponseDto<Void> add(Long categoryId, Product product, String lang) {
        try {
            if (lang.equals("uz")) {
                ResponseDto<Product> nameUz = findByNameUz(product.getNameUz(), categoryId);
                if (nameUz.isSuccess()) {
                    return new ResponseDto<>(false, "Duplicate product");
                }
                productRepository.save(product);
                return new ResponseDto<>(true, "Ok");
            } else if (lang.equals("ru")) {
                ResponseDto<Product> nameRu = findByNameRu(product.getNameRu(), categoryId);
                if (nameRu.isSuccess()) {
                    return new ResponseDto<>(false, "Duplicate product");
                }
                productRepository.save(product);
                return new ResponseDto<>(true, "Ok");
            } else {
                return save(product);
            }
        } catch (Exception e) {
            return exception(e);
        }
    }

    @Override
    public ResponseDto<List<Product>> findAllByCategoryId(Long categoryId) {
        try {
            return new ResponseDto<>(true, "Ok",
                    productRepository.findAllByCategoryIdAndActiveIsTrueAndStatusOrderByIdAsc(categoryId, "open")
            );
        } catch (Exception e) {
            return exception(e);
        }
    }

    private <T> ResponseDto<T> exception(Exception e) {
        log.error(e);
        return new ResponseDto<>(false, e.getMessage());
    }
}
