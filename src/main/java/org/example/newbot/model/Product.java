package org.example.newbot.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "producttlar")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nameUz;
    @Column(columnDefinition = "TEXT")
    private String descriptionUz;
    private String nameRu;
    @Column(columnDefinition = "TEXT")
    private String descriptionRu;
    private String status;
    private Boolean active;
    private Long categoryId;
}
