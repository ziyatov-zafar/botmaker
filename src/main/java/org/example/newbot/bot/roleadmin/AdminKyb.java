package org.example.newbot.bot.roleadmin;

import org.example.newbot.bot.Kyb;
import org.example.newbot.bot.StaticVariable;
import org.example.newbot.model.BotInfo;
import org.example.newbot.model.BotPrice;
import org.example.newbot.model.Channel;
import org.example.newbot.model.User;
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
public class AdminKyb extends Kyb {
    public ReplyKeyboardMarkup menu = setKeyboards(botMakerAdminMenu, 2);
    public ReplyKeyboardMarkup userPage = setKeyboards(userPageMenu, 2);
    public ReplyKeyboardMarkup settingsMenu = setKeyboards(StaticVariable.settingsMenu, 2);

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


    public ReplyKeyboardMarkup chooseTypeBotList() {
        return setKeyboards(chooseTypeBotList, 2);
    }

    public InlineKeyboardMarkup getAllBots(List<BotInfo> bots, boolean first, boolean last) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (int i = 0; i < bots.size(); i++) {
            row.add(createButton("" + (i + 1), bots.get(i).getId().toString()));
            if ((i + 1) % 5 == 0) {
                rows.add(row);
                row = new ArrayList<>();
            }
        }

        if (!row.isEmpty()) {
            rows.add(row);
        }

        // Navigatsiya tugmalari
        rows = addPagination(rows, first, last);

