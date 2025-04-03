package org.example.newbot.bot.roleuser;

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
public class UserKyb extends Kyb {
    public ReplyKeyboardMarkup isBuyBot = setKeyboards(new String[]{"\uD83D\uDEE0 Yaratish", "❌ Bekor qilish", backButton, mainMenu}, 2);

    public ReplyKeyboardMarkup menu() {
        KeyboardButton button = new KeyboardButton(menu[0]);
        KeyboardRow row = new KeyboardRow();
        List<KeyboardRow> rows = new ArrayList<KeyboardRow>();
        row.add(button);
        rows.add(row);
        row = new KeyboardRow();
        button = new KeyboardButton(menu[1]);
        row.add(button);
        button = new KeyboardButton(menu[2]);
        row.add(button);
        rows.add(row);
        row = new KeyboardRow();
        button = new KeyboardButton(menu[3]);
        row.add(button);
        button = new KeyboardButton(menu[4]);
        row.add(button);
        rows.add(row);
        return ReplyKeyboardMarkup.builder().resizeKeyboard(true).selective(true).keyboard(rows).build();
    }

    public ReplyKeyboardMarkup setBotTypes(List<BotPrice> data) {
        KeyboardButton button;
        KeyboardRow row = new KeyboardRow();
        List<KeyboardRow> rows = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            button = new KeyboardButton(data.get(i).getTypeText());
            row.add(button);
            if ((i + 1) % 2 == 0) {
                rows.add(row);
                row = new KeyboardRow();
            }
        }
        rows.add(row);
        row = new KeyboardRow();
        button = new KeyboardButton(backButton);
        row.add(button);
        button = new KeyboardButton(mainMenu);
        row.add(button);
        rows.add(row);
        return ReplyKeyboardMarkup.builder().selective(true).resizeKeyboard(true).keyboard(rows).build();
    }
}
