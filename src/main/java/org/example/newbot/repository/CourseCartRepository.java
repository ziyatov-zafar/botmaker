package org.example.newbot.repository;

import org.example.newbot.model.online_course_entities.CourseCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseCartRepository extends JpaRepository<CourseCart, Long> {
    @Query("select c from CourseCart c where c.id=:id")
    CourseCart getCourseCart(@Param("id") Long id);

    @Query("select c from CourseCart c where c.userId=:userId")
    List<CourseCart> getCourseCartsByUserId(@Param("userId") Long userId);

    @Query("select c from CourseCart c where c.courseId=:courseId")
    List<CourseCart> getCourseCartsByCourseId(@Param("courseId") Long courseId);
}