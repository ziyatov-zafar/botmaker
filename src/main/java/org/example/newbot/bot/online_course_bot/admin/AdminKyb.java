package org.example.newbot.bot.online_course_bot.admin;

import org.example.newbot.bot.Kyb;
import org.example.newbot.bot.online_course_bot.OnlineCourseConstVariables;
import org.example.newbot.repository.BotInfoRepository;
import org.example.newbot.service.BotUserService;
import org.example.newbot.service.DynamicBotService;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class AdminKyb extends Kyb {
    public ReplyKeyboardMarkup menu=setKeyboards(OnlineCourseConstVariables.menu,2);

}
