package org.example.newbot.service;

import org.example.newbot.dto.ResponseDto;
import org.example.newbot.model.Product;

import java.util.List;

public interface ProductService {
    ResponseDto<Product> findByNameUz(String nameUz, Long categoryId);

    ResponseDto<Product> findByNameRu(String nameRu, Long categoryId);

    ResponseDto<Product> draftProduct(Long categoryId);

    ResponseDto<List<Product>> findAllByCategoryId(Long categoryId);
    ResponseDto<Void>save(Product product);
    ResponseDto<Void>add(Long categoryId , Product product , String lang);
    ResponseDto<Product>findById(Long productId);
}
