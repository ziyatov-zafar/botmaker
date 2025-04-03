package org.example.newbot.bot.online_magazine_bot.admin;

import org.example.newbot.bot.Kyb;
import org.example.newbot.model.BotUser;
import org.example.newbot.model.Category;
import org.example.newbot.model.ProductVariant;
import org.example.newbot.model.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

import static org.example.newbot.bot.StaticVariable.*;

public class AdminOnlineMagazineKyb extends Kyb {
    public ReplyKeyboardMarkup menu = setKeyboards(adminOnlineMagazineMenu, 2);

    public ReplyKeyboardMarkup usersPage() {
        KeyboardRow row = new KeyboardRow();
        boolean isOne = !(adminOnlineMagazineUsersPage.length % 2 == 0);
        List<KeyboardRow> rows = new ArrayList<>();
        int c = 0;
        for (int i = 0; i < adminOnlineMagazineUsersPage.length - 2; i++) {
            String s = adminOnlineMagazineUsersPage[i];
            c++;
            row.add(new KeyboardButton(s));
            if (!isOne) {
                rows.add(row);
                row = new KeyboardRow();
                c = 0;
                isOne = true;
            } else {
                if (c == 2) {
                    rows.add(row);
                    row = new KeyboardRow();
                    c = 0;
                    isOne = false;
                }
            }
        }

        rows.add(row);
        row = new KeyboardRow();
        row.add(new KeyboardButton(backButton));
        row.add(new KeyboardButton(mainMenu));
        rows.add(row);
        return ReplyKeyboardMarkup.builder().selective(true).resizeKeyboard(true).keyboard(rows).build();
    }

