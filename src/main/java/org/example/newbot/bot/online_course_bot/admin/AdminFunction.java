package org.example.newbot.bot.online_course_bot.admin;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.newbot.model.BotInfo;
import org.example.newbot.model.BotUser;
import org.example.newbot.repository.BotInfoRepository;
import org.example.newbot.service.BotUserService;
import org.example.newbot.service.DynamicBotService;
@Log4j2
@RequiredArgsConstructor
public class AdminFunction {
    private final BotInfoRepository botInfoRepository;
    private final BotUserService botUserService;
    private final DynamicBotService bot;
    private final AdminKyb kyb;
    private final AdminMsg msg;
    public void start(BotInfo botInfo, BotUser user) {
        bot.sendMessage(botInfo.getId(), user.getChatId(), msg.menu, kyb.menu);
        eventCode(user, "menu");
    }
    private void eventCode(BotUser user, String eventCode) {
        user.setEventCode(eventCode);
        botUserService.save(user);
    }
    public void menu(BotUser user, String text) {
        eventCode(user, text);
    }
}