package org.example.newbot.bot.online_course_bot.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.newbot.bot.Status;
import org.example.newbot.model.BotInfo;
import org.example.newbot.model.BotUser;
import org.example.newbot.model.online_course_entities.Course;
import org.example.newbot.model.online_course_entities.Lesson;
import org.example.newbot.model.online_course_entities.LessonVideo;
import org.example.newbot.repository.BotInfoRepository;
import org.example.newbot.repository.CourseRepository;
import org.example.newbot.repository.LessonRepository;
import org.example.newbot.repository.LessonVideoRepository;
import org.example.newbot.service.BotUserService;
import org.example.newbot.service.DynamicBotService;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Video;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.example.newbot.bot.online_course_bot.OnlineCourseConstVariables.*;

@Log4j2
@RequiredArgsConstructor
public class AdminFunction {
    private final BotInfoRepository botInfoRepository;
    private final BotUserService botUserService;
    private final DynamicBotService bot;
    private final AdminKyb kyb;
    private final AdminMsg msg;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final LessonVideoRepository lessonVideoRepository;

    public void start(BotInfo botInfo, BotUser user) {
        bot.sendMessage(botInfo.getId(), user.getChatId(), msg.menu, kyb.menu);
        eventCode(user, "menu");
    }

    private void eventCode(BotUser user, String eventCode) {
        user.setEventCode(eventCode);
        botUserService.save(user);
    }

    public void menu(BotInfo botInfo, BotUser user, String text) {
        if (text.equals(menu[0])) {
            List<Course> courses = courseRepository.findAllByActiveAndStatusAndBotIdOrderById(true, Status.OPEN, botInfo.getId());
            ReplyKeyboardMarkup markup = kyb.courseList(courses, courses.isEmpty());
            if (courses.isEmpty()) {
                bot.sendMessage(botInfo.getId(), user.getChatId(), msg.emptyCourse, markup);
            } else {
                bot.sendMessage(botInfo.getId(), user.getChatId(), msg.courseList, markup);
            }
            eventCode(user, "course menu");
        }
    }

    public void courseMenu(BotInfo botInfo, BotUser user, String text) {
        Course course;
        if (text.equals(backBtn)) start(botInfo, user);
        else if (text.equals(addCourse)) {
            bot.sendMessage(botInfo.getId(), user.getChatId(), msg.getNewCourseName, kyb.addBackAndMainBtn(new ArrayList<>()));
            eventCode(user, "addCourseName");
        } else {
            course = courseRepository.findCourse(text, botInfo.getId());
            if (course == null) {
                List<Course> courses = courseRepository.findAllByActiveAndStatusAndBotIdOrderById(true, Status.OPEN, botInfo.getId());
                bot.sendMessage(botInfo.getId(), user.getChatId(), msg.wrongBtn, kyb.courseList(courses, courses.isEmpty()));

                return;
            }
            user.setCourseId(course.getId());
            botUserService.save(user);
            bot.sendMessage(botInfo.getId(), user.getChatId(), msg.aboutCourse(course), kyb.courseCrud());
            eventCode(user, "about course");
        }
    }

