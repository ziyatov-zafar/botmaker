package org.example.newbot.bot.online_course_bot.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.newbot.bot.online_course_bot.admin.AdminKyb;
import org.example.newbot.model.BotInfo;
import org.example.newbot.model.BotUser;
import org.example.newbot.repository.BotInfoRepository;
import org.example.newbot.service.BotUserService;
import org.example.newbot.service.DynamicBotService;
@RequiredArgsConstructor
@Log4j2
public class UserFunction {
    private final BotInfoRepository botInfoRepository;
    private final BotUserService botUserService;
    private final DynamicBotService bot;
    private final UserKyb kyb;
    private final UserMsg msg;
    public void start(BotInfo botInfo, BotUser user) {

    }
}
