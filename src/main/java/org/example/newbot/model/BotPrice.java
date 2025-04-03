package org.example.newbot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class BotPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double price;
    @Column(unique = true , name = "typee")
    private String type;
    @Column(unique = true)
    private String typeText;
    private String description;
    private Boolean active;
    private String status;
}