    public void addCourse(BotInfo botInfo, BotUser user, String text, String eventCode) {
        if (text.equals(backBtn)) menu(botInfo, user, menu[0]);
        else {
            Course course = courseRepository.draftCourse(botInfo.getId());
            if (course == null) {
                course = new Course();
                course.setActive(false);
                course.setBotId(botInfo.getId());
                course.setStatus(Status.DRAFT);
                courseRepository.save(course);
                course = courseRepository.draftCourse(botInfo.getId());
            }
            switch (eventCode) {
                case "addCourseName" -> {
                    boolean isAddCourse = courseRepository.findCourse(text, botInfo.getId()) == null;
                    if (isAddCourse) {
                        course.setName(text);
                        try {
                            courseRepository.save(course);
                            bot.sendMessage(botInfo.getId(), user.getChatId(), msg.successAddCourseName);
                            eventCode(user, "addNewCourseDescription");
                        } catch (Exception e) {
                            bot.sendMessage(botInfo.getId(), user.getChatId(), msg.unexpectedErrorForAddNewCourseName);
                        }
                    } else {
                        bot.sendMessage(botInfo.getId(), user.getChatId(), msg.busyCourseName);
                    }
                }
                case "addNewCourseDescription" -> {
                    course.setDescription(text);
                    courseRepository.save(course);
                    bot.sendMessage(botInfo.getId(), user.getChatId(), msg.coursePrice);
                    eventCode(user, "addNewCoursePrice");
                }
                case "addNewCoursePrice" -> {
                    try {
                        course.setPrice(Double.valueOf(text));
                        courseRepository.save(course);
                        bot.sendMessage(botInfo.getId(), user.getChatId(), msg.addCourseGroupLink, kyb.leaveBtn);
                        eventCode(user, "addCourseNewGroupLink");
                    } catch (Exception e) {
                        bot.sendMessage(botInfo.getId(), user.getChatId(), msg.numberFormatErrorForCoursePrice);
                    }
                }
                case "addCourseNewGroupLink" -> {
                    String msgToUser;
                    if (text.equals(leaveBtn)) {
                        course.setHasGroup(false);
                        msgToUser = msg.leaveBtnClickForGroupLink;
                    } else {
                        course.setHasGroup(true);
                        course.setGroupLink(text);
                        msgToUser = msg.addCourseAddedGroupLink;
                    }
                    courseRepository.save(course);
                    bot.sendMessage(botInfo.getId(), user.getChatId(), msgToUser, kyb.leaveBtn);
                    eventCode(user, "addCourseNewTeacherLink");
                }
                case "addCourseNewTeacherLink" -> {
                    String msgToUser;
                    if (text.equals(leaveBtn)) {
                        course.setHasTeacher(false);
                        msgToUser = msg.leaveBtnClickForTeacherLink(course);
                    } else {
                        course.setHasTeacher(true);
                        course.setTeacherUrl(text);
                        msgToUser = msg.addCourseAddedTeacherLink(course);
                    }
                    courseRepository.save(course);
                    bot.sendMessage(botInfo.getId(), user.getChatId(), msgToUser, kyb.isSuccess("uz"));
                    eventCode(user, "isAddCourse");
                }
                case "isAddCourse" -> {
                    if (text.equals("✅ Ha")) {
                        course.setActive(true);
                        course.setStatus(Status.OPEN);
                        courseRepository.save(course);
                        bot.sendMessage(botInfo.getId(), user.getChatId(), msg.successAddCourse);
                        menu(botInfo, user, menu[0]);
                    } else if (text.equals("❌ Yo'q")) {
                        course.setStatus(Status.OPEN);
                        course.setName(UUID.randomUUID().toString() + course.getId());
                        courseRepository.save(course);
                        bot.sendMessage(botInfo.getId(), user.getChatId(), msg.failedAddCourse);
                        menu(botInfo, user, menu[0]);
                    }
                }
            }
        }
    }

