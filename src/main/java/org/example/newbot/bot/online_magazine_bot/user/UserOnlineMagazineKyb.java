package org.example.newbot.bot.online_magazine_bot.user;

import org.example.newbot.bot.Kyb;
import org.example.newbot.model.Branch;
import org.example.newbot.model.Category;
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

    public ReplyKeyboardMarkup locationList(List<Location> list, String lang) {
        KeyboardRow row = new KeyboardRow();
        List<KeyboardRow> rows = new ArrayList<>();
        for (Location location : list) {
            KeyboardButton button = new KeyboardButton(location.getAddress());
            row.add(button);
            rows.add(row);
            row = new KeyboardRow();
        }
        row.add(lang.equals("uz") ? backButton : backButtonRu);
        row.add(lang.equals("uz") ? mainMenu : mainMenuRu);
        rows.add(row);
        return markup(rows);
    }

    public ReplyKeyboardMarkup isSuccessLocation(String lang) {
        String[] successBtn = new String[isSuccessForText(lang).length + 2];
        System.arraycopy(isSuccessForText(lang), 0, successBtn, 0, isSuccessForText(lang).length);
        successBtn[successBtn.length - 2] = lang.equals("uz") ? backButton : backButtonRu;
        successBtn[successBtn.length - 1] = lang.equals("uz") ? mainMenu : mainMenuRu;
        return setKeyboards(successBtn, 2);
    }

    public ReplyKeyboardMarkup setCategories(List<Category> list, String lang) {
        String[] mainBtn = {
                lang.equals("uz") ? backButton : backButtonRu,
                lang.equals("uz") ? mainMenu : mainMenuRu
        };
        KeyboardRow row = new KeyboardRow();
        List<KeyboardRow> rows = new ArrayList<>();
        String[] a = getCategories(list, lang);
        for (int i = 0; i < a.length; i++) {
            row.add(new KeyboardButton(a[i]));
            if ((i + 1) % 2 == 0) {
                rows.add(row);
                row = new KeyboardRow();
            }
        }
        rows.add(row);
        row = new KeyboardRow();
        row.add(mainBtn[0]);
        row.add(mainBtn[1]);
        rows.add(row);
        return markup(rows);
    }

    private String[] getCategories(List<Category> list, String lang) {
        String[] categories = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            Category category = list.get(i);
            categories[i] = lang.equals("uz") ? category.getNameUz() : category.getNameRu();
        }
        return categories;
    }

    public ReplyKeyboardMarkup chooseBranch(String lang, List<Branch> branches) {
        KeyboardRow row = new KeyboardRow();
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardButton requestLocationBtn = new KeyboardButton(
                lang.equals("uz") ? "📍 Eng yaqin filialni topish" : "📍 Найти ближайший филиал"
        );
        requestLocationBtn.setRequestLocation(true);
        row.add(requestLocationBtn);
        rows.add(row);
        row = new KeyboardRow();
        for (int i = 0; i < branches.size(); i++) {
            row.add(new KeyboardButton(branches.get(i).getName()));
            if ((i + 1) % 2 == 0) {
                rows.add(row);
                row = new KeyboardRow();
            }
        }
        rows.add(row);
        return markup(addBackAndMenuBtn(rows, lang));
    }

    private List<KeyboardRow> addBackAndMenuBtn(List<KeyboardRow> rows, String lang) {
        KeyboardRow row = new KeyboardRow();
        row.add(lang.equals("uz") ? backButton : backButtonRu);
        row.add(lang.equals("uz") ? mainMenu : mainMenuRu);
        rows.add(row);
        return rows;
    }
}
