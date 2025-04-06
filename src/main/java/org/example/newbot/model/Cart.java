package org.example.newbot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long botId;
    private Long userId;
    private String status;
    private String type;
    private Boolean active;
    private String address;
    private Double lat;
    private Double lon;
    private String paymentTypeUz;
    private String paymentTypeRu;
    private String phone;
    private String deliveryType;
    private Integer branchId;
}
