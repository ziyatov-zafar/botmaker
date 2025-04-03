package org.example.newbot.bot.roleadmin;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.newbot.bot.TelegramBot;
import org.example.newbot.dto.ResponseDto;
import org.example.newbot.model.BotInfo;
import org.example.newbot.model.BotPrice;
import org.example.newbot.model.User;
import org.example.newbot.repository.BotInfoRepository;
import org.example.newbot.service.BotPriceService;
import org.example.newbot.service.DynamicBotService;
import org.example.newbot.service.UserService;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.example.newbot.bot.StaticVariable.*;

@Controller
@Log4j2
@RequiredArgsConstructor
public class AdminFunction {
    public final TelegramBot bot;
    public final UserService userService;
    public final AdminKyb kyb;
    private final BotPriceService botPriceService;
    private final DynamicBotService dynamicBotService;
    private final TelegramBotsApi telegramBotsApi;
    private final BotInfoRepository botInfoRepository;

    public void start(User user) {
        String welcomeMessage = "🏠 Asosiy menyuga xush kelibsiz, " + user.getNickname() + "!\n\n"
                + "ℹ️ Quyidagi tugmalar yordamida kerakli bo'limni tanlang:\n"
                + "============================";

        bot.sendMessage(user.getChatId(), welcomeMessage, kyb.menu);
        eventCode(user, "menu"); // Foydalanuvchi holatini yangilash
    }

    private void eventCode(User user, String e) {
        user.setEventCode(e);
        userService.save(user);
    }

    public void menu(User user, String text) {
        if (text.equals(botMakerAdminMenu[0])) {

        } else if (text.equals(botMakerAdminMenu[1])) {
            bot.sendMessage(user.getChatId(), text, kyb.botMenu());
            eventCode(user, "bot menu");
        }

    }

    public void botMenu(User user, String text) {
        if (text.equals(backButton)) {
            start(user);
        } else if (text.equals(botMakerAdminBotMenu[0])) {

        } else if (text.equals(botMakerAdminBotMenu[1])) {

        } else if (text.equals(botMakerAdminBotMenu[2])) {
            List<BotPrice> list = botPriceService.findAll().getData();
            bot.sendMessage(user.getChatId(), "Quyidagi botlardan birini tanlang", kyb.getBots(list));
            eventCode(user, "get bot type");
        } else errorBtn(user.getChatId(), kyb.botMenu());
    }

    public void getBotType(User user, String text) {
        if (text.equals(backButton)) {
            menu(user, botMakerAdminMenu[1]);
        } else {
            ResponseDto<BotPrice> checkType = botPriceService.findByTypeText(text);
            if (checkType.isSuccess()) {
                BotPrice selectedBotPlan = checkType.getData();

                // Foydalanuvchi sozlamalarini yangilash
                user.setHelperBotType(selectedBotPlan.getType());
                userService.save(user);

                // Foydalanuvchiga token so'rash xabari
                String promptMessage = "🤖 Bot yaratish jarayoni boshlandi!\n"
                        + "Iltimos, @BotFather dan olingan bot tokenini yuboring:\n\n"
                        + "◽ Masalan: `1234567890:ABCdefGHIJKLmnopQRSTUVwxyz`";

                bot.sendMessage(
                        user.getChatId(),
                        promptMessage,
                        kyb.backBtn
                );
                eventCode(user, "get bot token");
            } else {
                List<BotPrice> list = botPriceService.findAll().getData();
                errorBtn(user.getChatId(), kyb.getBots(list));
            }
        }
    }

    private void errorBtn(Long chatId, ReplyKeyboardMarkup markup) {
        bot.sendMessage(chatId, "⚠️ Iltimos, quyidagi tugmalardan foydalaning:\n"
                + "⌨️ Tugmalarni bosish orqali amalni bajaring", markup);
    }

    public void getBotToken(User user, String text, Integer messageId) {
        if (text.equals(backButton)) {
            botMenu(user, botMakerAdminBotMenu[2]);
        } else {
            BotInfo checkBot = botInfoRepository.findByBotTokenAndActiveIsTrue(text);
            if (checkBot != null) {
                bot.sendMessage(user.getChatId(), "❌ Bu bot band, iltimos boshqa bot tokenini yuboring", kyb.backBtn);

                return;
            }
            bot.sendMessage(user.getChatId(),
                    """
                    ⏳ Jarayon davom etmoqda...
                
                    Bu biroz vaqt olishi mumkin. Iltimos, sabr qiling va orqaga suyanib kuting! ☕
                    """);

            int c = 100; // Progress uchun
            System.out.println("\n\n🔄 Jarayon boshlandi... \n\n");

            for (int i = 0; i <= 10000; i++) {
                if (i % 100 == 0) {
                    bot.editMessageText(user.getChatId(), messageId + 1,
                            "⚙️ Funksiyalar ulanmoqda... ⏳\n\nTayyor bo‘lishiga oz qoldi: " + c + "%");
                    c--;
                }
                System.out.print(i + " ");
            }

            BotInfo botInfo = new BotInfo();
            botInfo.setActive(true);
            botInfo.setBotToken(text);
            botInfo.setAdminChatIds(new ArrayList<>());
            botInfo.setAdminChatIds(List.of(user.getChatId()));
            botInfo.setType(user.getHelperBotType());
            botInfo.setIsFree(true);
            botInfo.setCreatedAt(new Date());
            botInfo.setUpdatedAt(new Date());
            String username = dynamicBotService.createAndRegisterBot(telegramBotsApi, botInfo,messageId,user.getChatId(), """
                    ⚙️ Funksiyalar ulandi... ⏳
                    
                    Jarayon yakuniga yetdi""",bot);
            if (username == null) {
                // Xatolik xabarini aniqroq qilish
                String errorMessage = "⚠️ *Xatolik!* Bot tokeni noto'g'ri yoki band.\n"
                        + "Buning sabablari:\n"
                        + "1️⃣ Token formati noto'g'ri (masalan: `1234567890:ABCdefGHIJKL...`)\n"
                        + "2️⃣ Token allaqachon tizimda mavjud\n"
                        + "3️⃣ BotFatherda ro'yxatdan o'tmagan";

                bot.sendMessage(
                        user.getChatId(),
                        errorMessage, kyb.backBtn
                );

                // Log qo'shish (qo'shimcha debug ma'lumoti uchun)
                log.warn("Noto'g'ri token kiritildi. User ID: {}", user.getId());

            } else {
                // Muvaffaqiyatli xabar + bot profiliga havola
                String successMessage = "✅ *Bot muvaffaqiyatli yaratildi!*\n"
                        + "Sizning botingiz: @" + username + "\n"
                        + "Botni ishga tushirish uchun: "
                        + "[@BotFather](https://t.me/BotFather) ga /start buyrug'ini yuboring";
                try {
                    bot.execute(
                            SendMessage
                                    .builder()
                                    .chatId(user.getChatId())
                                    .text(successMessage)
                                    .parseMode(ParseMode.MARKDOWN)
                                    .disableWebPagePreview(true)
                                    .build()
                    );
                } catch (Exception e) {
                    log.error(e);
                }
                // Keyingi menyu bosqichiga o'tish
                menu(user, botMakerAdminMenu[1]);
            }
        }
    }
}
