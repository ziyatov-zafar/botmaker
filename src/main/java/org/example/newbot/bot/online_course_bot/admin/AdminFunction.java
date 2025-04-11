package org.example.newbot.bot.online_course_bot.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.newbot.bot.Status;
import org.example.newbot.model.BotInfo;
import org.example.newbot.model.BotUser;
import org.example.newbot.model.online_course_entities.Course;
import org.example.newbot.repository.BotInfoRepository;
import org.example.newbot.repository.CourseRepository;
import org.example.newbot.service.BotUserService;
import org.example.newbot.service.DynamicBotService;
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
        Course course = courseRepository.findCourse(user.getCourseId());
        if (text.equals(backBtn)) start(botInfo, user);
        else if (text.equals(addCourse)) {
            bot.sendMessage(botInfo.getId(), user.getChatId(), msg.getNewCourseName, kyb.addBackAndMainBtn(new ArrayList<>()));
            eventCode(user, "addCourseName");
        } else if (text.equals(editCourse)) {
            bot.sendMessage(botInfo.getId(), user.getChatId(), msg.editCourseMsg(course), kyb.editCourse(course.getHasGroup(), course.getHasTeacher()));
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
}