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

    public String locationList(String lang) {
        return lang.equals("uz")
                ? "📍 Yetkazib berish manzilini tanlang:"
                : "📍 Выберите адрес доставки:";
    }

    public String wrongLocation(String lang) {
        return lang.equals("uz")
                ? "❌ Lokatsiya faqat Oʻzbekiston ichida bo‘lishi kerak! Afsuski, boshqa davlatlarga xizmat ko‘rsatmaymiz."
                : "❌ Локация должна быть только в Узбекистане! К сожалению, мы не обслуживаем другие страны.";
    }

    public String emptyLocation(String lang) {
        return lang.equals("uz")
                ? "⚠️ Manzillaringiz ro‘yxati bo‘sh!"
                : "⚠️ Ваш список адресов пуст!";
    }

    public String isSuccessLocation(String lang, String address) {
        return lang.equals("uz")
                ? "<b>📍 Buyurtma bermoqchi bo‘lgan manzil:</b>\n<pre>%s</pre>\n\n✅ <i>Ushbu manzilni tasdiqlaysizmi?</i>".formatted(address)
                : "<b>📍 Адрес для оформления заказа:</b>\n<pre>%s</pre>\n\n✅ <i>Вы подтверждаете этот адрес?</i>".formatted(address);
    }


    public String categoryMenu(String lang) {
        return lang.equals("uz")
                ? "📋 O'zingizga kerakli menyulardan birini tanlang"
                : "📋 Выберите нужное меню";
    }

    public String emptyCategory(String lang) {
        return lang.equals("uz") ?
                "📦 Mahsulotlar mavjud emas 😕\nIltimos keyinroq urinib ko'ring. Mahsulotlar tez orada qo'shiladi 🔜📈." :
                lang.equals("ru") ?
                        "📦 Товары отсутствуют 😕\nПожалуйста, попробуйте позже. Продукты скоро будут добавлены 🔜📈." : "";
    }

    public String chooseBranch(String lang) {
        return lang.equals("uz") ?
                "📍 <b>Filialni tanlang:</b>\n\nO'zingizga qulay filialni tanlang yoki \"Eng yaqin filialni topish\" tugmasini bosing." :
                "📍 <b>Выберите филиал:</b>\n\nВыберите удобный для вас филиал или нажмите кнопку \"Найти ближайший филиал\".";
    }


    public String notFoundBranch(String lang, String btn) {
        return lang.equals("uz") ?
                "⚠️ <b>%s</b> bo‘limi hozirda ishlamayapti.\n\nTez orada ishga tushishi kutilmoqda. Siz <b>Buyurtma berish</b> menyusi orqali bemalol buyurtma berishingiz mumkin.".formatted(btn) :
                "⚠️ Раздел <b>%s</b> в настоящее время не работает.\n\nОжидается, что он скоро будет запущен. Вы можете сделать заказ через меню <b>Оформить заказ</b>.".formatted(btn);

    }
}
