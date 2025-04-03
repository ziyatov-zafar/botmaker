package org.example.newbot.service.impl;

import lombok.extern.log4j.Log4j2;
import org.example.newbot.dto.ResponseDto;
import org.example.newbot.model.Product;
import org.example.newbot.model.ProductVariant;
import org.example.newbot.repository.ProductVariantRepository;
import org.example.newbot.service.ProductVariantService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class ProductVariantServiceImpl implements ProductVariantService {
    private final ProductVariantRepository productVariantRepository;

    public ProductVariantServiceImpl(ProductVariantRepository productVariantRepository) {
        this.productVariantRepository = productVariantRepository;
    }

    @Override
    public ResponseDto<List<ProductVariant>> findAllByProductId(Long productId) {
        try {
            return new ResponseDto<>(
                    true, "Ok",
                    productVariantRepository.findAllByProductIdAndActiveIsTrueAndStatusOrderByIdAsc(
                            productId, "open"
                    )
            );
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<ProductVariant> findById(Long productTypeId) {
        try {
            Optional<ProductVariant> o = productVariantRepository.findById(
                    productTypeId
            );
            return o.map(productVariant -> new ResponseDto<>(true, "Ok", productVariant)).orElseGet(() -> new ResponseDto<>(false, "Not found"));
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false , e.getMessage());
        }
    }

    @Override
    public ResponseDto<Void> save(ProductVariant product) {
        try {
            productVariantRepository.save(product);
            return new ResponseDto<>(true, "Ok");
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<ProductVariant> draftProductVariant(Long productId) {
        try {
            ProductVariant draft = productVariantRepository.findByStatusAndProductId("draft", productId);
            if (draft == null) {
                return new ResponseDto<>(false, "Draft");
            }
            return new ResponseDto<>(true, "Ok", draft);
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }
}
