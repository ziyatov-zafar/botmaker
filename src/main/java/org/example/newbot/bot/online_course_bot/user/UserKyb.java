package org.example.newbot.bot.online_course_bot.user;

import org.example.newbot.bot.Kyb;
import org.example.newbot.bot.online_course_bot.OnlineCourseConstVariables;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

import static org.example.newbot.bot.online_course_bot.OnlineCourseConstVariables.mainBtn;
import static org.example.newbot.bot.online_course_bot.OnlineCourseConstVariables.userMenuBtn;

public class UserKyb extends Kyb {

    public ReplyKeyboardMarkup addBackAndMainBtn(List<KeyboardRow> rows) {
        KeyboardRow row = new KeyboardRow();
        row.add(OnlineCourseConstVariables.backBtn);
        row.add(mainBtn);
        rows.add(row);
        return markup(rows);
    }

    ReplyKeyboardMarkup menu() {
        KeyboardRow row = new KeyboardRow();
        row.add(userMenuBtn[0]);
        List<KeyboardRow> rows = new ArrayList<KeyboardRow>();
        rows.add(row);
        row = new KeyboardRow();
        row.add(userMenuBtn[1]);
        rows.add(row);
        row = new KeyboardRow();
        row.add(userMenuBtn[2]);
        row.add(userMenuBtn[3]);
        rows.add(row);
        return markup(rows);
    }
}
