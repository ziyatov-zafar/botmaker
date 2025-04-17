package org.example.newbot.model.online_course_entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class CourseCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String status;//open//draft//finish
    private Long courseId;
    private Long userId;
    private String courseName;
    private String courseDescription;
    private Boolean free;

}
