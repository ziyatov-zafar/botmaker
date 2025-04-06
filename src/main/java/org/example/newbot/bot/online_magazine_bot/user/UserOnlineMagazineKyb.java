package org.example.newbot.bot.online_magazine_bot.user;

import org.example.newbot.bot.Kyb;
import org.example.newbot.dto.CartItemDto;
import org.example.newbot.model.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
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
        return setKeyboards(new String[]{lang.equals("uz") ? backButton : backButtonRu, lang.equals("uz") ? mainMenu : mainMenuRu}, 1);
    }

    public ReplyKeyboardMarkup menu(String lang) {
        String[] menu = menuBtn(lang);
        KeyboardRow row = new KeyboardRow();
        List<KeyboardRow> rows = new ArrayList<>();
        row.add(new KeyboardButton(menu[0]));
        rows.add(row);
        row = new KeyboardRow();
        row.add(new KeyboardButton(menu[1]));
        rows.add(row);
        row = new KeyboardRow();
        row.add(new KeyboardButton(lang.equals("uz") ? "🏢 Filiallar bo'limi" : "🏢 Отделы филиалов"));
        rows.add(row);
        row = new KeyboardRow();
        row.add(new KeyboardButton(menu[2]));
        row.add(new KeyboardButton(menu[3]));
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
        String[] mainBtn = {lang.equals("uz") ? backButton : backButtonRu, cardBtn(lang)};
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
        KeyboardButton requestLocationBtn = new KeyboardButton(lang.equals("uz") ? "📍 Eng yaqin filialni topish" : "📍 Найти ближайший филиал");
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


    public ReplyKeyboardMarkup setProductsUser(List<Product> list, String lang) {
        KeyboardRow row = new KeyboardRow();
        List<KeyboardRow> rows = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            row.add(new KeyboardButton(lang.equals("uz") ? list.get(i).getNameUz() : list.get(i).getNameRu()));
            if ((i + 1) % 2 == 0) {
                rows.add(row);
                row = new KeyboardRow();
            }
        }
        rows.add(row);
        row = new KeyboardRow();
        row.add(lang.equals("uz") ? backButton : backButtonRu);
        row.add(cardBtn(lang));
        rows.add(row);
        return markup(rows);
    }

    public ReplyKeyboardMarkup backAndBasket(String lang) {
        KeyboardRow row = new KeyboardRow();
        List<KeyboardRow> rows = new ArrayList<>();
        row.add(new KeyboardButton(lang.equals("uz") ? backButton : backButtonRu));
        row.add(new KeyboardButton(cardBtn(lang)));
        rows.add(row);
        return markup(rows);
    }

    public InlineKeyboardMarkup setProductVariant(String lang, List<ProductVariant> variants, ProductVariant selectedVariant, boolean isOne, int count) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // ➖➕ soni uchun tugmalar
        List<InlineKeyboardButton> quantityRow = new ArrayList<>();
        quantityRow.add(createButton("➖", "minus"));
        quantityRow.add(createButton("" + count, "count"));
        quantityRow.add(createButton("➕", "plus"));
        rows.add(quantityRow);

        // 🔘 Variant tanlash (agar bitta bo'lmasa)
        if (!isOne) {
            List<InlineKeyboardButton> variantRow = new ArrayList<>();
            for (int i = 0; i < variants.size(); i++) {
                ProductVariant variant = variants.get(i);
                String name = lang.equals("uz") ? variant.getNameUz() : variant.getNameRu();
                boolean isSelected = selectedVariant.getId().equals(variant.getId());

                variantRow.add(createButton((isSelected ? "✅ " : "") + name, variant.getId()));

                // Har 3 tadan keyin yangi qatorga o‘tish
                if ((i + 1) % 3 == 0) {
                    rows.add(variantRow);
                    variantRow = new ArrayList<>();
                }
            }
            if (!variantRow.isEmpty()) rows.add(variantRow);
        }

        // 📥 Savat tugmasi
        List<InlineKeyboardButton> cartRow = new ArrayList<>();
        cartRow.add(createButton(lang.equals("uz") ? cardBtn(lang) + "ga qo‘shish" : "Добавить в " + cardBtn(lang), "basket"));

        rows.add(cartRow);

        return new InlineKeyboardMarkup(rows);
    }

    public InlineKeyboardMarkup basket(String lang, List<CartItemDto> list) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        int i = 0;
        row.add(createButton(lang.equals("uz") ? deliveryContinue : deliveryContinueRu, "continueDelivery"));
        rows.add(row);
        row = new ArrayList<>();
        row.add(createButton(lang.equals("uz") ? "\uD83D\uDE9A Buyurtma berish" : "\uD83D\uDE9A Оформить заказ", "go delivery"));
        row.add(createButton(lang.equals("uz") ? "🗑 Savatni tozalash" : "🗑 Очистить корзину", "clear"));
        rows.add(row);
        row = new ArrayList<>();
        while (i < list.size()) {
            row.add(createButton("-", "minus_" + list.get(i).getId()));
            if (lang.equals("uz"))
                row.add(createButton(list.get(i).getProductNameUz() + "(%s)".formatted(list.get(i).getProductVariantNameUz()), list.get(i).getProductNameUz() + "(%s)".formatted(list.get(i).getProductVariantNameUz())));
            else
                row.add(createButton(list.get(i).getProductNameRu() + "(%s)".formatted(list.get(i).getProductVariantNameRu()), list.get(i).getProductNameRu() + "(%s)".formatted(list.get(i).getProductVariantNameRu())));
            row.add(createButton("+", "plus_" + list.get(i).getId()));
            rows.add(row);
            row = new ArrayList<>();
            i++;
        }
        return new InlineKeyboardMarkup(rows);
    }

    public InlineKeyboardMarkup choosePaymentType(String lang) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // To'lov turlari uchun tugmalar
        List<InlineKeyboardButton> row = new ArrayList<>();
        if (lang.equals("uz")) {
            row.add(createButton("💵 Naqd to‘lov", "pay_cash"));
            row.add(createButton("💳 Karta orqali", "pay_card"));
        } else if (lang.equals("ru")) {
            row.add(createButton("💵 Наличными", "pay_cash"));
            row.add(createButton("💳 Картой", "pay_card"));
        } else {
            row.add(createButton("💵 Cash", "pay_cash"));
            row.add(createButton("💳 By card", "pay_card"));
        }

        rows.add(row);

        // Bekor qilish tugmasi
        List<InlineKeyboardButton> cancelRow = new ArrayList<>();
        if (lang.equals("uz")) {
            cancelRow.add(createButton("❌ Bekor qilish", "cancel_payment"));
        } else {
            cancelRow.add(createButton("❌ Отмена", "cancel_payment"));
        }
        rows.add(cancelRow);

        return new InlineKeyboardMarkup(rows);
    }

    public InlineKeyboardMarkup successBasket(Long cartId) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Tugma uchun emoji qo'shish
        row.add(createButton("✅ Operatsiyani yakunlash", "finish_" + cartId)); // Tugmada ✔️ emoji

        rows.add(row);
        return new InlineKeyboardMarkup(rows);
    }

    public ReplyKeyboardMarkup successOrCancel(String lang) {
        // Tugmalar uchun ro'yxat yaratish
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        // Tilga qarab tugmalarni sozlash
        if (lang.equals("uz")) {
            row.add("✅ Tasdiqlash");
            row.add("❌ Bekor qilish");
        } else if (lang.equals("ru")) {
            row.add("✅ Подтверждение");
            row.add("❌ Отмена");
        } else {
            // Default fallback, agar til boshqa bo'lsa
            row.add("✅ Success");
            row.add("❌ Cancel");
        }

        // Tugmalarni ro'yxatga qo'shish
        keyboardRows.add(row);

        // ReplyKeyboardMarkup yaratish va tugmalarni qo'shish
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        replyKeyboardMarkup.setResizeKeyboard(true); // Klaviaturani moslashtirish
        replyKeyboardMarkup.setOneTimeKeyboard(true); // Foydalanuvchi klaviaturani yopishi mumkin

        return replyKeyboardMarkup;
    }

    public InlineKeyboardMarkup cancelBtn(String lang, Long cartId) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        row.add(createButton(
                lang.equals("uz") ? "❌ Bekor qilish" : "❌ Отменить",
                "cancelorder_" + cartId
        ));

        rows.add(row);
        return new InlineKeyboardMarkup(rows);
    }
}
