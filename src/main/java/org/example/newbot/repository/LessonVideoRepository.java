package org.example.newbot.repository;

import org.example.newbot.model.online_course_entities.Lesson;
import org.example.newbot.model.online_course_entities.LessonVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LessonVideoRepository extends JpaRepository<LessonVideo, Long> {
    @Query("select lv from LessonVideo lv where lv.lessonId=:lessonId and lv.active=true order by lv.id")
    List<LessonVideo> findByLessonId(@Param("lessonId") Long lessonId);
}
