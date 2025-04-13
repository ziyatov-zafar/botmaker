package org.example.newbot.bot.online_course_bot.user;

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
import org.telegram.telegrambots.meta.api.objects.Contact;

import java.util.List;

import static org.example.newbot.bot.online_course_bot.OnlineCourseConstVariables.userMenuBtn;

@RequiredArgsConstructor
@Log4j2
public class UserFunction {
    private final BotInfoRepository botInfoRepository;
    private final BotUserService botUserService;
    private final DynamicBotService bot;
    private final UserKyb kyb;
    private final UserMsg msg;/**/
    private final CourseRepository courseRepository;

    private void eventCode(BotUser user, String eventCode) {
        user.setEventCode(eventCode);
        botUserService.save(user);
    }
    public void start(BotInfo botInfo, BotUser user, boolean newUser) {
        if (user.getPhone() == null) {
            bot.sendMessage(botInfo.getId(), user.getChatId(), msg.requestContactForNewUser(user), kyb.requestContact(msg.contactBtn));
            eventCode(user, "requestContactForNewUser");
            return;
        }
        bot.sendMessage(botInfo.getId(), user.getChatId(), newUser ? msg.openMenu : msg.menu, kyb.menu());
        eventCode(user, "menu");
    }

    public void start(BotInfo botInfo, BotUser user) {
        start(botInfo, user, false);
    }



    public void requestContactForNewUser(BotInfo botInfo, BotUser user) {
        bot.sendMessage(botInfo.getId(), user.getChatId(), msg.wrongBtn, kyb.requestContact(msg.contactBtn));
    }

    public void requestContactForNewUser(BotInfo botInfo, BotUser user, Contact contact) {
        String phone = contact.getPhoneNumber();
        if (phone.charAt(0) != '+') phone = "+" + phone;
        user.setPhone(phone);
        botUserService.save(user);
        start(botInfo, user,true);
    }

    public void menu(BotInfo botInfo, BotUser user, String text) {
        List<Course> courses = courseRepository.findAllByActiveAndStatusAndBotIdOrderById(true, Status.OPEN, botInfo.getId());
        if (text.equals(userMenuBtn[0])){
            
        }
    }
}
