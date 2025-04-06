package org.example.newbot.bot;

import org.example.newbot.model.Product;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

import static org.example.newbot.bot.StaticVariable.*;


public class Kyb {

    public ReplyKeyboardMarkup markup(List<KeyboardRow> r) {
        return ReplyKeyboardMarkup.builder().selective(true).resizeKeyboard(true).keyboard(r).build();
    }

    public ReplyKeyboardMarkup requestLang = setKeyboards(new String[]{"\uD83C\uDDFA\uD83C\uDDFF O'zbek tili", "\uD83C\uDDF7\uD83C\uDDFA Русский язык"}, 1);

    public ReplyKeyboardMarkup setKeyboards(String[] words, int size) {
        KeyboardButton button;
        KeyboardRow row = new KeyboardRow();
        List<KeyboardRow> rows = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {
            button = new KeyboardButton();
            button.setText(words[i]);
            row.add(button);
            if ((i + 1) % size == 0) {
                rows.add(row);
                row = new KeyboardRow();
            }
        }
        rows.add(row);
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        markup.setSelective(true);
        markup.setKeyboard(rows);
        return markup;
    }

    public ReplyKeyboardMarkup requestContact(String word) {
        KeyboardButton button;
        KeyboardRow row = new KeyboardRow();
        List<KeyboardRow> rows = new ArrayList<>();
        button = new KeyboardButton();
        button.setText(word);
        button.setRequestContact(true);
        row.add(button);
        rows.add(row);
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        markup.setSelective(true);
        markup.setKeyboard(rows);
        return markup;
    }

    public ReplyKeyboardMarkup requestContact(String word, String lang) {
        return setKeyboards(new String[]{word, lang.equals("uz")?backButton:backButtonRu}, 1);
    }


    public InlineKeyboardButton createButton(String text, String data) {
        return InlineKeyboardButton.builder().callbackData(data).text(text).build();
    }

    public InlineKeyboardButton createButton(String text, Long data) {
        return InlineKeyboardButton.builder().callbackData(String.valueOf(data)).text(text).build();
    }


    public ReplyKeyboardMarkup isSuccess(String lang) {
        if (lang.equals("uz")) {
            return setKeyboards(new String[]{"✅ Ha", "❌ Yo'q"}, 2);
        } else return setKeyboards(new String[]{"«✅ Да»", "«❌ Нет»"}, 2);
    }

    public InlineKeyboardMarkup isSuccessBtn(String lang) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        if (lang.equals("uz")) {
            row.add(createButton("✅ Ha", "yes delete"));
            rows.add(row);
            row = new ArrayList<>();
            row.add(createButton("❌ Yo'q", "no delete"));

        } else {
            row.add(createButton("«✅ Да»", "yes delete"));
            rows.add(row);
            row = new ArrayList<>();
            row.add(createButton("«❌ Нет»", "no delete"));

        }
        rows.add(row);
        return new InlineKeyboardMarkup(rows);

    }

    public ReplyKeyboardMarkup backBtn = setKeyboards(new String[]{backButton, mainMenu}, 2);

    public ReplyKeyboardMarkup backBtn(String lang) {
        return lang.equals("uz") ? setKeyboards(new String[]{backButton, mainMenu}, 2) : setKeyboards(new String[]{backButtonRu, mainMenuRu}, 2);
    }

    public ReplyKeyboardMarkup setProducts(List<Product> products, String lang) {
        KeyboardRow row = new KeyboardRow();
        List<KeyboardRow> rows = new ArrayList<>();
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            row.add(new KeyboardButton(lang.equals("uz") ? product.getNameUz() : product.getNameRu()));
            if ((i + 1) % 2 == 0) {
                rows.add(row);
                row = new KeyboardRow();
            }
        }
        rows.add(row);
        row = new KeyboardRow();
        row.add(new KeyboardButton(addProduct));
        rows.add(row);
        row = new KeyboardRow();
        row.add(new KeyboardButton(backButton));
        row.add(new KeyboardButton(mainMenu));
        rows.add(row);
        return markup(rows);
    }

    public InlineKeyboardMarkup replyBtn(Long chatId, String lang) {
        InlineKeyboardButton replyButton = createButton(lang.equals("uz") ? "✍️ Javob yozish" : "✍️ Напишите ответ", "reply_%d".formatted(chatId));
        List<List<InlineKeyboardButton>> rows = List.of(List.of(replyButton));
        return new InlineKeyboardMarkup(rows);
    }

}
