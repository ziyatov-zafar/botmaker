package org.example.newbot.service;

import org.example.newbot.dto.ResponseDto;
import org.example.newbot.model.Product;
import org.example.newbot.model.ProductVariant;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;

public interface ProductVariantService {
    ResponseDto<List<ProductVariant>>findAllByProductId(Long productId);
    ResponseDto<ProductVariant>draftProductVariant(Long productId);

    ResponseDto<Void> save(ProductVariant productVariant);

    ResponseDto<ProductVariant> findById(Long productTypeId);
}
