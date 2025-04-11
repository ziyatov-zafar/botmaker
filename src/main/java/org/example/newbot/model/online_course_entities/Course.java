package org.example.newbot.model.online_course_entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Long botId;//qaysi botga tegishliligi
    private String groupLink;
    private Boolean hasGroup;
    private Boolean active;
    private String status;
    @Column(columnDefinition = "TEXT")
    private String teacherUrl;
    private Boolean hasTeacher;
    private Double price;

}
