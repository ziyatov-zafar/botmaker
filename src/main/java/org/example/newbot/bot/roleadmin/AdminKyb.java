package org.example.newbot.bot.roleadmin;

import org.example.newbot.bot.Kyb;
import org.example.newbot.model.BotPrice;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

import static org.example.newbot.bot.StaticVariable.*;


@Controller
public class AdminKyb extends Kyb {
    public ReplyKeyboardMarkup menu = setKeyboards(botMakerAdminMenu, 2);

    public ReplyKeyboardMarkup botMenu() {
        KeyboardButton button;
        KeyboardRow row = new KeyboardRow();
        List<KeyboardRow> rows = new ArrayList<KeyboardRow>();
        for (int i = 0; i < botMakerAdminBotMenu.length - 2; i++) {
            button = new KeyboardButton(botMakerAdminBotMenu[i]);
            row.add(button);
            if ((i + 1) % 2 == 0) {
                rows.add(row);
                row = new KeyboardRow();
            }
        }
        rows.add(row);
        row = new KeyboardRow();
        button = new KeyboardButton(botMakerAdminBotMenu[botMakerAdminBotMenu.length - 2]);
        row.add(button);
        button = new KeyboardButton(botMakerAdminBotMenu[botMakerAdminBotMenu.length - 1]);
        row.add(button);
        rows.add(row);
        return markup(rows);
    }

    public ReplyKeyboardMarkup getBots(List<BotPrice> list) {
        KeyboardButton button;
        KeyboardRow row = new KeyboardRow();
        List<KeyboardRow> rows = new ArrayList<>();
        for (BotPrice botPrice : list) {
            button = new KeyboardButton(botPrice.getTypeText());
            row.add(button);
            rows.add(row);
            row = new KeyboardRow();
        }
        button = new KeyboardButton(backButton);
        row.add(button);
        button = new KeyboardButton(mainMenu);
        row.add(button);
        rows.add(row);
        return markup(rows);
    }
}