    public void aboutCourse(BotInfo botInfo, BotUser user, String text) {
        Course course = courseRepository.findCourse(user.getCourseId());
        switch (text) {
            case editCourse -> {
                bot.sendMessage(botInfo.getId(), user.getChatId(), msg.editCourseMsg(course), kyb.editCourse(course.getHasGroup(), course.getHasTeacher()));
                eventCode(user, "editCourse");
            }
            case addCourse -> courseMenu(botInfo, user, addCourse);
            case deleteCourse ->
                    bot.sendMessage(botInfo.getId(), user.getChatId(), msg.isDeleteMsg(course), kyb.isSuccessDelete);
            case confirm -> {
                course.setActive(false);
                course.setName(UUID.randomUUID().toString() + course.getId());
                courseRepository.save(course);
                bot.sendMessage(botInfo.getId(), user.getChatId(), msg.confirmDelete);
                menu(botInfo, user, menu[0]);
            }

            case backBtn -> menu(botInfo, user, menu[0]);
            case cancel -> bot.sendMessage(botInfo.getId(), user.getChatId(), msg.cancelOperation, kyb.courseCrud());
            case viewCourseLessons -> {
                List<Lesson> lessons = lessonRepository.getLessonsOfCourse(course.getId());
                String msgToUser;
                if (lessons.isEmpty()) {
                    msgToUser = msg.emptyLessonOfCourse(course);
                } else {
                    msgToUser = msg.getLessonsOfCourse(course);
                }
                bot.sendMessage(botInfo.getId(), user.getChatId(), msgToUser, kyb.setLessons(lessons, lessons.isEmpty()));
                eventCode(user, "viewCourseLessons");
            }
            default -> bot.sendMessage(botInfo.getId(), user.getChatId(), msg.wrongBtn, kyb.courseCrud());
        }
    }

    public void editCourse(BotInfo botInfo, BotUser user, String text) {
        Course course = courseRepository.findCourse(user.getCourseId());

        if (text.equals(editCourseBtn[0])) {
            bot.sendMessage(botInfo.getId(), user.getChatId(), msg.getCourseNameForEdit(course), kyb.toBack);
            eventCode(user, "editCourseName");
        } else if (text.equals(editCourseBtn[1])) {
            bot.sendMessage(botInfo.getId(), user.getChatId(), msg.getCourseDescriptionForEdit(course), kyb.toBack);
            eventCode(user, "editCourseDescription");
        } else if (text.equals(editCourseBtn[2])) {
            bot.sendMessage(botInfo.getId(), user.getChatId(), msg.getCoursePriceForEdit(course), kyb.toBack);
            eventCode(user, "editCoursePrice");
        } else if (text.equals(editCourseBtn[3]) || text.equals(editCourseBtn[4])) {
            bot.sendMessage(botInfo.getId(), user.getChatId(), msg.getCourseGroupForEdit(course, text.equals(editCourseBtn[3])), kyb.toBack);
            eventCode(user, "editCourseGroup");
        } else if (text.equals(editCourseBtn[5])) {
            course.setHasGroup(false);
            courseRepository.save(course);
            bot.sendMessage(botInfo.getId(), user.getChatId(), msg.removeGroupFromCourse);
            eventCode(user, "about course");
            aboutCourse(botInfo, user, editCourse);
        } else if (text.equals(editCourseBtn[6]) || text.equals(editCourseBtn[7])) {
            bot.sendMessage(botInfo.getId(), user.getChatId(), msg.getCourseTeacherForEdit(course, text.equals(editCourseBtn[6])), kyb.toBack);
            eventCode(user, "editCourseTeacher");
        } else if (text.equals(editCourseBtn[8])) {
            course.setHasTeacher(false);
            courseRepository.save(course);
            bot.sendMessage(botInfo.getId(), user.getChatId(), msg.removeTeacherFromCourse);
            eventCode(user, "about course");
            aboutCourse(botInfo, user, editCourse);
        } else if (text.equals(backBtn)) {
            courseMenu(botInfo, user, course.getName());
        } else {
            bot.sendMessage(botInfo.getId(), user.getChatId(), msg.wrongBtn, kyb.editCourse(course.getHasGroup(), course.getHasTeacher()));
        }

    }

