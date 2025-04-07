package org.example.newbot.bot.roleuser;

import lombok.extern.log4j.Log4j2;
import org.example.newbot.bot.Function;
import org.example.newbot.bot.StaticVariable;
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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

import static org.example.newbot.bot.StaticVariable.*;

@Controller
@Log4j2
public class UserFunction extends Function {
    public final TelegramBot bot;
    public final UserService userService;
    public final UserKyb kyb;
    private final BotPriceService botPriceService;
    private final DynamicBotService dynamicBotService;
    private final TelegramBotsApi telegramBotsApi;
    private final BotInfoRepository botInfoRepository;

    public UserFunction(TelegramBot bot, UserService userService, UserKyb kyb, BotPriceService
            botPriceService, DynamicBotService dynamicBotService, TelegramBotsApi telegramBotsApi, BotInfoRepository botInfoRepository) {
        super(userService);
        this.bot = bot;
        this.userService = userService;
        this.kyb = kyb;
        this.botPriceService = botPriceService;
        this.dynamicBotService = dynamicBotService;
        this.telegramBotsApi = telegramBotsApi;
        this.botInfoRepository = botInfoRepository;
    }

    public void start(User user) {
        if (user.getIsNew() == null) user.setIsNew(true);
        try {
            // Foydalanuvchi holatini yangilash
            eventCode(user, "menu");

            // Xabar matnini tayyorlash
            String welcomeMessage;
            if (user.getIsNew()) {
                welcomeMessage = """
                        🖐 Salom hurmatli %s. « %s » ga xush kelibsiz
                        
                        Super Maker Bot | Konstruktor sizga telegram tarmog'ida 
                        mukammal telegram bot yaratish imkoniyatini beradi.
                        """.formatted(
                       user.getNickname() ,bot.getBotUsername()
                );
                user.setIsNew(false);
                userService.save(user);
            } else {
                welcomeMessage = """
                        O'zingizga kerakli menyulardan birini tanlang
                        """;
            }

            // Xabarni yuborish
            bot.sendMessage(
                    user.getChatId(),
                    welcomeMessage,
                    kyb.menu() // Formatlash uchun
            );

        } catch (Exception e) {
            // Xatolikni logga yozish
            log.error("Start command error for user {}: {}", user.getUserId(), e.getMessage());

            // Foydalanuvchiga xato xabarini yuborish
            bot.sendMessage(
                    user.getChatId(),
                    "⚠️ Botda texnik nosozlik yuz berdi. Iltimos, keyinroq urinib ko'ring."
            );
        }
    }

    // Telegram markdown belgilarini ekranlash uchun yordamchi metod

    public void menu(User user, String text) {
        if (text.equals(menu[0])) {
            ResponseDto<List<BotPrice>> all = botPriceService.findAll();
            bot.sendMessage(user.getChatId(),
                    "O'zingizga kerakli botni tanlang. 😊 Endi siz o'zingiz uchun mukammal" +
                            " botni yaratishga tayyormisiz? Tanlovni qiling va biz birgalikda noyob bot yaratish safarini boshlaymiz! Qani kettik 🚀", kyb.setBotTypes(all.getData()));
            eventCode(user, "choose bot");
        } else {
            wrongBtn(user.getChatId(), kyb.menu());
        }
    }

    private void wrongBtn(Long chatId, ReplyKeyboardMarkup m) {
        bot.sendMessage(chatId, "❌ Iltimos, tugmalardan foydalaning", m);
    }

    public void chooseBot(User user, String text) {
        if (text.equals(backButton)) {
            start(user);
        } else {
            ResponseDto<List<BotPrice>> list = botPriceService.findAll();
            ResponseDto<BotPrice> checkType = botPriceService.findByTypeText(text);
            if (checkType.isSuccess()) {
                BotPrice botPrice = checkType.getData();
                user.setBotPriceId(botPrice.getId());
                userService.save(user);
                bot.sendMessage(user.getChatId(), aboutBotPrice(botPrice, false) + "\n\n\uD83E\uDD16 **Ushbu Telegram Botni yaratmoqchimisiz?**\n", kyb.isBuyBot);
                eventCode(user, "is buy bot");
            } else {
                wrongBtn(user.getChatId(), kyb.setBotTypes(list.getData()));
            }
        }
    }

    public void isBuyBot(User user, String text) {
        if (text.equals(backButton)) {
            menu(user, StaticVariable.menu[0]);
        } else if (text.equals("\uD83D\uDEE0 Yaratish")) {
            if (user.getBalance() >= botPrice(user).getPrice()) {
                bot.sendMessage(user.getChatId(), """
                        <b>🤖 Yaratmoqchi bo'lgan botingizni tokenini yuboring:</b>
                        
                        Bot tokenini olish uchun, avvalo <a href="https://t.me/BotFather">BotFather</a> ga o'ting va /newbot buyrug'ini yuboring.
                        
                        <i>📝 Tokenni yuboring:</i>""");
            } else {
                bot.sendMessage(user.getChatId(), """
                        ⚠️ **Afsuski, hisobingizdagi mablag' yetarli emas.**
                        
                        Hisobingizni to'ldirish uchun "💳 Hisobni to'ldirish" tugmasini bosing.
                        """, kyb.setKeyboards(new String[]{"💳 Hisobni to'ldirish", backButton}, 1));
            }
        } else if (text.equals("❌ Bekor qilish")) {
            menu(user, StaticVariable.menu[0]);
        } else {
            BotInfo botInfo = new BotInfo();
            botInfo.setBotToken(text);
            botInfo.setType(botPrice(user).getType());
            botInfo.setActive(true);
            botInfo.setAdminChatIds(List.of(user.getChatId(), bot.superAdminChatId));
            botInfo.setIsFree(false);
            botInfo.setUpdatedAt(getTashekntDate());
            botInfo.setCreatedAt(getTashekntDate());
            bot.sendMessage(user.getChatId(), """
                    <b>🤖 Bot yaratilmoqda:</b>
                    
                    Yangi bot yaratish jarayoni boshlangan. Iltimos, funksiyalar ulanayotganini kuting...
                    
                    <i>⚙️ Iltimos, kuting...</i>
                    """);
            String username = dynamicBotService.createAndRegisterBot(telegramBotsApi, botInfo);
            if (username != null) {
                user.setBalance(user.getBalance() - botPrice(user).getPrice());
                userService.save(user);
                bot.sendMessage(user.getChatId(), """
                        <b>✅ Bot muvaffaqiyatli yaratildi!</b>
                        
                        Yangi botingiz: <b>@%s</b>
                        
                        <i>🔧 Funksiyalar muvaffaqiyatli ulanib bo'ldi. Endi botni ishlatish mumkin.</i>
                        """.formatted(username));
                start(user);
            } else {
                bot.sendMessage(user.getChatId(), """
                        <b>❗️ Kutilmagan xatolik!</b>
                        
                        Yaratilgan token bilan bog'liq muammo yuz berdi. Iltimos, boshqa token kiriting.
                        
                        <i>🔄 Tokenni qayta tekshirib, to'g'ri kiritganingizni tasdiqlang.</i>
                        """);

            }
        }
    }

    private BotPrice botPrice(User user) {
        return botPriceService.findById(user.getBotPriceId()).getData();
    }

    private Date getTashekntDate() {
        ZoneId uzbekistanZone = ZoneId.of("Asia/Tashkent");
        ZonedDateTime nowInTashkent = ZonedDateTime.now(uzbekistanZone);
        return Date.from(nowInTashkent.toInstant());
    }
}
