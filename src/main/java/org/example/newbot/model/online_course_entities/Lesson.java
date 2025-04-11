package org.example.newbot.model.online_course_entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;
    private Long courseId;
    private String videoUrl;
    @Column(columnDefinition = "TEXT")
    private String homework;
    private Boolean hasHomework;
    private String status;
    private Boolean active;
    private Boolean free;
}
