package org.example.newbot.bot.roleuser;

import org.example.newbot.bot.Kyb;
import org.example.newbot.bot.online_magazine_bot.admin.AdminOnlineMagazineKyb;
import org.example.newbot.model.BotInfo;
import org.example.newbot.model.BotPrice;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
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
        return setbackAndMenuBtn(rows);
    }

    public ReplyKeyboardMarkup setbackAndMenuBtn(List<KeyboardRow> rows) {
        return AdminOnlineMagazineKyb.setbackAndMenuBtn(rows);
    }

    public InlineKeyboardMarkup forChannel(String link, String botUsername) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(createButtonLink("\uD83D\uDC40 Botni ko'rish", link));
        rows.add(row);
        row = new ArrayList<>();
        row.add(createButtonLink("\uD83E\uDD16 Xuddi shunday bot yaratish", botUsername));
        rows.add(row);
        return new InlineKeyboardMarkup(rows);
    }

    public InlineKeyboardMarkup sendCheckToAdmin(Long chatId) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        row.add(createButton("✅ Chekni qabul qilish", "checkConfirm_" + chatId)); // Emoji qo'shildi
        rows.add(row);
        row = new ArrayList<>();
        row.add(createButton("❌ Chekni bekor qilish", "checkCancelled_" + chatId)); // Emoji qo'shildi
        rows.add(row);
        return new InlineKeyboardMarkup(rows);
    }

    public InlineKeyboardMarkup bots(List<BotInfo> bots) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        int c = 0, i = 0;
        for (BotInfo bot : bots) {
            i++;
            c++;
            row.add(createButton(i + ") " + bot.getBotUsername(), "bot_" + bot.getId()));
            if (c % 2 == 0) {
                rows.add(row);
                row = new ArrayList<>();
            }
        }
        rows.add(row);
        return new InlineKeyboardMarkup(rows);
    }

    public InlineKeyboardMarkup aboutBot() {
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        row.add(createButton("🗑️ O'chirish", "delete"));
        rows.add(row);
        row = new ArrayList<>();
        row.add(createButton(backButton,"back"));
        rows.add(row);
        return new InlineKeyboardMarkup(rows);
    }

    public ReplyKeyboardMarkup paymentKyb() {
        String[] btns = {"✅ To'lov qildim"};
        KeyboardRow row = new KeyboardRow();
        List<KeyboardRow> rows = new ArrayList<>();
        row.add(btns[0]);
        rows.add(row);
        return (setbackAndMenuBtn(rows));

    }

    public InlineKeyboardMarkup addBalance() {
        List<InlineKeyboardButton>row = new ArrayList<>();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        row.add(createButton("\uD83D\uDCB8 Hisobni to'ldirish" , "addBalance"));
        rows.add(row);
        return new InlineKeyboardMarkup(rows);
    }
}
