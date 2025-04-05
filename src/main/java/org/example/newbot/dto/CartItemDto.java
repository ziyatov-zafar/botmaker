package org.example.newbot.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemDto {
    private Long id;
    private String categoryNameUz;
    private String categoryNameRu;
    private String productNameUz;
    private String productNameRu;
    private String productVariantNameUz ;
    private String productVariantNameRu ;
    private Double price ;
    private Double totalPrice ;
    private Integer quantity ;
}
