package org.example.newbot.bot.roleuser;

import lombok.extern.log4j.Log4j2;
import org.example.newbot.bot.*;
import org.example.newbot.dto.ResponseDto;
import org.example.newbot.model.*;
import org.example.newbot.repository.BotInfoRepository;
import org.example.newbot.repository.BotPriceRepository;
import org.example.newbot.repository.ChannelRepository;
import org.example.newbot.repository.PaymentRepository;
import org.example.newbot.service.BotPriceService;
import org.example.newbot.service.DynamicBotService;
import org.example.newbot.service.UserService;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

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
    private final ChannelRepository channelRepository;
    private final BotPriceRepository botPriceRepository;
    private final PaymentRepository paymentRepository;

    public UserFunction(TelegramBot bot, UserService userService, UserKyb kyb, BotPriceService botPriceService, DynamicBotService dynamicBotService, TelegramBotsApi telegramBotsApi, BotInfoRepository botInfoRepository, ChannelRepository channelRepository, BotPriceRepository botPriceRepository, PaymentRepository paymentRepository) {
        super(userService);
        this.bot = bot;
        this.userService = userService;
        this.kyb = kyb;
        this.botPriceService = botPriceService;
        this.dynamicBotService = dynamicBotService;
        this.telegramBotsApi = telegramBotsApi;
        this.botInfoRepository = botInfoRepository;
        this.channelRepository = channelRepository;
        this.botPriceRepository = botPriceRepository;
        this.paymentRepository = paymentRepository;
    }


    public void updateChannel(User user, TelegramBot bot, Kyb kyb) {
        bot.sendMessage(user.getChatId(), "Quyidagi kanallarga to'liq azo boling", kyb.subscribeChannel(bot.getChannels(user.getChatId()), user.getLang()));
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
                        """.formatted(user.getNickname(), bot.getBotUsername());
                user.setIsNew(false);
                userService.save(user);
            } else {
                welcomeMessage = """
                        O'zingizga kerakli menyulardan birini tanlang
                        """;
            }

            // Xabarni yuborish
            bot.sendMessage(user.getChatId(), welcomeMessage, kyb.menu() // Formatlash uchun
            );

        } catch (Exception e) {
            // Xatolikni logga yozish
            log.error("Start command error for user {}: {}", user.getUserId(), e.getMessage());

            // Foydalanuvchiga xato xabarini yuborish
            bot.sendMessage(user.getChatId(), "⚠️ Botda texnik nosozlik yuz berdi. Iltimos, keyinroq urinib ko'ring.");
        }
    }

    // Telegram markdown belgilarini ekranlash uchun yordamchi metod

    public void menu(User user, String text) {
        if (text.equals(menu[0])) {
            ResponseDto<List<BotPrice>> all = botPriceService.findAll();
            bot.sendMessage(user.getChatId(), "O'zingizga kerakli botni tanlang. 😊 Endi siz o'zingiz uchun mukammal" + " botni yaratishga tayyormisiz? Tanlovni qiling va biz birgalikda noyob bot yaratish safarini boshlaymiz! Qani kettik 🚀", kyb.setBotTypes(all.getData()));
            eventCode(user, "choose bot");
        } else if (text.equals(menu[1])) {
            List<BotInfo> bots = myBots(user.getChatId());
            if (bots.isEmpty()) {
                bot.sendMessage(user.getChatId(), "⚠️ Sizda faol botlar mavjud emas", kyb.menu());
            } else {
                String a = "";
                int c = 0;
                for (BotInfo botInfo : bots) {
                    c++;
                    a = a.concat(c + ") 🤖 Bot manzili: @%s\n".formatted(botInfo.getBotUsername()));
                }
                bot.sendMessage(user.getChatId(), """
                        📋 Sizda faol botlar soni: %d ta
                        
                        %s
                        """.formatted(bots.size(), a), kyb.bots(myBots(user.getChatId())));
            }
        } else if (text.equals(menu[2])) {
            Payment payment = payment();
            bot.sendPhoto(user.getChatId(), payment.getImg(), """
                    💸 Karta: <code>%s</code>
                    🧾 Karta egasining ismi: %s
                    💳 Turi: %s
                    
                    Almashuvingiz muvaffaqiyatli bajarilishi uchun quyidagi harakatlarni amalga oshiring:
                    1️⃣ Istalgan pul miqdorini tepadagi hamyonga tashlang
                    2️⃣ «✅ To'lov qildim» tugmasini bosing;
                    """.formatted(payment.getNumber(), payment.getOwner(), payment.getType()), kyb.paymentKyb());
            eventCode(user, "payment menu");
        } else if (text.equals(menu[3])) {
            bot.sendMessage(user.getChatId(), """
                    💰Hisobingiz: %s
                    
                    👤ID raqam: %d""".formatted(formatPrice(user.getBalance()), user.getId()), kyb.addBalance());
        } else if (text.equals(menu[4])) {
            bot.sendMessage(user.getChatId(), """
                    📝 Murojaat matnini (faqat matn) yuboring:""", kyb.setKeyboards(new String[]{backButton}, 1));
            eventCode(user, "admin to msg");
        } else if (text.equals(backButton)) {
            start(user);
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
                bot.sendMessage(user.getChatId(), aboutBotPrice(botPrice, false, user.getBalance()) + "\n\n\uD83E\uDD16 **Ushbu Telegram Botni yaratmoqchimisiz?**\n", kyb.isBuyBot);
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
                double price = Math.abs(botPrice(user).getPrice() - user.getBalance());
                bot.sendMessage(user.getChatId(), """
                        ⚠️ **Afsuski, hisobingizdagi mablag' yetarli emas.**
                        
                        Ushbu botni ochish uchun %s yetmadi
                        
                        Hisobingizni to'ldirish uchun "💳 Hisobni to'ldirish" tugmasini bosing.
                        """.formatted(formatPrice(price)), kyb.setKeyboards(new String[]{"💳 Hisobni to'ldirish", backButton}, 1));
                eventCode(user, "top up account");
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
            List<Channel> channels = channelRepository.findAllByActiveIsTrueAndStatusOrderByIdAsc(Status.OPEN);
            String type = botInfo.getType();
            BotPrice byType = botPriceRepository.findByType(type);
            for (Channel channel : channels) {
                bot.sendMessageToChannel(channel.getUsername(), ("\uD83C\uDD95️ Yana yangi %s boti yaratildi!\n" + "                    \n" + "                    \n" + "\uD83E\uDD16 Bot: @%s").formatted(byType.getTypeText(), username), kyb.forChannel("https://t.me/" + username, bot.botUsername));
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


    public boolean isFinished(User user, TelegramBot bot) {
        return !bot.getChannels(user.getChatId()).isEmpty();
    }

    public boolean success(CallbackQuery callbackQuery, User user, TelegramBot bot, Kyb kyb) {
        if (isFinished(user, bot)) {
            bot.alertMessage(callbackQuery, "⚠️ Quyidagi kanallarga to'liq azo bolishingiz kerak");
            bot.editMessageText(user.getChatId(), callbackQuery.getMessage().getMessageId(), "Quyidagi kanallarga a'zo bo'ling", kyb.subscribeChannel(bot.getChannels(user.getChatId()), user.getLang()));
            return false;
        } else {
            bot.alertMessage(callbackQuery, "✅ Siz kanallarga to'liq azo boldingiz");
            bot.deleteMessage(user.getChatId(), callbackQuery.getMessage().getMessageId());
            return true;
        }
    }

    public void topUpAccount(User user, String text) {
        if (text.equals(backButton)) {
            chooseBot(user, botPrice(user).getTypeText());
        } else if (text.equals("✅ To'lov qildim")) {
            bot.sendMessage(user.getChatId(), "Chek rasmini yuboring, chek rasm ko'rinishida bo'lishi kerak 📸", true);
        } else if (text.equals("💳 Hisobni to'ldirish")) {
            Payment payment = payment();
            bot.sendPhoto(user.getChatId(), payment.getImg(), """
                    💸 Karta: <code>%s</code>
                    🧾 Karta egasining ismi: %s
                    💳 Turi: %s
                    
                    Almashuvingiz muvaffaqiyatli bajarilishi uchun quyidagi harakatlarni amalga oshiring:
                    1️⃣ Istalgan pul miqdorini tepadagi hamyonga tashlang
                    2️⃣ «✅ To'lov qildim» tugmasini bosing;
                    """.formatted(payment.getNumber(), payment.getOwner(), payment.getType()), kyb.setKeyboards(new String[]{"✅ To'lov qildim", backButton}, 1));
        } else {
            wrongBtn(user.getChatId(), kyb.setKeyboards(new String[]{"💳 Hisobni to'ldirish", backButton}, 1));
        }
    }


    private Payment payment() {
        return paymentRepository.findAll().get(0);
    }

    public void topUpAccount(User user, PhotoSize photo) {
        bot.sendPhoto(bot.superAdminChatId, photo.getFileId(), """
                Hisobni to'ldirilganligi bo'yicha chek
                
                Foydalanuvchi ma'lumotlari:
                
                ID: %d
                Chat ID: %d
                nickname: %s
                username: %s
                Balans: %s
                """.formatted(user.getId(), user.getChatId(), user.getNickname(), user.getUsername() == null ? "Mavjud emas" : "@" + user.getUsername(), formatPrice(user.getBalance())), kyb.sendCheckToAdmin(user.getChatId()));
        bot.sendMessage(user.getChatId(), "📜 Chek tez orada ko'rib chiqiladi, operatorlarning xabarini kuting. ⏳\nYoki siz %s ga aloqaga chiqishingiz mumkin 📩".formatted(adminTelegramProfile), kyb.setKeyboards(new String[]{backButton}, 1));
    }

    private List<BotInfo> myBots(Long chatId) {
        List<BotInfo> res = new ArrayList<>();
        for (BotInfo botInfo : botInfoRepository.findAllByActiveIsTrueOrderByIdAsc()) {
            for (Long chatId1 : botInfo.getAdminChatIds()) {
                if (chatId.equals(chatId1)) {
                    res.add(botInfo);
                }
            }
        }
        return res;
    }

    public void menu(User user, String data, Integer messageId, CallbackQuery callbackQuery) {
        if (data.startsWith("bot_")) {
            Long botId = Long.parseLong(data.split("_")[1]);
            Optional<BotInfo> bOp = botInfoRepository.findById(botId);
            if (bOp.isEmpty()) {
                return;
            }

            BotInfo botInfo = bOp.get();
            bot.editMessageText(user.getChatId(), messageId, aboutBot(botInfo), kyb.aboutBot());
            user.setBotId(botId);
            userService.save(user);
            return;
        }
        if (data.equals("delete")) {
            Long botId = user.getBotId();
            Optional<BotInfo> bOp = botInfoRepository.findById(botId);
            if (bOp.isEmpty()) {
                return;
            }
            BotInfo botInfo = bOp.get();
            bot.editMessageText(user.getChatId(), messageId, aboutBot(botInfo) + "\n\nHaqiqatdan ham ushbu botni o'chirmoqchimisiz ?", kyb.isSuccessBtn("uz"));
        } else if (data.equals("addBalance")) {
            bot.deleteMessage(user.getChatId(), messageId);
            menu(user, menu[2]);
        } else if (data.equals("yes delete")) {
            Long botId = user.getBotId();
            Optional<BotInfo> bOp = botInfoRepository.findById(botId);
            if (bOp.isEmpty()) {
                return;
            }
            BotInfo botInfo = bOp.get();
            botInfo.setActive(false);
            botInfo.setBotToken(UUID.randomUUID().toString() + botId);
            botInfo.setBotUsername(UUID.randomUUID().toString() + botId);
            bot.alertMessage(callbackQuery, "⏳ Iltimos, kutib turing... Bu jarayon biroz vaqt olishi mumkin.");
            bot.editMessageText(user.getChatId(), messageId, "⏳ Iltimos, kutib turing... Bu jarayon biroz vaqt olishi mumkin.");
            dynamicBotService.stopBot(botInfo.getId());
            botInfoRepository.save(botInfo);
            bot.alertMessage(callbackQuery, "✅ Muvaffaqiyatli o'chirildi!");
            bot.deleteMessage(user.getChatId(), messageId);
            menu(user, menu[1]);
        } else if (data.equals("no delete")) {
            String a = "";
            int c = 0;
            for (BotInfo botInfo : myBots(user.getChatId())) {
                c++;
                a = a.concat(c + ") 🤖 Bot manzili: @%s\n".formatted(botInfo.getBotUsername()));
            }
            bot.alertMessage(callbackQuery, "ℹ️ Operatsiya bekor qilindi");
            bot.editMessageText(user.getChatId(), messageId, """
                    📋 Sizda faol botlar soni: %d ta
                    
                    %s
                    """.formatted(myBots(user.getChatId()).size(), a), kyb.bots(myBots(user.getChatId())));
        } else if (data.equals("back")) {
            String a = "";
            int c = 0;
            for (BotInfo botInfo : myBots(user.getChatId())) {
                c++;
                a = a.concat(c + ") 🤖 Bot manzili: @%s\n".formatted(botInfo.getBotUsername()));
            }
            bot.editMessageText(user.getChatId(), messageId, """
                    📋 Sizda faol botlar soni: %d ta
                    
                    %s
                    """.formatted(myBots(user.getChatId()).size(), a), kyb.bots(myBots(user.getChatId())));
        }
    }

    private String aboutBot(BotInfo botInfo) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return """
                🤖 <b>Bot username:</b> @%s
                📡 <b>Holati:</b> %s
                🧩 <b>Turi:</b> %s
                📅 <b>Ochilgan vaqt:</b> %s
                🔄 <b>Yangilangan vaqt:</b> %s
                """.formatted(botInfo.getBotUsername(), botInfo.isActive() ? "✅ Faol" : "❌ O'chirilgan", botInfo.getType(), sdf.format(botInfo.getCreatedAt()), sdf.format(botInfo.getUpdatedAt()));

    }

    public void paymentMenu(User user, String text) {
        if (text.equals(backButton)) start(user);
        else if (text.equals("✅ To'lov qildim")) {
            bot.sendMessage(user.getChatId(), "📸 Iltimos, toʻlov chekini surat ko‘rinishida yuboring. Tekshirish uchun bu juda muhim.", true);
        }
    }

    public void adminToMsg(User user, String text) {
        if (text.equals(backButton)) start(user);
        else {
            bot.sendMessage(bot.superAdminChatId, adminToMsgFromUser(user, text), kyb.replyBtn(user.getChatId(), "uz"));
            bot.sendMessage(user.getChatId(), """
                    ✅ Murojaatingiz yuborildi.
                    
                    Tez orada javob qaytaramiz!""", kyb.menu());
            eventCode(user, "menu");
        }
    }

    private String adminToMsgFromUser(User user, String text) {
        return """
                📩 <b>Yangi xabar keldi!</b>
                
                👤 <b>Foydalanuvchi maʼlumotlari:</b>
                
                🆔 ID: <code>%d</code>
                💬 Chat ID: <code>%d</code>
                🙍‍♂️ Nickname: <a href="tg://user?id=%d">%s</a>
                🔗 Username: %s
                💰 Balans: %s soʻm
                
                📝 <b>Foydalanuvchi xabari:</b>
                <code>%s</code>
                """.formatted(
                user.getId(),
                user.getChatId(),
                user.getChatId(),
                user.getNickname(),
                user.getUsername() == null ? "Mavjud emas" : "@" + user.getUsername(),
                formatPrice(user.getBalance()),
                text
        );
    }

    public void reply(User user, String data, Integer messageId, CallbackQuery callbackQuery) {
        Long chatId = Long.valueOf(data.split("_")[1]);
        user.setHelperChatId(chatId);
        userService.save(user);
        bot.alertMessage(callbackQuery , "✍️ Iltimos, javobingizni yozing:");
        bot.sendMessage(user.getChatId(), "✍️ Iltimos, javobingizni yozing:", kyb.setKeyboards(new String[]{backButton}, 1));
        eventCode(user, "reply");
    }

    public void reply(User user, String text) {
        if (text.equals(backButton)) start(user);
        else {
            bot.sendMessage(
                    user.getHelperChatId(), adminToMsgFromUser(user, text),
                    kyb.replyBtn(user.getChatId(), "uz")
            );
            // Adminga tasdiq xabari
            bot.sendMessage(
                    user.getChatId(),
                    """
                            ✅ Murojaatingiz yuborildi.
                              
                            Tez orada javob qaytaramiz!""",
                    kyb.menu()
            );

            eventCode(user, "menu");
        }
    }
}
