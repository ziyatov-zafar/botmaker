package org.example.newbot.bot.online_magazine_bot.user;

import org.example.newbot.bot.StaticVariable;
import org.example.newbot.dto.CartItemDto;
import org.example.newbot.model.Branch;
import org.example.newbot.model.CartItem;
import org.example.newbot.model.Product;
import org.example.newbot.model.ProductVariant;
import org.telegram.telegrambots.meta.api.objects.Location;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import static org.example.newbot.bot.StaticVariable.formatPrice;
import static org.example.newbot.bot.online_magazine_bot.user.BranchUtil.formatDistance;
import static org.example.newbot.bot.online_magazine_bot.user.BranchUtil.haversine;

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

    public String branchInformationWithDistance(String lang, Branch branch, Location location) {
        String branchInfo = branchInformation(lang, branch);
        double userLon = location.getLongitude();
        double userLat = location.getLatitude();
        double distanceInKm = haversine(userLat, userLon, branch.getLatitude(), branch.getLongitude());
        String formattedDistance = formatDistance(distanceInKm); // Masofani formatlash
        if (lang.equals("uz")) {
            return branchInfo + "\n📍 <b>Manzil:</b> " + branch.getAddress() + "\n" + "📏 Masofa: " + formattedDistance;
        } else {
            return branchInfo + "\n📍 <b>Адрес:</b> " + branch.getAddress() + "\n" + "📏 Расстояние: " + formattedDistance;
        }
    }

    public String branchInformation(String lang, Branch branch) {
        if (lang.equals("uz")) {
            return String.format("""
                            🏢 <b>%s</b> (%s)
                            📍 <b>Mo'ljal:</b> %s
                            🕒 <b>Ish vaqti:</b> %s
                            📞 <b>Telefon raqami:</b> %s
                            📍 <b>Manzil:</b> %s
                            """,
                    branch.getName(),
                    branch.getDescription(),
                    branch.getDestination(),
                    branch.getWorkingHours(),
                    branch.getPhone(),
                    branch.getAddress());
        } else {
            return String.format("""
                            🏢 <b>%s</b> (%s)
                            🎯 <b>Назначение:</b> %s
                            🕒 <b>Рабочие часы:</b> %s
                            📞 <b>Телефонный номер:</b> %s
                            📍 <b>Адрес:</b> %s
                            """,
                    branch.getName(),
                    branch.getDescription(),
                    branch.getDestination(),
                    branch.getWorkingHours(),
                    branch.getPhone(),
                    branch.getAddress());
        }
    }


    public String emptyProducts(String categoryName, String lang) {
        if (lang.equals("uz")) {
            return String.format("""
                    %s ning mahsulotlari tez orada joylanadi, boshqa kategoriyadagi mahsulotlarni kirib ko'rishingiz mumkin.""", categoryName);
        } else if (lang.equals("ru")) {
            return String.format("""
                    В категории %s товары скоро появятся, вы можете посмотреть товары в других категориях.""", categoryName);
        }
        return "";
    }

    public String productCaption(String lang, Product product, ProductVariant variant, int count) {
        // Narxni formatlash
        String price = formatPrice(variant.getPrice(), lang);
        String totalPrice = formatPrice(variant.getPrice() * count, lang);

        if (lang.equals("uz")) {
            return """
                    🍽 <b>%s - %s</b>
                    
                    📝 <i>%s</i>
                    💰 Narxi: %s
                    
                    %s(%s) * %d = %s
                    🔢 Umumiy narxi: %s
                    """.formatted(
                    product.getNameUz(),
                    variant.getNameUz(),
                    product.getDescriptionUz(),
                    price, product.getNameUz(), variant.getNameUz(), count, totalPrice,
                    totalPrice
            );
        } else if (lang.equals("ru")) {
            return """
                    🍽 <b>%s - %s</b>
                    
                    📝 <i>%s</i>
                    💰 Цена: %s
                    
                    %s(%s) * %d = %s
                    🔢 Общая цена: %s
                    """.formatted(
                    product.getNameRu(),
                    variant.getNameRu(),
                    product.getDescriptionRu(),
                    price, product.getNameUz(), variant.getNameUz(), count, totalPrice,
                    totalPrice
            );
        } else {
            // Default fallback
            return """
                    🍽 <b>%s - %s</b>
                    
                    📝 <i>%s</i>
                    💰 Price: %s
                    
                    🔢 Total Price: %s
                    """.formatted(
                    product.getNameUz(),
                    variant.getNameUz(),
                    product.getDescriptionUz(),
                    price,
                    totalPrice
            );
        }
    }


    private String formatPrice(double price, String lang) {
        Locale locale;
        String currency;

        switch (lang) {
            case "ru" -> {
                locale = new Locale("ru", "RU");
                currency = " сум";
            }
            case "uz" -> {
                locale = new Locale("uz", "UZ");
                currency = " so‘m";
            }
            default -> {
                locale = Locale.US;
                currency = " UZS";
            }
        }

        NumberFormat nf = NumberFormat.getInstance(locale);
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(0); // faqat kerakli hollarda .00 ni ko‘rsatadi

        return nf.format(price) + currency;
    }

    public String addBasketMsg(String lang) {
        if (lang.equals("uz")) {
            return "✅ Savatga muvaffaqiyatli qo‘shildi!";

        }
        return "✅ Успешно добавлено в корзину!";

    }

    public String basket(List<CartItemDto> carts, String lang) {
        StringBuilder s = new StringBuilder();
        double sum = 0D;

        for (CartItemDto dto : carts) {
            sum += dto.getQuantity() * dto.getPrice();
            if (lang.equals("uz")) {
                s.append(String.format(
                        "\uD83C\uDF0A %s (%s) * %d = 💰 %s\n\n",
                        dto.getProductNameUz(),
                        dto.getProductVariantNameUz(),
                        dto.getQuantity(),
                        formatPrice(dto.getPrice() * dto.getQuantity(), lang)
                ));
            } else if (lang.equals("ru")) {
                s.append(String.format(
                        "\uD83C\uDF0A %s (%s) * %d  = %s\n\n",
                        dto.getProductNameRu(),
                        dto.getProductVariantNameRu(),
                        dto.getQuantity(),
                        formatPrice(dto.getPrice() * dto.getQuantity(), lang)
                ));
            }
        }

        if (lang.equals("uz")) {
            s.append("\n\uD83D\uDCB3 Umumiy narxi: ").append(formatPrice(sum, lang));
        } else if (lang.equals("ru")) {
            s.append("\n\uD83D\uDCB3 Общая сумма: ").append(formatPrice(sum, lang));
        }

        return s.toString();
    }

}
