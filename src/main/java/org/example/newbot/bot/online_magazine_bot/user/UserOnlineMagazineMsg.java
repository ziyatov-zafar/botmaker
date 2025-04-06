package org.example.newbot.bot.online_magazine_bot.user;

import org.example.newbot.dto.CartItemDto;
import org.example.newbot.model.*;
import org.telegram.telegrambots.meta.api.objects.Location;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

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

    public String contactBtn1(String lang) {
        if (lang.equals("uz")) {
            return "📲 🇺🇿 Telefon raqamni ulashish";  // O'zbek tilida
        }
        return "📲 🇷🇺 Поделиться номером телефона";  // Rus tilida "Telefon raqamni ulashish"
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

    public String clearBasket(String lang) {
        if (lang.equals("uz")) {
            return "🧺 Savat tozalandi. Yangi mahsulotlar qo‘shishingiz mumkin ✅";
        } else {
            return "🧺 Корзина очищена. Вы можете добавить новые товары ✅";
        }
    }

    public String emptyBasket(String lang) {
        if (lang.equals("uz")) {
            return "🧺 Savat hozircha bo‘sh. Mahsulotlar qo‘shib ko‘ring!";
        } else if (lang.equals("ru")) {
            return "🧺 Ваша корзина пока пуста. Попробуйте добавить товары!";
        } else {
            return "🧺 Your basket is currently empty. Try adding some products!";
        }
    }


    public String choosePaymentType(String lang) {
        if (lang.equals("uz")) {
            return "\uD83D\uDCB3 To‘lov turini tanlang:";
        } else if (lang.equals("ru")) {
            return "\uD83D\uDCB3 Выберите способ оплаты:";
        } else {
            return "\uD83D\uDCB3 Please choose a payment method:";
        }
    }

    public String getPhoneNumber(String lang) {
        if (lang.equals("uz")) {
            return "📱 Telefon raqamingizni quyidagi formatda yuboring yoki kiriting: +998 ** *** ** **";
        } else if (lang.equals("ru")) {
            return "📱 Пожалуйста, введите или отправьте свой номер телефона в следующем формате: +998 ** *** ** **";
        }
        return "";
    }

    public String invalidPhoneNumber(String lang) {
        if (lang.equals("uz")) {
            return "Iltimos, telefon raqamingizni to'g'ri formatda kiriting: +998 ** *** ** **";
        } else if (lang.equals("ru")) {
            return "Пожалуйста, введите ваш номер телефона в правильном формате: +998 ** *** ** **";
        } else {
            return "Please enter your phone number in the correct format: +998 ** *** ** **";
        }
    }

    public String basketForAdmin(List<CartItemDto> carts, String paymentType, String deliveryType, Branch branch, BotUser user, Long cartId) {
        StringBuilder s = new StringBuilder(); // StringBuilder - samaraliroq va tejamkorroq
        double sum = 0;
        s.append("🆔 ID: ").append(cartId).append("\n");

        // Har bir mahsulot uchun ma'lumotlarni formatlash
        for (CartItemDto cart : carts) {
            String productName = cart.getProductNameUz();
            String productVariantName = cart.getProductVariantNameUz();
            String categoryName = cart.getCategoryNameUz();
            int count = cart.getQuantity();
            double price = cart.getPrice();
            double totalPrice = price * count;
            sum += totalPrice;

            // Mahsulot ma'lumotlarini formatlash
            s.append(String.format("""
                    🛒 Kategoriya nomi: %s
                    🍽 Mahsulot nomi: %s
                    🧳 Mahsulot turi: %s
                    🔢 Mahsulotlar soni: %d
                    💵 Mahsulot narxi: %s
                    💰 Narxi: %s
                    
                    """, categoryName, productName, productVariantName, count, formatPrice(price, "uz"), formatPrice(totalPrice, "uz")));
        }


        s.append(String.format("""
                        📊 Umumiy narx: %s
                        💳 To'lov turi: %s
                        🚚 Yetkazib berish turi: %s
                        
                        %s
                        """, formatPrice(sum, "uz"), paymentType,
                deliveryType.equals("delivery") ? "Yetkazib berish kerak" : "O'zi olib ketadi", deliveryType.equals("delivery") ? "\uD83D\uDCCD Ushbu foydalanuvchiga eng yaqin filial: " + branch.getName() : "\uD83C\uDFE2 Foydalanuvchi tanlagan filial: " + branch.getName()));

        s.append(String.format(
                """
                        \n
                        
                          Foydalanuvchining ma'lumotlari:
                        🆔 Foydalanuvchi ID: %d
                        💬 Foydalanuvchi Chat ID: %d
                        📱 Foydalanuvchi telefon raqami: %s
                        📲 Telegramdagi telefon raqami: %s
                        """, user.getId(), user.getChatId(), user.getHelperPhone(), user.getPhone()
        ));
        return s.toString(); // Natijani qaytarish
    }

    public String finishBasket(List<CartItemDto> carts, String lang, Long cartId, String address, BotUser user, Branch branch) {
        StringBuilder s = new StringBuilder();

        double sum = 0;
        String deliveryType = user.getDeliveryType();
        String paymentType = user.getLang().equals("uz") ? user.getPaymentTypeUz() : user.getPaymentTypeRu();

        if (lang.equals("uz")) {
            // Manzil va buyurtma holati
            if (deliveryType.equals("delivery")) {
                s.append(String.format("""
                        🛵 Buyurtma raqami: %d
                        📍 Manzil: %s
                        🛑 Holat: Yangi
                        """, cartId, address));
            } else {
                s.append(String.format("""
                        🛵 Buyurtma raqami: %d
                        📍 Manzil: %s
                        🏢 Filial: %s
                        """, cartId, address, branch.getName()));
            }

            // Mahsulotlar ro'yxati
            for (CartItemDto cart : carts) {
                String productName = cart.getProductNameUz();
                int count = cart.getQuantity();
                double price = cart.getPrice();
                double totalPrice = price * count;
                sum += totalPrice;
                // Mahsulot nomi va soni
                s.append(String.format("""
                        📦 %d * %s
                        💸 Narxi: %s
                        """, count, productName, formatPrice(price, "uz")));
            }

            // Jami narx va to'lov turi
            s.append(String.format("""
                    \n\n💰 Jami: %s
                    \n💳 To'lov turi: %s
                    """, formatPrice(sum, "uz"), paymentType));
        } else {
            // Rus tilidagi matnni to'ldirish uchun
            if (deliveryType.equals("delivery")) {
                s.append(String.format("""
                        🛵 Заказ номер: %d
                        📍 Адрес: %s
                        🛑 Статус: Новый
                        """, cartId, address));
            } else {
                s.append(String.format("""
                        🛵 Заказ номер: %d
                        📍 Адрес: %s
                        🏢 Филиал: %s
                        """, cartId, address, branch.getName()));
            }

            // Mahsulotlar ro'yxati
            for (CartItemDto cart : carts) {
                String productName = cart.getProductNameRu();
                int count = cart.getQuantity();
                double price = cart.getPrice();
                double totalPrice = price * count;
                sum += totalPrice;

                // Mahsulot nomi va soni
                s.append(String.format("""
                        📦 %d * %s
                        💸 Цена: %s
                        """, count, productName, formatPrice(price, "ru")));
            }

            // Jami narx va to'lov turi
            s.append(String.format("""
                    \n\n💰 Общая сумма: %s
                    \n💳 Тип оплаты: %s
                    """, formatPrice(sum, "ru"), paymentType));
        }

        return s.toString();
    }


    public String branchLists(String lang) {
        if (lang.equals("uz")) {
            return "🏢 Barcha filiallarning ro'yxati";
        } else if (lang.equals("ru")) {
            return "🏢 Список всех филиалов";
        }
        return "";
    }

    public String myOrders(Cart cart, List<CartItemDto> list, String lang , Branch branch) {
        double sum = 0;
        StringBuilder s = new StringBuilder();

        if (lang.equals("uz")) {
            for (CartItemDto dto : list) {
                s.append("""
                        📦 %d * %s = 💸 %s
                        """.formatted(dto.getQuantity(), dto.getProductNameUz(), formatPrice(dto.getPrice() * dto.getQuantity(), "uz")));
                sum += dto.getQuantity() * dto.getPrice();
            }

            return """
                    🧾 Buyurtma raqami: %d
                    %s
                    
                    %s
                    
                    💳 To'lov turi: %s
                    💰 Jami: %s
                    """.formatted(cart.getId(), branch == null ? "📍 Manzil: %s".formatted(cart.getAddress()):"📍 Filial: %s dan olib ketishingiz kerak".formatted(branch.getName()), s, cart.getPaymentTypeUz(), formatPrice(sum, "uz"));
        } else {
            for (CartItemDto dto : list) {
                s.append("""
                        📦 %d * %s = 💸 %s
                        """.formatted(dto.getQuantity(), dto.getProductNameRu(), formatPrice(dto.getPrice() * dto.getQuantity(), "ru")));
                sum += dto.getQuantity() * dto.getPrice();
            }

            return """
                    🧾 Номер заказа: %d
                    📍 Адрес: %s
                    
                    %s
                    
                    💳 Тип оплаты: %s
                    💰 Общая сумма: %s
                    """.formatted(cart.getId(), cart.getAddress(), s, cart.getPaymentTypeRu(), formatPrice(sum, "ru"));
        }
    }


    public String emptyOrders(String lang) {
        if (lang.equals("uz")) {
            return "📭 Faol buyurtmalaringiz mavjud emas";
        }
        return "📭 У вас нет активных заказов";
    }

    public String cancelOrder(Cart cart, List<CartItemDto> list, BotUser user, Branch branch) {
        double sum = 0;
        StringBuilder s = new StringBuilder("❌ *Buyurtma bekor qilindi!*\n\n");

        for (CartItemDto dto : list) {
            s.append("""
                    📦 %d × %s  
                    💸 Narxi: %s\n
                    """.formatted(dto.getQuantity(), dto.getProductNameUz(), formatPrice(dto.getPrice() * dto.getQuantity(), "uz")));
            sum += dto.getQuantity() * dto.getPrice();
        }

        String username = user.getUsername() != null ? "@" + user.getUsername() : "❌ Mavjud emas";

        return """
                🧾 *Buyurtma raqami:* %d
                %s
                
                %s
                
                💳 *To'lov turi:* %s
                💰 *Jami:* %s
                
                👤 *Foydalanuvchi ma'lumotlari:*
                🔢 ID: %d
                💬 Chat ID: %d
                🧑‍💼 Nickname: %s
                🔗 Username: %s
                📱 Telegram raqam: %s
                ☎️ Buyurtma qoldirilgan raqam: %s
                """.formatted(
                cart.getId(),
                branch == null ? "\uD83D\uDCCD *Manzil:* %s".formatted(cart.getAddress()) : "\uD83D\uDCCD *Filial:* %s dan olib ketadi".formatted(branch.getName()),
                s,
                cart.getPaymentTypeUz(),
                formatPrice(sum, "uz"),
                user.getId(),
                user.getChatId(),
                user.getNickname(),
                username,
                user.getPhone(),
                cart.getPhone()
        );
    }

    public String alertMsgForCancelOrder(String lang) {
        if (lang.equals("uz")) {
            return "❌ *Buyurtma bekor qilindi!*";
        } else if (lang.equals("ru")) {
            return "❌ *Заказ был отменён!*";
        }
        return "❌ Order canceled!";
    }

}
