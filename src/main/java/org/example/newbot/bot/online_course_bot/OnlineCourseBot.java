package org.example.newbot.bot.online_course_bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.newbot.bot.online_course_bot.admin.AdminFunction;
import org.example.newbot.bot.online_course_bot.admin.AdminKyb;
import org.example.newbot.bot.online_course_bot.user.UserFunction;
import org.example.newbot.bot.online_course_bot.user.UserKyb;
import org.example.newbot.dto.ResponseDto;
import org.example.newbot.model.BotInfo;
import org.example.newbot.model.BotUser;
import org.example.newbot.repository.BotInfoRepository;
import org.example.newbot.service.BotUserService;
import org.example.newbot.service.DynamicBotService;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

import static org.example.newbot.bot.online_course_bot.OnlineCourseConstVariables.mainBtn;

@Log4j2
@RequiredArgsConstructor
public class OnlineCourseBot {
    private final DynamicBotService bot;
    private final BotInfoRepository botInfoRepository;
    private final BotUserService botUserService;
    private final AdminFunction adminFunction;
    private final UserFunction userFunction;

    public void onlineCourseBotMenu(BotInfo botInfo, Long chatId, Update update, Long adminChatId) {

        boolean isAdmin = false;
        for (Long x : botInfo.getAdminChatIds()) {
            if (x.equals(chatId)) {
                isAdmin = true;
                break;
            }
        }
        ResponseDto<BotUser> checkUser = botUserService.findByUserChatId(chatId, botInfo.getId());
        String username, firstname, lastname, nickname;
        if (update.hasMessage()) {
            bot.setActive(botInfo.getId(), chatId);
            Message message = update.getMessage();
            username = message.getFrom().getUserName();
            firstname = message.getFrom().getFirstName();
            lastname = message.getFrom().getLastName();
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            username = callbackQuery.getFrom().getUserName();
            firstname = callbackQuery.getFrom().getFirstName();
            lastname = callbackQuery.getFrom().getLastName();
        } else return;
        nickname = firstname + " " + (lastname != null ? lastname : "");
        BotUser user;
        if (checkUser.isSuccess()) {
            user = checkUser.getData();
        } else {
            user = new BotUser();
            user.setChatId(chatId);
            user.setRole("user");
            user.setEventCode("online");
            List<BotInfo> bots = user.getBots();
            if (bots == null) bots = new ArrayList<>();
            bots.add(botInfo);
            List<BotUser> users = botInfo.getUsers();
            users.add(user);
            botUserService.save(user);
            botInfoRepository.save(botInfo);
        }
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setNickname(nickname);
        user.setUsername(username);
        botUserService.save(user);
        String eventCode = user.getEventCode();
        if (isAdmin) {
            if (!adminChatId.equals(user.getChatId())) user.setRole("admin");
            else user.setRole("user");
            if (update.hasMessage()) {
                Message message = update.getMessage();
                if (message.hasText()) {
                    String text = message.getText();
                    if (text.equals("/start") || text.equals(mainBtn)) {
                        adminFunction.start(botInfo, user);
                    } else {
                        switch (eventCode) {
                            case "menu" -> adminFunction.menu(botInfo, user, text);
                            case "course menu" -> adminFunction.courseMenu(botInfo, user, text);
                            case "addCourseName", "addNewCourseDescription", "addNewCoursePrice",
                                 "addCourseNewGroupLink", "addCourseNewTeacherLink", "isAddCourse" ->
                                    adminFunction.addCourse(botInfo, user, text, eventCode);
                            case "about course" -> adminFunction.aboutCourse(botInfo, user, text);
                            case "editCourse" -> adminFunction.editCourse(botInfo, user, text);
                            case "editCourseName", "editCourseDescription", "editCoursePrice",
                                 "editCourseGroup", "editCourseTeacher" ->
                                    adminFunction.editCourseInformation(botInfo, user, text);
                            case "viewCourseLessons" -> adminFunction.viewCourseLessons(botInfo, user, text);
                            case "get new lesson name", "get new lesson desc", "get new lesson video",
                                 "is present video", "get new lesson homework", "get new lesson is free",
                                 "is add lesson" -> adminFunction.addLesson(botInfo, user, message);
                            case "lessonCrud" -> adminFunction.lessonCrud(botInfo, user, text);
                            case "edit lesson for btn" -> adminFunction.editLessonForBtn(botInfo, user, text);
                            case "edit lesson name", "edit lesson desc", "edit lesson homework" ->
                                    adminFunction.editLesson(botInfo, user, text);
                            case "get lesson videos" ->
                                    adminFunction.getLessonVideo(botInfo, user, text, message.getMessageId());
                            case "add video to lesson" ->
                                    adminFunction.addVideoToLesson(botInfo, user, text, message.getMessageId());
                        }
                    }
                } else if (message.hasVideo()) {

                    switch (eventCode) {
                        case "get new lesson video" -> adminFunction.addLesson(botInfo, user, message);
                        case "add video to lesson" -> adminFunction.addVideoToLesson(botInfo, user, message.getVideo());
                    }

                }
            } else if (update.hasCallbackQuery()) {
                CallbackQuery callbackQuery = update.getCallbackQuery();
                String data = callbackQuery.getData();
                int messageId = callbackQuery.getMessage().getMessageId();
                if (eventCode.equals("get lesson videos"))
                    adminFunction.getLessonVideo(botInfo, user, data, messageId, callbackQuery);
            }
        } else {

            if (update.hasMessage()) {
                Message message = update.getMessage();
                if (message.hasText()) {
                    String text = message.getText();
                    if (text.equals("/start")) {
                        userFunction.start(botInfo, user);
                    } else {
                        if (eventCode.equals("requestContactForNewUser")) {
                            userFunction.requestContactForNewUser(botInfo, user, message.getText());
                        }
                    }
                } else if (message.hasContact()) {
                    if (eventCode.equals("requestContactForNewUser")) {
                        userFunction.requestContactForNewUser(botInfo, user, message.getContact());
                    }
                }
            } else if (update.hasCallbackQuery()) {
                CallbackQuery callbackQuery = update.getCallbackQuery();
                String data = callbackQuery.getData();
                int messageId = Integer.parseInt(data);
            }

        }
    }
}
