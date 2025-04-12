package org.example.newbot.repository;

import org.example.newbot.model.online_course_entities.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    @Query("select l from Lesson l where l.courseId=:courseId and l.status='draft'")
    Lesson draftLesson(@Param("courseId") Long courseId);
    @Query("select l from Lesson l where l.courseId=:courseId and l.name=:name")
    Lesson findLessonFromCourse(@Param("name") String name, @Param("courseId") Long courseId);
    @Query("select l from Lesson l where l.active=true and l.status='open' and l.courseId=:courseId order by l.id")
    List<Lesson> getLessonsOfCourse(@Param("courseId") Long courseId);
}