    public void editCourseInformation(BotInfo botInfo, BotUser user, String text) {
        Course course = courseRepository.findCourse(user.getCourseId());
        if (text.equals(toBackBtn)) {
            eventCode(user, "about course");
            aboutCourse(botInfo, user, editCourse);
        } else {
            String eventCode = user.getEventCode(), msgToAdmin;
            ReplyKeyboardMarkup markup = null;
            switch (eventCode) {
                case "editCourseName" -> {
                    Course checkCourse = courseRepository.findCourse(text, botInfo.getId());
                    if (checkCourse != null) {
                        msgToAdmin = msg.busyCourseName;
                        markup = kyb.toBack;
                    } else {
                        course.setName(text);
                        courseRepository.save(course);
                        msgToAdmin = msg.editedCourseName;
                    }
                }
                case "editCourseDescription" -> {
                    course.setDescription(text);
                    courseRepository.save(course);
                    msgToAdmin = msg.editedCourseDescription;
                }
                case "editCoursePrice" -> {
                    try {
                        course.setPrice(Double.valueOf(text));
                        courseRepository.save(course);
                        msgToAdmin = msg.editedCourseDescription;
                    } catch (Exception e) {
                        msgToAdmin = msg.numberFormatErrorForCoursePrice;
                        markup = kyb.toBack;
                    }
                }
                case "editCourseGroup" -> {
                    course.setGroupLink(text);
                    course.setHasGroup(true);
                    courseRepository.save(course);
                    msgToAdmin = msg.editedCourseGroup;
                }
                case "editCourseTeacher" -> {
                    course.setTeacherUrl(text);
                    course.setHasTeacher(true);
                    courseRepository.save(course);
                    msgToAdmin = msg.editedCourseTeacher;
                }
                default -> {
                    return;
                }
            }
            if (markup == null) {
                bot.sendMessage(botInfo.getId(), user.getChatId(), msgToAdmin);
                eventCode(user, "about course");
                aboutCourse(botInfo, user, editCourse);
            } else {
                bot.sendMessage(botInfo.getId(), user.getChatId(), msgToAdmin, markup);
            }
        }
    }

    public void viewCourseLessons(BotInfo botInfo, BotUser user, String text) {
        Course course = courseRepository.findCourse(user.getCourseId());
        List<Lesson> lessons = lessonRepository.getLessonsOfCourse(course.getId());
        if (text.equals(addLesson)) {
            bot.sendMessage(botInfo.getId(), user.getChatId(), msg.addLessonName(course), kyb.toBack);
            eventCode(user, "get new lesson name");
        } else if (text.equals(backBtn)) {
            courseMenu(botInfo, user, course.getName());
        } else {
            Lesson lesson = lessonRepository.findLessonFromCourse(text, course.getId());
            if (lesson == null) {
                bot.sendMessage(botInfo.getId(), user.getChatId(), msg.wrongBtn, kyb.setLessons(lessons, lessons.isEmpty()));
                return;
            }
            //qilinmoqda
            bot.sendMessage(botInfo.getId(), user.getChatId(), msg.lessonInformation(lesson, course), kyb.lessonCrud());
            user.setLessonId(lesson.getId());
            user.setEventCode("lessonCrud");
            botUserService.save(user);
        }
    }

