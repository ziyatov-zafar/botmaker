package org.example.newbot.bot.online_magazine_bot.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.newbot.bot.online_magazine_bot.admin.AdminOnlineMagazineKyb;
import org.example.newbot.model.BotInfo;
import org.example.newbot.model.BotUser;
import org.example.newbot.model.Location;
import org.example.newbot.repository.LocationRepository;
import org.example.newbot.service.*;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.List;

import static org.example.newbot.bot.StaticVariable.*;
import static org.example.newbot.bot.online_magazine_bot.user.ConstVariable.menuBtn;

@RequiredArgsConstructor
@Log4j2

public class UserOnlineMagazineFunction {
    private final BotUserService userService;
    private final DynamicBotService bot;
    private final UserOnlineMagazineKyb kyb;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final ProductVariantService productVariantService;
    private final UserOnlineMagazineMsg msg;
    private final LocationRepository locationRepository;

    public void reply(BotInfo botInfo, Long chatId, BotUser user, Long newChatId, Integer messageId, boolean isAdmin) {
        user.setChatIdHelp(newChatId);
        user.setEventCode("reply message");
        userService.save(user);
        String lang = user.getLang();
        bot.deleteMessage(botInfo.getId(), chatId, messageId);
        if (lang == null) lang = "uz";
        String message = (lang.equals("uz") || isAdmin) ? "📩 Xabaringizni yuboring" : "📩 Отправьте ваше сообщение";
        bot.sendMessage(botInfo.getId(), chatId, message, kyb.backBtn(lang));
    }

    public void replyMessage(BotInfo botInfo, BotUser user, String text) {
        String lang = user.getLang();
        if (inArray(text, backButton, backButtonRu)) {
            start(botInfo, user, false);

            return;
        }
        Long chatIdHelp = user.getChatIdHelp();
        BotUser botUser = userService.findByUserChatId(chatIdHelp, botInfo.getId()).getData();
        if (lang == null) lang = "uz";
        String username = botUser.getUsername() != null ? "@" + botUser.getUsername() : "—";
        bot.sendMessage(botInfo.getId(), chatIdHelp, """
                🆔 Foydalanuvchi ma'lumotlari:
                
                🏷️ Niki: %s
                🔗 Username: %s
                🆔 ID: %d
                💬 Chat ID: %d
                
                📝 Foydalanuvchi qoldirgan izoh:
                %s
                """.formatted(botUser.getNickname(), username, botUser.getId(), botUser.getChatId(), text), kyb.replyBtn(user.getChatId(), "uz"));
        bot.sendMessage(botInfo.getId(), user.getChatId(),
                lang.equals("uz") ? "✅ Xabaringiz muvaffaqiyatli yetkazildi" : "✅ Ваше сообщение успешно доставлено",
                kyb.backBtn(lang));

        start(botInfo, user, false);
    }

    private boolean inArray(String text, String s1, String s2) {
        return text.equals(s1) || text.equals(s2);
    }

    private boolean inArray(String text, String s1) {
        return text.equals(s1);
    }

    public void start(BotInfo botInfo, BotUser user, boolean isMenu) {
        Long botId = botInfo.getId();
        String lang = user.getLang();
        String eventCode;
        String s;
        ReplyKeyboardMarkup markup;
        if (isMenu || user.getLang() == null) {
            if (user.getLang() == null) s = msg.requestLang(user.getNickname());
            else s = msg.changeLang(lang);
            markup = kyb.requestLang;
            eventCode = "request_lang";
        } else if (user.getPhone() == null) {
            s = msg.requestContact(lang);
            markup = kyb.requestContact(msg.contactBtn(lang));
            eventCode = "request_contact";
        } else {
            s = msg.menu(user.getLang());
            markup = kyb.menu(lang);
            eventCode = "menu";
        }
        eventCode(user, eventCode);
        bot.sendMessage(botId, user.getChatId(), s, markup);
    }

    private void eventCode(BotUser user, String eventCode) {
        user.setEventCode(eventCode);
        userService.save(user);
    }

    public void requestLang(BotInfo botInfo, BotUser user, String text) {
        String lang;
        if (text.equals("\uD83C\uDDFA\uD83C\uDDFF O'zbek tili")) {
            lang = "uz";
        } else if (text.equals("\uD83C\uDDF7\uD83C\uDDFA Русский язык")) {
            lang = "ru";
        } else {
            wrongBtn(botInfo, user, kyb.requestLang);
            return;
        }
        user.setLang(lang);
        userService.save(user);
        start(botInfo, user, false);
    }

    public void requestContact(BotInfo botInfo, BotUser user, Contact contact) {
        if (contact.getUserId().equals(user.getChatId())) {
            String phone = contact.getPhoneNumber();
            if (phone.charAt(0) != '+') phone = "+" + phone;
            user.setPhone(phone);
            userService.save(user);
            start(botInfo, user, false);
        } else {
            wrongBtn(botInfo, user, kyb.requestContact(msg.contactBtn(user.getLang())));
        }
    }

    private void wrongBtn(BotInfo botInfo, BotUser user, ReplyKeyboardMarkup markup) {
        bot.sendMessage(botInfo.getId(), user.getChatId(), msg.errorBtn(user.getLang()), markup);
    }

    public void requestContact(BotInfo botInfo, BotUser user, String text) {
        wrongBtn(botInfo, user, kyb.requestContact(msg.contactBtn(user.getLang())));
    }

    public void menu(BotInfo botInfo, BotUser user, String text) {
        String[] menu = menuBtn(user.getLang());
        String lang = user.getLang();
        if (text.equals(menu[0])) {
            bot.sendMessage(botInfo.getId(), user.getChatId(), msg.deliveryType(lang), kyb.deliveryType(lang));
            eventCode(user, "deliveryType");
        } else if (text.equals(menu[1])) {

        } else if (text.equals(menu[2])) {
            bot.sendMessage(botInfo.getId(), user.getChatId(), msg.commentMsg(lang), kyb.backBtn(lang));
            eventCode(user, "commentToAdmin");
        } else if (text.equals(menu[3])) {
            start(botInfo, user, true);
        } else {
            wrongBtn(botInfo, user, kyb.menu(user.getLang()));
            return;
        }
    }

    public void commentToAdmin(BotInfo botInfo, BotUser user, String text) {
        if (inArray(text, backButton, backButtonRu)) {
            start(botInfo, user, false);
        } else {
            replyMessage(botInfo, user, text);
        }
    }

    public void deliveryType(BotInfo botInfo, BotUser user, String text) {
        String lang = user.getLang(), s, eventCode;
        String[] buttons = ConstVariable.deliveryType(lang);
        if (inArray(text, backButton, backButtonRu)) {
            start(botInfo, user, false);
            return;
        } else if (inArray(text, buttons[0])) {
            user.setDeliveryType("🚚 Yetkazib berish");
            user.setEventCode("chooseLocation");
            userService.save(user);
            bot.sendMessage(botInfo.getId(), user.getChatId(), msg.getLocation(lang), kyb.getLocation(lang));
        }

    }
}