    public InlineKeyboardMarkup getUsers(List<BotUser> list) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            BotUser botUser = list.get(i);
            row.add(createButton(String.valueOf(i + 1), botUser.getId()));
            if ((i + 1) % 5 == 0) {
                rows.add(row);
                row = new ArrayList<>();
            }
        }
        rows.add(row);
        row = new ArrayList<>();
        row.add(createButton("⬅️ Oldingi", "prev"));
        row.add(createButton("Keyingi ➡️", "next"));
        rows.add(row);
        return new InlineKeyboardMarkup(rows);
    }

    public InlineKeyboardMarkup updateUser(boolean isBlock, Boolean isList) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(createButton(isBlock ? "✅ Blokdan olish" : "🚫 Bloklash", isBlock ? "unblock" : "block"));
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(row);
        if (isList) {
            row = new ArrayList<>();
            row.add(createButton(backButton, "back"));
            rows.add(row);
        }
        return new InlineKeyboardMarkup(rows);
    }

    public ReplyKeyboardMarkup setCategories(List<Category> categories, boolean empty) {
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        KeyboardButton button;
        if (empty) {
            button = new KeyboardButton(addCategory);
            rows = new ArrayList<>();
            row = new KeyboardRow();
            row.add(button);
            rows.add(row);
            row = new KeyboardRow();
            return setbackAndMenuBtn(rows, row);
        }
        for (int i = 0; i < categories.size(); i++) {
            button = new KeyboardButton(categories.get(i).getNameUz());
            row.add(button);
            if ((i + 1) % 2 == 0) {
                rows.add(row);
                row = new KeyboardRow();
            }
        }
        rows.add(row);
        row = new KeyboardRow();
        button = new KeyboardButton(addCategory);
        row.add(button);
        rows.add(row);
        row = new KeyboardRow();
        return setbackAndMenuBtn(rows, row);
    }

    public static ReplyKeyboardMarkup setbackAndMenuBtn(List<KeyboardRow> rows, KeyboardRow row) {
        KeyboardButton button;
        button = new KeyboardButton(backButton);
        row.add(button);
        button = new KeyboardButton(mainMenu);
        row.add(button);
        rows.add(row);
        return ReplyKeyboardMarkup.builder().selective(true).resizeKeyboard(true).keyboard(rows).build();
    }

    public InlineKeyboardMarkup crudCategory() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // 1-qator: Mahsulotlarini ko'rish (yagona tugma)
        rows.add(List.of(
                InlineKeyboardButton.builder()
                        .text("📦 Mahsulotlarini ko'rish")
                        .callbackData("view_products")
                        .build()
        ));

        // 2-qator: Nomlarni o'zgartirish
        rows.add(List.of(
                InlineKeyboardButton.builder()
                        .text("🇺🇿 O'zbekcha nom")
                        .callbackData("edit_uz_name")
                        .build(),
                InlineKeyboardButton.builder()
                        .text("🇷🇺 Ruscha nom")
                        .callbackData("edit_ru_name")
                        .build()
        ));

        // 3-qator: O'chirish (yagona tugma)
        rows.add(List.of(
                InlineKeyboardButton.builder()
                        .text("🗑 O'chirish")
                        .callbackData("delete_category")
                        .build()
        ));

        markup.setKeyboard(rows);
        return markup;
    }

    public InlineKeyboardMarkup isDeleteBtn() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // 1-qator: Ha va Yo'q tugmalari
        rows.add(List.of(
                InlineKeyboardButton.builder()
                        .text("✅ Ha")
                        .callbackData("delete_confirm")
                        .build(),
                InlineKeyboardButton.builder()
                        .text("❌ Yo'q")
                        .callbackData("delete_cancel")
                        .build()
        ));

        // 2-qator: Orqaga tugmasi
        rows.add(List.of(
                InlineKeyboardButton.builder()
                        .text("◀️ Orqaga")
                        .callbackData("back_action")
                        .build()
        ));

        markup.setKeyboard(rows);
        return markup;
    }

    public InlineKeyboardMarkup isAddProduct(List<ProductVariant> variants, Long variantId) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        String active = "✅ ";
        for (int i = 0; i < variants.size(); i++) {
            ProductVariant variant = variants.get(i);
            row.add(createButton((variantId.equals(variant.getId()) ? active : "") + variant.getNameUz(), variant.getId()));
            if ((i + 1) % 3 == 0) {
                rows.add(row);
                row = new ArrayList<>();
            }
        }
        rows.add(row);
        return new InlineKeyboardMarkup(rows);
    }

    public InlineKeyboardMarkup getProductVariantsAndEditProductBtn(List<ProductVariant> variants, Long variantId) {
        String active = "✅ ";
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (int i = 0; i < variants.size(); i++) {
            ProductVariant variant = variants.get(i);
            boolean inActive = variant.getId().equals(variantId);
            row.add(createButton((inActive ? (active) : "") + (variant.getNameUz()), variant.getId()));
            if ((i + 1) % 3 == 0) {
                rows.add(row);
                row = new ArrayList<>();
            }
        }
        rows.add(row);
        row = new ArrayList<>();
        row.add(createButton("✏️ O'zgartirish", "edit"));
        row.add(createButton("➕ Tur qo'shish", "add type"));
        rows.add(row);
        row = new ArrayList<>();
        row.add(createButton("\uD83D\uDDD1 O'chirish", "delete"));
        rows.add(row);
        return new InlineKeyboardMarkup(rows);
    }

    public InlineKeyboardMarkup productVariantIsDelete() {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(createButton("\uD83D\uDDD1 O'chirish", "delete product variant"));
        rows.add(row);
        row = new ArrayList<>();
        row.add(createButton(backButton, "back1"));
        rows.add(row);
        return new InlineKeyboardMarkup(rows);
    }

    public InlineKeyboardMarkup editProductBtn() {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(createButton("✏️ O'zbekcha nomini o'zgartirish", "edit name uz"));
        rows.add(row);
        row = new ArrayList<>();
        row.add(createButton("✏️ Ruscha nomini o'zgartirish", "edit name ru"));
        rows.add(row);
        row = new ArrayList<>();
        row.add(createButton("✏️ O'zbekcha tavsifni o'zgartirish", "edit desc uz"));
        rows.add(row);
        row = new ArrayList<>();
        row.add(createButton("✏️ Ruscha tavsifni o'zgartirish", "edit desc ru"));
        rows.add(row);
        row = new ArrayList<>();
        row.add(createButton("💸 Narxini o'zgartirish", "edit price"));
        rows.add(row);
        row = new ArrayList<>();
        row.add(createButton(backButton, "back"));
        rows.add(row);
        return new InlineKeyboardMarkup(rows);
    }
}
