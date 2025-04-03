package org.example.newbot.bot.online_magazine_bot.user;

public class UserOnlineMagazineMsg {
    public String requestLang(String nickname) {
        return """
                🇺🇿 Assalomu alaykum, hurmatli <b>%s</b>!
                Iltimos, o‘zingizga qulay tilni tanlang. ⬇️
                
                🇷🇺 Привет, уважаемый <b>%s</b>!
                Пожалуйста, выберите удобный вам язык. ⬇️
                """.formatted(nickname, nickname);
    }

    public String requestContact(String lang) {
        return lang.equals("uz") ? """
                🇺🇿 Botdan foydalanish uchun ro‘yxatdan o‘tishingiz kerak. 
                Iltimos, kontaktingizni yuboring. 📲
                """ : """
                🇷🇺 Чтобы воспользоваться ботом, вам необходимо зарегистрироваться.
                Пожалуйста, отправьте свой контакт. 📲
                """;
    }

    public String contactBtn(String lang) {
        if (lang.equals("uz")) return "📲 🇺🇿 Ro‘yxatdan o‘tish";
        return "📲 🇷🇺 Регистрация";
    }

    public String menu(String lang) {
        return lang.equals("uz") ? "🏠 Asosiy menyudasiz" : "🏠 Вы находитесь в главном меню";
    }

    public String errorBtn(String lang) {
        return lang.equals("uz") ? "❗ Iltimos, tugmalardan foydalaning" : "❗ Пожалуйста, используйте кнопки";
    }

    public String changeLang(String lang) {
        if (lang.equals("uz")) {
            return "🌍 O'zingizga kerakli tilni tanlang";
        } else if (lang.equals("ru")) {
            return "🌍 Выберите нужный язык";
        }
        return "";
    }

    public String commentMsg(String lang) {
        if (lang.equals("uz")) {
            return "💬 Fikringizni qoldiring, sizning fikringiz biz uchun muhim";
        } else if (lang.equals("ru")) {
            return "💬 Оставьте ваш комментарий, ваше мнение для нас важно";
        }
        return "";
    }

    public String deliveryType(String lang) {
        return lang.equals("uz") ? "🚚 Yetkazib berish turini tanlang" : "🚚 Выберите способ доставки";
    }

    public String getLocation(String lang) {
        return lang.equals("uz") ? "📍 Geolokatsiyani yuboring yoki yetkazib berish manzilini tanlang" : "📍 Отправьте геолокацию или выберите адрес доставки";
    }
}
