package org.example.newbot.bot.online_course_bot.admin;

import org.example.newbot.bot.*;
import org.example.newbot.bot.online_course_bot.OnlineCourseConstVariables;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

public class AdminKyb extends Kyb {
    public ReplyKeyboardMarkup menu = setKeyboards(OnlineCourseConstVariables.menu, 2);
}