        return new InlineKeyboardMarkup(rows);
    }

    private List<List<InlineKeyboardButton>> addPagination(List<List<InlineKeyboardButton>> rows, boolean first, boolean last) {
        List<InlineKeyboardButton> navigationRow = new ArrayList<>();
        if (!first || !last) {
            if (first) {
                navigationRow.add(createButton("⏭️ Keyingisi", "next"));
            } else if (last) {
                navigationRow.add(createButton("⏮️ Oldingisi", "back"));
            } else {
                navigationRow.add(createButton("⏮️ Oldingisi", "back"));
                navigationRow.add(createButton("⏭️ Keyingisi", "next"));
            }
        }
        rows.add(navigationRow);
        return rows;
    }

    public InlineKeyboardMarkup crudBot(boolean active) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> statusRow = new ArrayList<>();
        statusRow.add(createButton(active ? "🔴 To‘xtatish" : "🟢 Faollashtirish", active ? "deactivate_bot" : "activate_bot"));
        rows.add(statusRow);
        if (active) {
            List<InlineKeyboardButton> editDeleteRow = new ArrayList<>();
            editDeleteRow.add(createButton("👤 Admin qo'shish", "add_admin"));
            editDeleteRow.add(createButton("❌ Adminlarni olish", "delete_all_admin"));
            rows.add(editDeleteRow);
        }
        List<InlineKeyboardButton> backRow = new ArrayList<>();
        backRow.add(createButton("🔙 Orqaga", "back_to_bots"));
        rows.add(backRow);

        return new InlineKeyboardMarkup(rows);
    }

    public InlineKeyboardMarkup userLists(List<User> content, boolean first, boolean last) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (int i = 0; i < content.size(); i++) {
            row.add(createButton("" + (i + 1), content.get(i).getId()));
            if ((i + 1) % 5 == 0) {
                rows.add(row);
                row = new ArrayList<>();
            }
        }
        if (!row.isEmpty()) {
            rows.add(row);
        }
        return new InlineKeyboardMarkup(addPagination(rows, first, last));
    }

    public InlineKeyboardMarkup userCrud(boolean active) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Balans qo'shish tugmasi (faqat aktiv foydalanuvchilar uchun)
        if (active) {
            row.add(createButton("➕ Balans qo'shish", "addBalance"));
            rows.add(row);
            row = new ArrayList<>();
        }

        // Bloklash yoki blokdan chiqarish tugmasi
        row.add(createButton(active ? "🚫 Bloklash" : "✅ Blokdan chiqarish", active ? "blockUser" : "unblockUser"));
        rows.add(row);
        row = new ArrayList<>();

        // Orqaga qaytish tugmasi
        row.add(createButton("🔙 Orqaga qaytish", "back1"));
        rows.add(row);

        return new InlineKeyboardMarkup(rows);
    }


    public InlineKeyboardMarkup addBalance() {
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Raqamlar 1-9
        for (int i = 0; i < 9; i++) {
            row.add(createButton("" + (i + 1), "%d".formatted(i + 1)));
            // Har 3 tugmadan so'ng yangi qator boshlanadi
            if ((i + 1) % 3 == 0) {
                rows.add(row);
                row = new ArrayList<>();
            }
        }

        // 0 tugmasi

        // O'chirish tugmasi
        row.add(createButton("❌", "delete")); // O'chirish tugmasi
        row.add(createButton("0", "0")); // 0 tugmasi

        // Saqlash tugmasi
        row.add(createButton("💾 Saqlash", "save")); // Saqlash tugmasi

        // So'nggi qatorni qo'shamiz
        rows.add(row);

        // Orqaga qaytish tugmasi
        List<InlineKeyboardButton> backRow = new ArrayList<>();
        backRow.add(createButton("❌ Bekor qilish", "cancel")); // Orqaga tugmasi
        rows.add(backRow);

        return new InlineKeyboardMarkup(rows);
    }

    public ReplyKeyboardMarkup setBotPrices(List<BotPrice> list) {
        KeyboardRow row = new KeyboardRow();
        List<KeyboardRow> rows = new ArrayList<>();
        for (BotPrice botPrice : list) {
            row.add(new KeyboardButton(botPrice.getTypeText()));
            rows.add(row);
            row = new KeyboardRow();
        }

        return markup(addBackAndMainBtn(rows));
    }

    private List<KeyboardRow> addBackAndMainBtn(List<KeyboardRow> rows) {
        KeyboardRow row = new KeyboardRow();
        row.add(backButton);
        row.add(mainMenu);
        rows.add(row);
        return rows;
    }

    public ReplyKeyboardMarkup editBotPrice() {
        KeyboardRow row = new KeyboardRow();
        List<KeyboardRow> rows = new ArrayList<>();

        // Turini o'zgartirish tugmasi
        row.add("🔄 Turini o'zgartirish");
        rows.add(row);

        row = new KeyboardRow();
        // Tavsifini o'zgartirish tugmasi
        row.add("✏️ Tavsifini o'zgartirish");
        rows.add(row);

        row = new KeyboardRow();
        // Narxini o'zgartirish tugmasi
        row.add("💵 Narxini o'zgartirish");
        rows.add(row);

        // Orqaga qaytish va bosh menyuga qaytish tugmalari
        return markup(addBackAndMainBtn(rows));
    }

    public ReplyKeyboardMarkup editCard() {
        KeyboardRow row = new KeyboardRow();
        row.add("🏦 Karta turi");
        row.add("💳 Karta raqami");
        List<KeyboardRow> rows = new ArrayList<>();
        rows.add(row);

        row = new KeyboardRow();
        row.add("👤 Karta egasi");
        row.add("🖼️ Karta rasmi");
        rows.add(row);

        return markup(addBackAndMainBtn(rows));
    }


    public ReplyKeyboardMarkup getChannels(List<Channel> channels) {
        KeyboardRow row = new KeyboardRow();
        List<KeyboardRow> rows = new ArrayList<>();
        for (int i = 0; i < channels.size(); i++) {
            row.add(channels.get(i).getName());
            if ((i + 1) % 2 == 0) {
                rows.add(row);
                row = new KeyboardRow();
            }
        }
        rows.add(row);
        row = new KeyboardRow();
        row.add(addChannel);
        rows.add(row);
        return markup(addBackAndMainBtn(rows));
    }

    public ReplyKeyboardMarkup crudChannel() {
        KeyboardRow row = new KeyboardRow();
        List<KeyboardRow> rows = new ArrayList<>();

        row.add("✏️ Kanal nomini o‘zgartirish");
        row.add("🔗 Kanal usernamesini o‘zgartirish");
        rows.add(row);

        row = new KeyboardRow();
        row.add("🗑️ O‘chirish");
        rows.add(row);

        return markup(addBackAndMainBtn(rows));
    }

}
