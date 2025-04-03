package org.example.newbot.bot.online_magazine_bot.user;

import org.example.newbot.bot.Kyb;
import org.example.newbot.model.Location;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

import static org.example.newbot.bot.StaticVariable.*;
import static org.example.newbot.bot.online_magazine_bot.user.ConstVariable.*;

public class UserOnlineMagazineKyb extends Kyb {
    public ReplyKeyboardMarkup backBtn(String lang) {
        return setKeyboards(new String[]{lang.equals("uz") ? backButton : backButtonRu}, 1);
    }

    public ReplyKeyboardMarkup mainBtn(String lang) {
        return setKeyboards(new String[]{
                lang.equals("uz") ? backButton : backButtonRu,
                lang.equals("uz") ? mainMenu : mainMenuRu
        }, 1);
    }

    public ReplyKeyboardMarkup menu(String lang) {
        String[] menu = menuBtn(lang);
        KeyboardButton button = new KeyboardButton(menu[0]);
        KeyboardRow row = new KeyboardRow();
        row.add(button);
        List<KeyboardRow> rows = new ArrayList<>();
        rows.add(row);
        row = new KeyboardRow();
        button = new KeyboardButton(menu[1]);
        row.add(button);
        rows.add(row);
        row = new KeyboardRow();
        button = new KeyboardButton(menu[2]);
        row.add(button);
        button = new KeyboardButton(menu[3]);
        row.add(button);
        rows.add(row);
        return markup(rows);
    }

    public ReplyKeyboardMarkup deliveryType(String lang) {
        return setKeyboards(ConstVariable.deliveryType(lang), 2);
    }

    public ReplyKeyboardMarkup getLocation(String lang) {
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton(location(lang)[0]));
        rows.add(row);
        row = new KeyboardRow();
        KeyboardButton button = new KeyboardButton(location(lang)[1]);
        button.setRequestLocation(true);
        row.add(button);
        rows.add(row);
        row = new KeyboardRow();
        row.add(new KeyboardButton(location(lang)[2]));
        row.add(new KeyboardButton(location(lang)[3]));
        rows.add(row);
        return markup(rows);
    }
}
