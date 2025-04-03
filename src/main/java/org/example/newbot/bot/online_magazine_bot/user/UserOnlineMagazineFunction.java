package org.example.newbot.bot.online_magazine_bot.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.newbot.bot.online_magazine_bot.admin.AdminOnlineMagazineKyb;
import org.example.newbot.model.BotInfo;
import org.example.newbot.model.BotUser;
import org.example.newbot.service.*;

import static org.example.newbot.bot.StaticVariable.*;

@RequiredArgsConstructor
@Log4j2

public class UserOnlineMagazineFunction {
    private final BotUserService userService;
    private final DynamicBotService bot;
    private final UserOnlineMagazineKyb kyb;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final ProductVariantService productVariantService;

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
            //start funksiyasi yoziladi

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
                """.formatted(
                botUser.getNickname(),
                username,
                botUser.getId(),
                botUser.getChatId(),
                text
        ), kyb.replyBtn(user.getChatId(), "uz"));
        bot.sendMessage(botInfo.getId(), user.getChatId(), lang.equals("uz") ? "Xabaringiz muvaffaqiyatli yetkazildi" : "Ваше сообщение успешно доставлено", kyb.backBtn(lang));
        //start funksiyasi yoziladi
    }

    private boolean inArray(String text, String s1, String s2) {
        return text.equals(s1) || text.equals(s2);
    }
}
