package org.example.newbot.repository;

import org.example.newbot.model.BotUser;
import org.example.newbot.model.online_course_entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findAllByActiveAndStatusAndBotIdOrderById(Boolean active, String status, Long botId);

    @Query("select c from Course c WHERE c.status='draft' AND c.botId=:botId")
    Course draftCourse(@Param("botId") Long botId);

    @Query("select c from Course c where c.name=:name and c.botId=:botId")
    Course findCourse(@Param("name") String name, @Param("botId") Long botId);

    @Query("select c from Course c where c.id=:courseId")
    Course findCourse(@Param("courseId") Long courseId);
}
