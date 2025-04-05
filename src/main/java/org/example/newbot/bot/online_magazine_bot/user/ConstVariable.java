package org.example.newbot.bot.online_magazine_bot.user;

import static org.example.newbot.bot.StaticVariable.*;

public class ConstVariable {
    public static String[] menuBtn(String lang) {
        return lang.equals("uz") ? new String[]{
                "🛍️ Buyurtma berish",
                "\uD83D\uDECD Mening buyurtmalarim",
                "✍️ Fikr bildirish",
                "⚙️ Sozlamalar"
        } : new String[]{
                "🛍️ Сделать заказ",
                "\uD83D\uDECD Мои заказы",
                "✍️ Оставить отзыв",
                "⚙️ Настройки"
        };
    }

    public static String[] deliveryType(String lang) {
        return lang.equals("uz") ? new String[]{
                "🚚 Yetkazib berish", "🏠 Olib ketish", backButton, mainMenu
        } : new String[]{
                "🚚 Доставка", "🏠 Самовывоз", backButtonRu, mainMenuRu
        };
    }

    public static String[] location(String lang) {
        if (lang.equals("uz")) {
            return new String[]{
                    "🏢 Mening manzillarim", "📍 Joylashuvni yuborish", backButton, mainMenu
            };
        } else {
            return new String[]{
                    "🏢 Мои адреса", "📍 Отправить геолокацию", backButtonRu, mainMenuRu
            };
        }
    }



}