    public void addLesson(BotInfo botInfo, BotUser user, Message message) {
        String eventCode;
        eventCode = user.getEventCode();

        Course course = courseRepository.findCourse(user.getCourseId());
        Lesson lesson = lessonRepository.draftLesson(course.getId());
        if (lesson == null) {
            lesson = new Lesson();
            lesson.setCourseId(course.getId());
            lesson.setStatus(Status.DRAFT);
            lesson.setActive(false);
            lessonRepository.save(lesson);
        }
        if (message.hasText()) {
            String text = message.getText();
            if (text.equals(toBackBtn)) {
                aboutCourse(botInfo, user, viewCourseLessons);
                return;
            }
            switch (eventCode) {
                case "get new lesson name" -> {
                    Lesson checkLesson = lessonRepository.findLessonFromCourse(text, course.getId());
                    if (checkLesson != null) {
                        bot.sendMessage(botInfo.getId(), user.getChatId(), msg.busyLessonName(course, text), kyb.toBack);
                    } else {
                        lesson.setName(text);
                        lessonRepository.save(lesson);
                        bot.sendMessage(botInfo.getId(), user.getChatId(), msg.addedLessonName());
                        eventCode(user, "get new lesson desc");
                    }
                }
                case "get new lesson desc" -> {
                    lesson.setDescription(text);
                    lessonRepository.save(lesson);
                    bot.sendMessage(botInfo.getId(), user.getChatId(), msg.addedLessonDesc());
                    eventCode(user, "get new lesson video");
                }
                case "get new lesson video" -> bot.sendMessage(botInfo.getId(), user.getChatId(), msg.notVideo());
                case "is present video" -> {
                    if (text.equals("✅ Ha")) {
                        bot.sendMessage(botInfo.getId(), user.getChatId(), msg.getVideoMsg(), true);
                        eventCode(user, "get new lesson video");
                    } else if (text.equals("❌ Yo'q")) {
                        bot.sendMessage(botInfo.getId(), user.getChatId(), msg.getNewHomeworkForLesson(), kyb.leaveBtn);
                        eventCode(user, "get new lesson homework");
                    }
                }
                case "get new lesson homework" -> {
                    lesson.setHasHomework(false);
                    if (!text.equals(leaveBtn)) {
                        lesson.setHasHomework(true);
                        lesson.setHomework(text);
                        lessonRepository.save(lesson);
                    }
                    bot.sendMessage(botInfo.getId(), user.getChatId(), msg.isOpen(), kyb.isSuccess("uz"));
                    eventCode(user, "get new lesson is free");
                }
                case "get new lesson is free" -> {
                    if (text.equals("❌ Yo'q")) {
                        lesson.setFree(false);
                    } else if (text.equals("✅ Ha")) {
                        lesson.setFree(true);
                    } else return;
                    lessonRepository.save(lesson);
                    for (LessonVideo lessonVideo : lessonVideoRepository.findByLessonId(lesson.getId())) {
                        bot.sendVideo(botInfo.getId(), user.getChatId(), lessonVideo.getVideo(), null, false);
                    }
                    bot.sendMessage(botInfo.getId(), user.getChatId(), msg.isAddLessonMsg(lesson, course), kyb.isSuccess);
                    eventCode(user, "is add lesson");
                }
                case "is add lesson" -> {
                    if (text.equals(confirm)) {
                        lesson.setActive(true);
                        lesson.setStatus(Status.OPEN);
                        lessonRepository.save(lesson);
                        bot.sendMessage(botInfo.getId(), user.getChatId(), msg.addedLesson);
                        aboutCourse(botInfo, user, viewCourseLessons);
                    } else if (text.equals(cancel)) {
                        lesson.setStatus(Status.OPEN);
                        lesson.setName(UUID.randomUUID().toString() + lesson.getId());
                        lessonRepository.save(lesson);
                        bot.sendMessage(botInfo.getId(), user.getChatId(), msg.cancelOperation);
                        aboutCourse(botInfo, user, viewCourseLessons);
                    }
                }
            }
        } else if (message.hasVideo()) {
            Video video = message.getVideo();
            if (eventCode.equals("get new lesson video")) {
                LessonVideo lessonVideo = new LessonVideo();
                lessonVideo.setLessonId(lesson.getId());
                lessonVideo.setActive(true);
                lessonVideo.setVideo(video.getFileId());
                lessonVideoRepository.save(lessonVideo);
                bot.sendMessage(botInfo.getId(), user.getChatId(), msg.isPresentVideo, kyb.isSuccess("uz"));
                eventCode(user, "is present video");
            }
        }
    }

    public void lessonCrud(BotInfo botInfo, BotUser user, String text) {
        switch (text) {
            case backBtn -> aboutCourse(botInfo, user, viewCourseLessons);
            case addLesson -> viewCourseLessons(botInfo, user, addLesson);
            case editLesson -> {
                //nimadirlar qilinadi hali buyerga
            }
            case deleteLesson -> {
                break;
            }
            case viewLessonVideos -> {
                return;
            }
        }
    }
}