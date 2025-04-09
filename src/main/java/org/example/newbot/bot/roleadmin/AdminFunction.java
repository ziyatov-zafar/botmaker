package org.example.newbot.bot.roleadmin;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.newbot.bot.StaticVariable;
import org.example.newbot.bot.Status;
import org.example.newbot.bot.TelegramBot;
import org.example.newbot.dto.ResponseDto;
import org.example.newbot.model.*;
import org.example.newbot.repository.BotInfoRepository;
import org.example.newbot.repository.BotPriceRepository;
import org.example.newbot.repository.ChannelRepository;
import org.example.newbot.repository.PaymentRepository;
import org.example.newbot.service.BotPriceService;
import org.example.newbot.service.DynamicBotService;
import org.example.newbot.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static org.apache.http.client.utils.DateUtils.formatDate;
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
    private final BotPriceRepository botPriceRepository;
    private final PaymentRepository paymentRepository;
    private final ChannelRepository channelRepository;

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
            bot.sendMessage(user.getChatId(), text, kyb.userPage);
            eventCode(user, "user page");
        } else if (text.equals(botMakerAdminMenu[1])) {
            bot.sendMessage(user.getChatId(), text, kyb.botMenu());
            eventCode(user, "bot menu");
        } else if (text.equals(botMakerAdminMenu[2])) {
            bot.sendMessage(user.getChatId(), statistika(), kyb.backBtn);
        } else if (text.equals(botMakerAdminMenu[3])) {
            bot.sendMessage(user.getChatId(), "😊 Quyidagi menyulardan birini tanlang:", kyb.settingsMenu);
            eventCode(user, "settings menu");
        } else if (text.equals(botMakerAdminMenu[4])) {
            bot.sendMessage(user.getChatId(), "\uD83D\uDCCB Sizning barcha kanallaringiz ro'yxati quyidagicha:", kyb.getChannels(channelRepository.findAllByActiveIsTrueAndStatusOrderByIdAsc(Status.OPEN)));
            eventCode(user, "channels menu");
        } else if (text.equals(botMakerAdminMenu[5])) {
            bot.sendMessage(
                    user.getChatId(),
                    "📢 Iltimos, reklamangiz matnini yuboring:",
                    kyb.setKeyboards(new String[]{backButton}, 1)
            );
            eventCode(user, "reklama");
        } else if (text.equals(backButton)) {
            start(user);
        } else wrongBtn(user, kyb.menu);

    }

    private String statistika() {
        StringBuilder statistika = new StringBuilder();
        long userCount = userService.findAll().getData().size();
        long botCount = botInfoRepository.findAllByActiveIsTrueOrderByIdDesc().size();

        return String.format("""
                📊 Botdagi umumiy foydalanuvchilar soni: %d ta
                🤖 Yasalgan aktiv soni: %d ta
                🤖 Yasalgan aktiv bo'lmaganlar soni: %d ta
                """, userCount, botCount, botInfoRepository.findAll().size() - botCount);
    }


    public void botMenu(User user, String text) {
        if (text.equals(backButton)) {
            start(user);
        } else if (text.equals(botMakerAdminBotMenu[0])) {
            user.setPage(0);
            user.setEventCode("choose bot list type");
            userService.save(user);
            bot.sendMessage(user.getChatId(), text, kyb.chooseTypeBotList());
        } else if (text.equals(botMakerAdminBotMenu[1])) {
            bot.sendMessage(user.getChatId(),
                    """
                            🔍 Iltimos, botni qidirish uchun bot username yoki bot tokenini kiriting. Masalan, username yoki token orqali botni topishingiz mumkin.
                            
                            Agar yordamga muhtoj bo'lsangiz, iltimos, tugmalardan foydalaning.""",
                    kyb.backBtn);
            eventCode(user, "search bots");
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
                if (checkBot.isActive()) {
                    dynamicBotService.stopBot(checkBot.getId());
                }
                checkBot.setBotUsername(checkBot.getBotUsername() + checkBot.getBotToken() + UUID.randomUUID() + checkBot.getId());
                checkBot.setBotToken(UUID.randomUUID() + checkBot.getBotToken() + checkBot.getId());
                checkBot.setActive(false);
                botInfoRepository.save(checkBot);
            }
            bot.sendMessage(user.getChatId(),
                    """
                            ⏳ Jarayon davom etmoqda...
                            
                            Bu biroz vaqt olishi mumkin. Iltimos, sabr qiling va orqaga suyanib kuting! ☕
                            """);

            int c = 100; // Progress uchun
            System.out.println("\n\n🔄 Jarayon boshlandi... \n\n");
            BotInfo botInfo = new BotInfo();
            botInfo.setActive(true);
            botInfo.setBotToken(text);
            botInfo.setAdminChatIds(new ArrayList<>());
            botInfo.setAdminChatIds(List.of(user.getChatId()));
            botInfo.setType(user.getHelperBotType());
            botInfo.setIsFree(true);
            ZoneId uzbekistanZone = ZoneId.of("Asia/Tashkent");
            ZonedDateTime nowInTashkent = ZonedDateTime.now(uzbekistanZone);
            Date dateInTashkent = Date.from(nowInTashkent.toInstant());

            botInfo.setCreatedAt(dateInTashkent);
            botInfo.setUpdatedAt(dateInTashkent);
            String username = dynamicBotService.createAndRegisterBot(telegramBotsApi, botInfo);
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
                String successMessage = "✅ <b>Bot muvaffaqiyatli yaratildi!</b>\n"
                        + "Sizning botingiz: <a href=\"https://t.me/" + username + "\">@" + username + "</a>\n"
                        + "Botni ishga tushirish uchun: <a href=\"https://t.me/BotFather\">@BotFather</a> ga <code>/start</code> buyrug'ini yuboring";
                bot.sendMessage(user.getChatId(), successMessage);
                // Keyingi menyu bosqichiga o'tish
                menu(user, botMakerAdminMenu[1]);
            }
        }
    }

    private String allBotsMsg(List<BotInfo> bots) {
        if (bots.isEmpty()) {
            return "🤖 <b>Hech qanday bot topilmadi.</b>";
        }

        StringBuilder s = new StringBuilder("🤖 <b>Botlar ro'yxati:</b>\n\n");

        int index = 1;
        for (BotInfo bot : bots) {
            s.append(String.format("""
                            %d. <b>%s</b>
                            🔗 <a href="https://t.me/%s">@%s</a>
                            📊 Status: %s
                            💰 Tariff: %s
                            \n
                            """,
                    index++,
                    bot.getBotUsername(),
                    bot.getBotUsername(),
                    bot.getBotUsername(),
                    bot.isActive() ? "✅ Aktiv" : "⛔ To‘xtatilgan",
                    Boolean.TRUE.equals(bot.getIsFree()) ? "🆓 Bepul" : "💳 Pullik"
            ));
        }

        return s.toString();
    }

    private String aboutBotInformation(BotInfo bot, long botUsersCount, long botAdminsCount) {
        return String.format("""
                        🤖 <b>Bot haqida ma'lumot</b>
                        
                        🆔 Bot ID: <code>%d</code>
                        🔗 Username: <a href="https://t.me/%s">@%s</a>
                        📦 Tip: %s
                        📊 Status: %s
                        💰 Tariff: %s
                        
                        👥 Foydalanuvchilar soni: <code>%d</code>
                        👨‍💻 Adminlar soni: <code>%d</code>
                        
                        🗓 Yaratilgan sana: %s
                        📝 Oxirgi yangilanish: %s
                        """,
                bot.getId(),
                bot.getBotUsername(),
                bot.getBotUsername(),
                bot.getType() != null ? bot.getType() : "—",
                bot.isActive() ? "✅ Aktiv" : "⛔ To‘xtatilgan",
                Boolean.TRUE.equals(bot.getIsFree()) ? "🆓 Bepul" : "💳 Pullik",
                botUsersCount,
                botAdminsCount,
                formatDate(bot.getCreatedAt()),
                formatDate(bot.getUpdatedAt())
        );
    }


    public void chooseBotListType(User user, String text) {
        Page<BotInfo> bots;
        boolean active;
        if (text.equals(backButton)) {
            menu(user, botMakerAdminMenu[1]);
            return;
        } else if (text.equals(chooseTypeBotList[0])) {
            active = true;
        } else if (text.equals(chooseTypeBotList[1])) {
            active = false;
        } else {
            wrongBtn(user, kyb.chooseTypeBotList());
            return;
        }
        user.setForBotActive(active);
        userService.save(user);
        bots = botInfoRepository.findAllByActiveOrderByIdDesc(active, PageRequest.of(user.getPage(), bot.size));
        if (!bots.getContent().isEmpty()) {
            bot.sendMessage(user.getChatId(), allBotsMsg(bots.getContent()), kyb.getAllBots(bots.getContent(), bots.isFirst(), bots.isLast()));
        } else {
            bot.sendMessage(user.getChatId(), text + " botlar mavjud emas");
        }
    }

    private void wrongBtn(User user, ReplyKeyboardMarkup markup) {
        bot.sendMessage(user.getChatId(), "❗ Iltimos, faqat tugmalardan foydalaning", markup);
    }

    public void chooseBotListType(User user, String data, CallbackQuery callbackQuery, Integer messageId, boolean isSearch) {
        BotInfo botInfo;
        Integer page = user.getPage();
        if (data.equals("next")) {
            page++;
        } else if (data.equals("back")) {
            page--;
        } else {
            if (!data.equals("back_to_bots")) {
                if (data.equals("activate_bot")) {
                    botInfo = getBotInfo(user.getBotId());
                    botInfo.setActive(true);
                    botInfo.setUpdatedAt(getTashekntDate());
                    botInfoRepository.save(botInfo);
                    dynamicBotService.createAndRegisterBot(telegramBotsApi, botInfo);
                    bot.alertMessage(callbackQuery, "✅ Muvaffaqiyatli faollashtirildi");
                    List<BotUser> botUsers = botInfoRepository.findAllUsersByBotId(botInfo.getId());
                    long admins = botInfo.getAdminChatIds().size();
                    long users = botUsers.size() - admins;
                    String s = aboutBotInformation(botInfo, users, admins);
                    bot.editMessageText(user.getChatId(), messageId, s, kyb.crudBot(botInfo.isActive()));
                    user.setBotId(botInfo.getId());
                    userService.save(user);
                } else if (data.equals("deactivate_bot")) {
                    bot.alertMessage(callbackQuery, "⏳ Iltimos kutib turing, bu 1-2 daqiqa vaqtni olishi mumkin...");
                    botInfo = getBotInfo(user.getBotId());
                    botInfo.setActive(false);
                    dynamicBotService.stopBot(botInfo.getId());
                    botInfoRepository.save(botInfo);
                    List<BotUser> botUsers = botInfoRepository.findAllUsersByBotId(botInfo.getId());
                    long admins = botInfo.getAdminChatIds().size();
                    long users = botUsers.size() - admins;
                    String s = "✅ Muvaffaqiyatli o'chirildi\n\n" + aboutBotInformation(botInfo, users, admins);
                    bot.editMessageText(user.getChatId(), messageId, s, kyb.crudBot(botInfo.isActive()));
                    user.setBotId(botInfo.getId());
                    userService.save(user);
                } else if (data.equals("delete_all_admin")) {
                    botInfo = getBotInfo(user.getBotId());
                    deleteAllAdmin(botInfo);
                    bot.alertMessage(callbackQuery, "✅ Adminlar muvaffaqiyatli o'chirildi");
                    List<BotUser> botUsers = botInfoRepository.findAllUsersByBotId(botInfo.getId());
                    long admins = botInfo.getAdminChatIds().size();
                    long users = botUsers.size() - admins;
                    String s = aboutBotInformation(botInfo, users, admins);
                    bot.editMessageText(user.getChatId(), messageId, s, kyb.crudBot(botInfo.isActive()));
                } else if (data.equals("add_admin")) {
                    bot.deleteMessage(user.getChatId(), messageId);
                    bot.sendMessage(user.getChatId(), "🔑 Biror bir chat ID kiriting, adminnnikini bo'lsin.", kyb.backBtn);
                    eventCode(user, "add bot admin chat id");
                } else {
                    Long botId = Long.parseLong(data);
                    botInfo = getBotInfo(botId);
                    List<BotUser> botUsers = botInfoRepository.findAllUsersByBotId(botId);
                    long admins = botInfo.getAdminChatIds().size();
                    long users = botUsers.size() - admins;
                    String s = aboutBotInformation(botInfo, users, admins);
                    bot.editMessageText(user.getChatId(), messageId, s, kyb.crudBot(botInfo.isActive()));
                    user.setBotId(botId);
                    userService.save(user);
                }
                return;
            }
        }
        user.setPage(page);
        Page<BotInfo> bots;
        userService.save(user);
        if (isSearch) {
            bots = botInfoRepository.searchBot(user.getQueryText(), PageRequest.of(user.getPage(), bot.size));
        } else {
            bots = botInfoRepository.findAllByActiveOrderByIdDesc(user.getForBotActive(), PageRequest.of(user.getPage(), bot.size));
        }
        bot.editMessageText(user.getChatId(), messageId, allBotsMsg(bots.getContent()), kyb.getAllBots(bots.getContent(), bots.isFirst(), bots.isLast()));
    }

    private void deleteAllAdmin(BotInfo botInfo) {
        botInfo.setAdminChatIds(new ArrayList<>());
        botInfoRepository.save(botInfo);
    }

    private Date getTashekntDate() {
        ZoneId uzbekistanZone = ZoneId.of("Asia/Tashkent");
        ZonedDateTime nowInTashkent = ZonedDateTime.now(uzbekistanZone);
        return Date.from(nowInTashkent.toInstant());
    }

    private BotInfo getBotInfo(Long botId) {
        Optional<BotInfo> bOp = botInfoRepository.findById(botId);
        return bOp.orElse(null);
    }

    public void addBotAdminChatId(User user, String text) {
        if (addAdminChatId(user, text)) {
            eventCode(user, "choose bot list type");
        }
    }

    private boolean addAdminChatId(User user, String text) {
        try {
            String msg;
            BotInfo botInfo = getBotInfo(user.getBotId());
            List<BotUser> botUsers = botInfoRepository.findAllUsersByBotId(botInfo.getId());
            long admins = botInfo.getAdminChatIds().size();
            long users = botUsers.size() - admins;
            if (!text.equals(backButton)) {
                List<Long> chatIds = botInfo.getAdminChatIds();
                Long c = Long.parseLong(text);
                boolean found = false;
                for (Long chatId : chatIds) {
                    if (chatId.equals(c)) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    bot.sendMessage(user.getChatId(), "⚠️ Bu foydalanuvchi avvaldan admin.", kyb.backBtn);
                    return false;
                }
                chatIds.add(c);
                botInfo.setAdminChatIds(chatIds);
                botInfoRepository.save(botInfo);
                botUsers = botInfoRepository.findAllUsersByBotId(botInfo.getId());
                admins = botInfo.getAdminChatIds().size();
                users = botUsers.size() - admins;
                msg = "Muvaffaqiyatli qo'shildi" + aboutBotInformation(botInfo, users, admins);
            } else msg = aboutBotInformation(botInfo, users, admins);
            bot.sendMessage(user.getChatId(), msg, kyb.crudBot(botInfo.isActive()));
            return true;
        } catch (Exception e) {
            bot.sendMessage(user.getChatId(), "🔢 Chat ID ni son sifatida kiriting, yoki tugmalardan foydalaning.", kyb.backBtn);
            return false;
        }
    }


    public void searchBots(User user, String text) {
        if (text.equals(backButton)) {
            menu(user, botMakerAdminMenu[1]);
            return;
        }
        user.setPage(0);
        user.setQueryText(text);
        userService.save(user);
        Page<BotInfo> botPage = botInfoRepository.searchBot(text, PageRequest.of(user.getPage(), bot.size));
        if (botPage.getContent().isEmpty()) {
            bot.sendMessage(user.getChatId(), "🔍 Hech narsa topilmadi. Iltimos, qayta urinib ko'ring.", kyb.backBtn);
            return;
        }
        List<BotInfo> bots = botPage.getContent();
        bot.sendMessage(user.getChatId(), text + " - qidiruvi bo'yicha natijalar 🔍:\n" + allBotsMsg(bots), kyb.getAllBots(bots, botPage.isFirst(), botPage.isLast()));
    }

    public void searchBots(User user, String data, Integer messageId, CallbackQuery callbackQuery) {
        chooseBotListType(user, data, callbackQuery, messageId, true);
    }

    private String userList(List<User> users) {
        StringBuilder sb = new StringBuilder();
        for (User user : users) {
            sb.append(String.format("""
                            📛 Foydalanuvchi niki: <a href="tg://user?id=%d">%s</a>
                            🆔 Foydalanuvchi username si: %s
                            🔑 Foydalanuvchi ID si: %s
                            📬 Foydalanuvchi CHAT ID si: %s
                            💰 Foydalanuvchining balansi: %s
                            ------------------------
                            """,
                    user.getChatId(),
                    user.getNickname(),
                    user.getUsername() == null ? "❌ Mavjud emas" : ("@" + user.getUsername()),
                    user.getId(),
                    user.getChatId(),
                    StaticVariable.formatPrice(user.getBalance() == null ? 0 : user.getBalance())));
        }
        return sb.toString();
    }


    public void userPage(User user, String text) {
        user.setPage(0);
        userService.save(user);
        if (text.equals(userPageMenu[0])) {
            Page<User> userPage = userService.findAll(user.getPage(), bot.size).getData();
            bot.sendMessage(user.getChatId(),
                    "📋 Barcha foydalanuvchilarning ro'yxati:\n\n" + userList(userPage.getContent()),
                    kyb.userLists(userPage.getContent(), userPage.isFirst(), userPage.isLast())
            );
        } else if (text.equals(userPageMenu[1])) {
            bot.sendMessage(
                    user.getChatId(),
                    "🔍 Iltimos, foydalanuvchining *username* yoki *nickname*ini kiriting:",
                    kyb.backBtn
            );
            eventCode(user, "search users");
        } else if (text.equals(backButton)) {
            start(user);
        } else {
            wrongBtn(user, kyb.userPage);
        }
    }

    public String aboutUser(User user) {
        return String.format("""
                        🆔 Foydalanuvchi ID: %s
                        👤 Foydalanuvchi ismi: %s
                        📱 Foydalanuvchi username: %s
                        💬 Foydalanuvchi CHAT ID: %s
                        💰 Foydalanuvchining balansi: %s
                        🌟 Holati: %s
                        """,
                user.getId(),
                user.getNickname(),
                user.getUsername() == null ? "Mavjud emas" : user.getUsername(),
                user.getChatId(),
                StaticVariable.formatPrice(user.getBalance() == null ? 0 : user.getBalance()),
                user.getRole().equals("block") ? "Bloklangan" : "Aktiv"

        );
    }


    public void userPage(User user, String data, Integer messageId, CallbackQuery callbackQuery) {
        Integer page = user.getPage();
        if (data.equals("next")) page++;
        else if (data.equals("back")) page--;
        else {
            if (!data.equals("back1")) {
                if (data.equals("addBalance")) {
                    User currentUser = userService.findById(user.getUserId()).getData();
                    String caption = addBalance(currentUser, currentUser.getBalance());
                    currentUser.setHelperBalance(0.0);
                    userService.save(currentUser);
                    bot.editMessageText(user.getChatId(), messageId, caption, kyb.addBalance());
                    eventCode(user, data);
                } else if (data.equals("blockUser")) {
                    User currentUser = userService.findById(user.getUserId()).getData();
                    currentUser.setRole("block");
                    userService.save(currentUser);
                    bot.alertMessage(callbackQuery, "✅ Operatsiya muvaffaqiyatli yakunlandi!");

                    bot.editMessageText(user.getChatId(), messageId, aboutUser(currentUser), kyb.userCrud(currentUser.getRole().equals("user")));
                    bot.sendMessage(currentUser.getChatId(), "Siz admin tomonidan bloklandingiz, malumot uchun %s ga yozishingiz mumkin".formatted(adminTelegramProfile), true);
                } else if (data.equals("unblockUser")) {
                    User currentUser = userService.findById(user.getUserId()).getData();
                    currentUser.setRole("user");
                    userService.save(currentUser);
                    bot.alertMessage(callbackQuery, "✅ Operatsiya muvaffaqiyatli yakunlandi!");
                    bot.editMessageText(user.getChatId(), messageId, aboutUser(currentUser), kyb.userCrud(currentUser.getRole().equals("user")));
                    bot.sendMessage(currentUser.getChatId(), "Siz admin tomonidan blokdan chiqarildingiz, botni ishlastish uchun \"/start\" xabarini botga yuboring", true);
                } else {
                    Long userId = Long.valueOf(data);
                    User currentUser = userService.findById(userId).getData();
                    user.setUserId(userId);
                    userService.save(user);
                    bot.editMessageText(user.getChatId(), messageId, aboutUser(currentUser), kyb.userCrud(currentUser.getRole().equals("user")));
                }
                return;

            }
        }
        user.setPage(page);
        userService.save(user);
        Page<User> userPage = userService.findAll(user.getPage(), bot.size).getData();
        bot.editMessageText(user.getChatId(), messageId, "📋 Barcha foydalanuvchilarning ro'yxati:\n\n" + userList(userPage.getContent()), kyb.userLists(userPage.getContent(), userPage.isFirst(), userPage.isLast()));
    }

    private String addBalance(User currentUser, Double balance) {
        return String.format("""
                        💰 %s foydalanuvchiga qo'shmoqchi bo'lgan balansingiz:
                        
                        🔹 Balans: %s
                        """,
                currentUser.getNickname(),
                formatPrice(balance == null ? 0 : balance));
    }

    public void addBalance(User user, String data, Integer messageId, CallbackQuery callbackQuery) {
        User currentUser = userService.findById(user.getUserId()).getData();
        Double balance = user.getHelperBalance();

        if (data.equals("delete")) {
            // Agar balans 1 bo'lsa, u holda 0 ga o'tkazamiz
            if (balance < 10) {
                balance = 0.0;
            } else {
                balance = Math.floor(balance / 10);
            }
        } else if (data.equals("save")) {
            user.setHelperBalance(0.0);
            userService.save(user);
            currentUser.setBalance(currentUser.getBalance() == null ? balance : currentUser.getBalance() + balance);
            userService.save(currentUser);
            bot.alertMessage(callbackQuery, "✅ Muvaffaqiyatli qo‘shildi!");
            bot.editMessageText(user.getChatId(), messageId, aboutUser(currentUser), kyb.userCrud(currentUser.getRole().equals("user")));
            bot.sendMessage(currentUser.getChatId(), ("\uD83D\uDCB8 Sizning hisobingizga %s qo'shildi\n" +
                    "\n" +
                    "\uD83D\uDCCA Umumiy balansingiz: %s so‘m ga yetdi").formatted(formatPrice(balance), formatPrice(currentUser.getBalance())));
            eventCode(user, "user page");
            return;
        } else if (data.equals("cancel")) {
            user.setHelperBalance(0.0);
            userService.save(user);
            bot.alertMessage(callbackQuery, "❌ Operatsiya bekor qilindi!");
            bot.editMessageText(user.getChatId(), messageId, aboutUser(currentUser), kyb.userCrud(currentUser.getRole().equals("user")));
            eventCode(user, "user page");
            return;
        } else {
            balance = balance * 10 + Double.parseDouble(data);
        }

        user.setHelperBalance(balance);
        userService.save(user);
        String caption = addBalance(currentUser, balance);
        bot.editMessageText(user.getChatId(), messageId, caption, kyb.addBalance());

    }

    public void searchUsers(User user, String text) {
        if (text.equals(backButton)) {
            menu(user, adminOnlineMagazineMenu[0]);
            return;
        }

        Page<User> userPage = userService.searchUsers(text, user.getPage(), bot.size).getData();

        if (userPage.getContent().isEmpty()) {
            bot.sendMessage(user.getChatId(), "❗️Hech qanday foydalanuvchi topilmadi.", kyb.backBtn);
            return;
        }

        user.setQueryText(text);
        user.setPage(0);
        userService.save(user);
        String message = "🔍 <b>" + text + "</b> so‘rovi bo‘yicha topilgan foydalanuvchilar:\n\n"
                + userList(userPage.getContent());
        bot.sendMessage(user.getChatId(), message, kyb.userLists(userPage.getContent(), userPage.isFirst(), userPage.isLast()));
    }

    public void searchUsers(User user, String data, Integer messageId, CallbackQuery callbackQuery) {
        Integer page = user.getPage();
        if (data.equals("next")) page++;
        else if (data.equals("back")) page--;
        else {
            if (!data.equals("back1")) {
                if (data.equals("addBalance")) {
                    User currentUser = userService.findById(user.getUserId()).getData();
                    String caption = addBalance(currentUser, currentUser.getBalance());
                    currentUser.setHelperBalance(0.0);
                    userService.save(currentUser);
                    bot.editMessageText(user.getChatId(), messageId, caption, kyb.addBalance());
                    eventCode(user, "addBalance1");
                } else if (data.equals("blockUser")) {
                    User currentUser = userService.findById(user.getUserId()).getData();
                    currentUser.setRole("block");
                    userService.save(currentUser);
                    bot.alertMessage(callbackQuery, "✅ Operatsiya muvaffaqiyatli yakunlandi!");

                    bot.editMessageText(user.getChatId(), messageId, aboutUser(currentUser), kyb.userCrud(currentUser.getRole().equals("user")));
                    bot.sendMessage(currentUser.getChatId(), "Siz admin tomonidan bloklandingiz, malumot uchun %s ga yozishingiz mumkin".formatted(adminTelegramProfile), true);
                } else if (data.equals("unblockUser")) {
                    User currentUser = userService.findById(user.getUserId()).getData();
                    currentUser.setRole("user");
                    userService.save(currentUser);
                    bot.alertMessage(callbackQuery, "✅ Operatsiya muvaffaqiyatli yakunlandi!");
                    bot.editMessageText(user.getChatId(), messageId, aboutUser(currentUser), kyb.userCrud(currentUser.getRole().equals("user")));
                    bot.sendMessage(currentUser.getChatId(), "Siz admin tomonidan blokdan chiqarildingiz, botni ishlastish uchun \"/start\" xabarini botga yuboring", true);
                } else {
                    Long userId = Long.valueOf(data);
                    User currentUser = userService.findById(userId).getData();
                    user.setUserId(userId);
                    userService.save(user);
                    bot.editMessageText(user.getChatId(), messageId, aboutUser(currentUser), kyb.userCrud(currentUser.getRole().equals("user")));
                }
                return;

            }
        }
        user.setPage(page);
        userService.save(user);
        Page<User> userPage = userService.searchUsers(user.getQueryText(), user.getPage(), bot.size).getData();
        bot.editMessageText(user.getChatId(), messageId, "📋 Barcha foydalanuvchilarning ro'yxati:\n\n" + userList(userPage.getContent()), kyb.userLists(userPage.getContent(), userPage.isFirst(), userPage.isLast()));
    }

    public void addBalance1(User user, String data, Integer messageId, CallbackQuery callbackQuery) {
        User currentUser = userService.findById(user.getUserId()).getData();
        Double balance = user.getHelperBalance();

        if (data.equals("delete")) {
            // Agar balans 1 bo'lsa, u holda 0 ga o'tkazamiz
            if (balance < 10) {
                balance = 0.0;
            } else {
                balance = Math.floor(balance / 10);
            }
        } else if (data.equals("save")) {
            user.setHelperBalance(0.0);
            userService.save(user);
            currentUser.setBalance(currentUser.getBalance() == null ? balance : currentUser.getBalance() + balance);
            userService.save(currentUser);
            bot.alertMessage(callbackQuery, "✅ Muvaffaqiyatli qo‘shildi!");
            bot.editMessageText(user.getChatId(), messageId, aboutUser(currentUser), kyb.userCrud(currentUser.getRole().equals("user")));
            bot.sendMessage(currentUser.getChatId(), ("\uD83D\uDCB8 Sizning hisobingizga %s qo'shildi\n" +
                    "\n" +
                    "\uD83D\uDCCA Umumiy balansingiz: %s so‘m ga yetdi").formatted(formatPrice(balance), formatPrice(currentUser.getBalance())));

            eventCode(user, "search users");
            return;
        } else if (data.equals("cancel")) {
            user.setHelperBalance(0.0);
            userService.save(user);
            bot.alertMessage(callbackQuery, "❌ Operatsiya bekor qilindi!");
            bot.editMessageText(user.getChatId(), messageId, aboutUser(currentUser), kyb.userCrud(currentUser.getRole().equals("user")));
            eventCode(user, "search users");
            return;
        } else {
            balance = balance * 10 + Double.parseDouble(data);
        }

        user.setHelperBalance(balance);
        userService.save(user);
        String caption = addBalance(currentUser, balance);
        bot.editMessageText(user.getChatId(), messageId, caption, kyb.addBalance());

    }

    public void settingsMenu(User user, String text) {
        if (text.equals(backButton)) start(user);
        else if (text.equals(settingsMenu[0])) {
            List<BotPrice> list = botPriceService.findAll().getData();
            bot.sendMessage(user.getChatId(), "O'zingizga kerakli botni tanlang:", kyb.setBotPrices(list));
            eventCode(user, "edit bots menu");
        } else if (text.equals(settingsMenu[1])) {
            bot.sendPhoto(user.getChatId(), paymentRepository.findAll().get(0).getImg(), aboutCard(paymentRepository.findAll().get(0)), kyb.editCard());
        } else if (text.equals("🏦 Karta turi")) {
            bot.sendMessage(user.getChatId(), "Yangi karta turini kiriting\n\nAvvalgi karta turi: <code>%s</code>".formatted(card().getType()), kyb.backBtn);
            eventCode(user, "edit card type");
        } else if (text.equals("💳 Karta raqami")) {
            bot.sendMessage(user.getChatId(), "Yangi karta raqamini kiriting\n\nAvvalgi karta raqam: <code>%s</code>".formatted(card().getNumber()), kyb.backBtn);
            eventCode(user, "edit card number");
        } else if (text.equals("👤 Karta egasi")) {
            bot.sendMessage(user.getChatId(), "Yangi karta egasini ismini kiriting\n\nAvvalgi karta egasining ismi: <code>%s</code>".formatted(card().getOwner()), kyb.backBtn);
            eventCode(user, "edit card owner");
        } else if (text.equals("🖼️ Karta rasmi")) {
            bot.sendPhoto(user.getChatId(), card().getImg(), null);
            bot.sendMessage(user.getChatId(), "Yangi karta rasmini kiriting\n\nAvvalgi karta rasmi tepada", kyb.backBtn);
            eventCode(user, "edit card img");
        } else wrongBtn(user, kyb.settingsMenu);
    }

    private Payment card() {
        return paymentRepository.findAll().get(0);
    }

    private String aboutCard(Payment payment) {
        return """
                💳 *Karta raqami*: %s
                👤 *Karta egasi*: %s
                🏦 *Karta turi*: %s
                """.formatted(
                payment.getNumber(), payment.getOwner(), payment.getType()
        );

    }

    private String aboutBotPrice(BotPrice botPrice) {
        return String.format("""
                        🌟 *Bot haqida ma'lumot:*
                        
                        🔹 *Bot turi:* %s
                        🔸 *Tavsif:* %s
                        💰 *Narxi:* %s
                        """,
                botPrice.getTypeText(),
                botPrice.getDescription(),
                formatPrice(botPrice.getPrice())
        );
    }


    public void editBotsMenu(User user, String text) {
        BotPrice botPrice = botPrice(user);
        if (text.equals(backButton)) menu(user, botMakerAdminMenu[3]);
        else if (text.equals("🔄 Turini o'zgartirish")) {
            bot.sendMessage(user.getChatId(), "Botning yangi turini kiriting\n\nBotning avvalgi turi: <code>%s</code>".formatted(botPrice.getTypeText()), kyb.backBtn);
            eventCode(user, "edit bot type");
        } else if (text.equals("✏️ Tavsifini o'zgartirish")) {
            bot.sendMessage(user.getChatId(), "Botning yangi tavsifini kiriting\n\nBotning avvalgi tavsifi: <code>%s</code>".formatted(botPrice.getDescription()), kyb.backBtn);
            eventCode(user, "edit bot desc");
        } else if (text.equals("💵 Narxini o'zgartirish")) {
            bot.sendMessage(user.getChatId(), "Botning yangi tavsifini kiriting\n\nBotning avvalgi tavsifi: <code>%s</code>".formatted(formatPrice(botPrice.getPrice())), kyb.backBtn);
            eventCode(user, "edit bot price");
        } else {
            ResponseDto<BotPrice> checkBotPrice = botPriceService.findByTypeText(text);
            if (checkBotPrice.isSuccess()) {
                botPrice = checkBotPrice.getData();
                user.setBotPriceId(botPrice.getId());
                userService.save(user);
                bot.sendMessage(user.getChatId(), aboutBotPrice(botPrice), kyb.editBotPrice());
                eventCode(user, "edit bots menu");
            } else wrongBtn(user, kyb.setBotPrices(botPriceService.findAll().getData()));
        }
    }

    private BotPrice botPrice(User user) {
        return botPriceService.findById(user.getBotPriceId()).getData();
    }

    /*public void editBot(User user, String text, String eventCode) {
        if (text.equals(backButton)) editBotsMenu(user, botPrice(user).getTypeText());
        else {
            BotPrice botPrice = botPrice(user);
            String msg;
            ReplyKeyboardMarkup markup;
            switch (eventCode) {
                case "edit bot price" -> {
                    try {
                        botPrice.setPrice(Double.valueOf(text));
                        botPriceRepository.save(botPrice);
                        msg = "Muvaffaqoyatli o'zgartirildi";
                    } catch (Exception e) {
                        markup = kyb.backBtn;
                        msg = "Narxni faqat sonlar bilan kiritishingiz kerak";
                        bot.sendMessage(user.getChatId(), msg, markup);
                        return;
                    }
                }
                case "edit bot desc" -> {
                    botPrice.setDescription(text);
                    botPriceRepository.save(botPrice);
                    msg = "Muvaffaqoyatli o'zgartirildi";
                }
                case "edit bot type" -> {
                    botPrice.setTypeText(text);
                    botPriceRepository.save(botPrice);
                    msg = "Muvaffaqoyatli o'zgartirildi";
                }
                default -> {
                    return;
                }
            }
            eventCode(user, "edit bots menu");
            bot.sendMessage(user.getChatId(), msg);
            editBotsMenu(user, botPrice.getTypeText());
        }
    }*/
    public void editBot(User user, String text, String eventCode) {
        if (text.equals(backButton)) {
            // Foydalanuvchi "Orqaga" tugmasini bosgan bo'lsa, botni bosh menyuga qaytarish
            editBotsMenu(user, botPrice(user).getTypeText());
            return;
        }

        BotPrice botPrice = botPrice(user);
        String msg;
        ReplyKeyboardMarkup markup = kyb.backBtn;

        try {
            switch (eventCode) {
                case "edit bot price":
                    // Narxni faqat son sifatida qabul qilish
                    botPrice.setPrice(Double.valueOf(text));
                    botPriceRepository.save(botPrice);
                    msg = "Bot narxi muvaffaqiyatli o'zgartirildi!";
                    break;

                case "edit bot desc":
                    // Bot tavsifini o'zgartirish
                    botPrice.setDescription(text);
                    botPriceRepository.save(botPrice);
                    msg = "Bot tavsifi muvaffaqiyatli o'zgartirildi!";
                    break;

                case "edit bot type":
                    // Bot turini o'zgartirish
                    botPrice.setTypeText(text);
                    botPriceRepository.save(botPrice);
                    msg = "Bot turi muvaffaqiyatli o'zgartirildi!";
                    break;

                default:
                    return;  // Hech qanday o'zgartirish kerak emas
            }

            // O'zgartirishlar amalga oshirilgandan so'ng, botga xabar yuborish
            bot.sendMessage(user.getChatId(), msg);

            // Foydalanuvchini to'g'ri menyuga qaytarish
            editBotsMenu(user, botPrice.getTypeText());

        } catch (NumberFormatException e) {
            // Agar foydalanuvchi narxni noto'g'ri formatda kiritgan bo'lsa
            msg = "Narxni faqat sonlar bilan kiritishingiz kerak!";
            bot.sendMessage(user.getChatId(), msg, markup);
        }
    }


    public void editCard(User user, String text, String eventCode) {
        if (text.equals(backButton)) {
            eventCode(user, "settings menu");
            settingsMenu(user, settingsMenu[1]);
            return;
        }
        Payment card = card();
        String msg;
        if (eventCode.equals("edit card number")) {
            card.setNumber(text);
            msg = "Karta raqami muvaffaqiyatli o'zgartirildi!";
        } else if (eventCode.equals("edit card owner")) {
            card.setOwner(text);
            msg = "Karta egasining ismi muvaffaqiyatli o'zgartirildi!";
        } else if (eventCode.equals("edit card type")) {
            card.setType(text);
            msg = "Karta turi muvaffaqiyatli o'zgartirildi!";
        } else return;
        paymentRepository.save(card);
        bot.sendMessage(user.getChatId(), msg);
        eventCode(user, "settings menu");
        settingsMenu(user, settingsMenu[1]);
    }

    public void editCardImg(User user, PhotoSize photo) {
        Payment card = card();
        card.setImg(photo.getFileId());
        paymentRepository.save(card);
        bot.sendMessage(user.getChatId(), "Karta rasmi muvaffaqiyatli o'zgartirildi!");
        eventCode(user, "settings menu");
        settingsMenu(user, settingsMenu[1]);
    }

    public void channelsMenu(User user, String text) {
        if (text.equals(backButton)) start(user);
        else if (text.equals(addChannel)) {
            bot.sendMessage(
                    user.getChatId(),
                    "📢 Iltimos, qo‘shmoqchi bo‘lgan kanalingiz nomini kiriting:",
                    kyb.backBtn
            );
            eventCode(user, "get new channel name");
        } else if (text.equals("✏️ Kanal nomini o‘zgartirish")) {
            Channel channel = channel(user);
            bot.sendMessage(user.getChatId(), "Yangi kanal nomini kiriting, eski nomi : <code>" + channel.getName() + "</code>", kyb.backBtn);
            eventCode(user, "get edit channel name");
        } else if (text.equals("🔗 Kanal usernamesini o‘zgartirish")) {
            Channel channel = channel(user);
            bot.sendMessage(user.getChatId(), "Yangi kanal usernamesini kiriting, eski nomi : <code>" + channel.getUsername() + "</code>", kyb.backBtn, true);
            eventCode(user, "get edit channel username");
        } else if (text.equals("🗑️ O‘chirish")) {
            Channel channel = channel(user);
            bot.sendMessage(user.getChatId(), "\uD83D\uDCE2 *Kanal nomi:* %s\n\uD83D\uDD17 *Username:* %s\n\nHaqiqatdan ham ushbu kanalni o'chirmoqchimisiz ⬇️".formatted(channel.getName(), channel.getUsername()), kyb.isSuccess("uz"));
            eventCode(user, "is channel delete");
        } else {
            Channel channel = channelRepository.findByNameAndActiveIsTrue(text);
            if (channel == null) {
                errorBtn(user.getChatId(), kyb.getChannels(channelRepository.findAllByActiveIsTrueAndStatusOrderByIdAsc(Status.OPEN)));
            } else {
                user.setChannelId(channel.getId());
                userService.save(user);
                bot.sendMessage(user.getChatId(), "\uD83D\uDCE2 *Kanal nomi:* %s\n\uD83D\uDD17 *Username:* %s\n\nQuyidagi amallardan birini tanlang ⬇️".formatted(channel.getName(), channel.getUsername()), kyb.crudChannel());
            }
        }
    }

    private Channel channel(User user) {
        return channelRepository.findById(user.getChannelId()).orElse(null);
    }

    public void editChannel(User user, String text) {
        if (text.equals(backButton)) {
            channelsMenu(user, channel(user).getName());
            eventCode(user, "channels menu");
        } else {
            Channel channel = channel(user);
            String msg;
            if (user.getEventCode().equals("get edit channel name")) {
                channel.setName(text);
                try {
                    channelRepository.save(channel);
                } catch (Exception e) {
                    bot.sendMessage(
                            user.getChatId(),
                            "❗️Bu nom allaqachon mavjud. Iltimos, boshqa nom kiriting.",
                            kyb.backBtn
                    );
                    return;
                }
                msg = "✅ Kanal nomi muvaffaqiyatli o'zgartirildi!";
            } else if (user.getEventCode().equals("get edit channel username")) {
                if (text.charAt(0) == '@') {
                    bot.sendMessage(
                            user.getChatId(),
                            "❌ Siz usernamesini noto‘g‘ri formatda kiritdingiz.\n\nIltimos, kanal usernamesini quyidagi tartibda kiriting:\n\n📌 Agar kanal usernamesi @username ko‘rinishida bo‘lsa, faqat `username` qismini kiriting.",
                            kyb.backBtn
                    );
                    return;
                }
                channel.setUsername("@" + text);
                channel.setLink("https://t.me/" + text);
                channelRepository.save(channel);
                msg = "✅ Kanal usernamsi muvaffaqiyatli o'zgartirildi!";
            } else return;
            bot.sendMessage(user.getChatId(), msg);
            channelsMenu(user, channel(user).getName());
        }
    }

    public void isChannelDelete(User user, String text) {
        if (text.equals("❌ Yo'q")) {
            bot.sendMessage(user.getChatId(), "Operatsiya bekor qilindi ❗");
            channelsMenu(user, channel(user).getName());
            eventCode(user, "channels menu");
        } else if (text.equals("✅ Ha")) {
            Channel channel = channel(user);
            channel.setActive(false);
            channel.setStatus(Status.DRAFT);
            channel.setName(UUID.randomUUID().toString() + channel.getId());
            channel.setUsername(UUID.randomUUID().toString() + channel.getId());
            channelRepository.save(channel);
            bot.sendMessage(user.getChatId(), "Kanalingiz muvaffaqiyatli o'chirildi ✅");
            menu(user, botMakerAdminMenu[4]);
        } else {
            wrongBtn(user, kyb.isSuccess("uz"));
        }
    }

    public void addChannel(User user, String text, String eventCode) {
        if (text.equals(backButton)) {
            menu(user, botMakerAdminMenu[4]);
        } else {
            String msg;
            ReplyKeyboardMarkup markup = null;
            boolean isFinished = false;
            Channel channel = channelRepository.getDraft();
            if (channel == null) {
                channel = new Channel();
                channel.setActive(false);
                channel.setStatus(Status.DRAFT);
            }

            switch (eventCode) {
                case "get new channel name" -> {
                    channel.setName(text);
                    try {
                        channelRepository.save(channel);
                        msg = "Muvaffaqiyatli saqlandi, endi ushbu kanal usernamesini kiriting";
                        eventCode = "add new channel username";
                    } catch (Exception e) {
                        log.error(e);
                        msg = "Bu nom band, iltimos boshqa nom kiriting";
                    }
                }
                case "add new channel username" -> {
                    if (text.charAt(0) == '@') {
                        msg = "Username noto'g'ri formatda kiritdingiz, misol uchun @kanal_username si korinishda bolsa kanal_username deb kiritishingiz kerak";
                    } else {
                        channel.setUsername("@" + text);
                        channel.setLink("https://t.me/" + text);
                        channelRepository.save(channel);
                        msg = "Muvaffaqiyatli saqlandi, \n\nKanal nomi: %s\nKanal usernamesi: @%s\n\nushbu kanalni haqiqatdan ham qo'shmoqchimisiz".formatted(channel.getName(), channel.getUsername());
                        eventCode = "is add channel";
                        markup = kyb.isSuccess("uz");
                    }
                }
                case "is add channel" -> {
                    if (text.equals("✅ Ha")) {
                        channel.setStatus(Status.OPEN);
                        channel.setActive(true);
                        channelRepository.save(channel);
                        msg = "Muvaffaqoyatli qo'shildi";
                    } else if (text.equals("❌ Yo'q")) {
                        msg = "Operatsiya bekor qilindi o'chirildi!";
                    } else return;
                    isFinished = true;
                }
                default -> {
                    return;
                }
            }
            /// ///
            bot.sendMessage(user.getChatId(), msg, markup);
            eventCode(user, eventCode);
            if (isFinished) {
                menu(user, botMakerAdminMenu[4]);
            }
        }
    }

    public void checkService(User user, String data, Integer messageId, CallbackQuery callbackQuery) {
        Long chatId = Long.valueOf(data.split("_")[1]);
        user.setHelperChatId(chatId);
        User currentUser = userService.checkUser(chatId).getData();
        userService.save(user);

        if (data.startsWith("checkConfirm")) {
            bot.sendMessage(user.getChatId(), "💵 Balans miqdorini kiriting:", true);
            eventCode(user, "check");
        } else {
            bot.sendMessage(user.getChatId(), "❌ Sababini kiriting:", true);
            eventCode(user, "cancel check");
        }
    }


    public void check(User user, String text) {
        if (text.equals(backButton)) {
            start(user);
            return;
        }

        Long chatId = user.getHelperChatId();
        User currentUser = userService.checkUser(chatId).getData();
        double balance = Double.parseDouble(text);

        // Balansni yangilash
        currentUser.setBalance(currentUser.getBalance() == null ? balance : currentUser.getBalance() + balance);
        userService.save(currentUser);

        // Foydalanuvchiga xabar yuborish
        bot.sendMessage(chatId, "💸 Sizning hisobingizga %s qo'shildi\n\n📊 Umumiy balansingiz: %s ga yetdi".formatted(formatPrice(balance), formatPrice(currentUser.getBalance())));

        // Admin yoki boshqa foydalanuvchiga xabar yuborish
        bot.sendMessage(user.getChatId(), "📈 Ushbu foydalanuvchining umumiy balansi: %s ga yetdi".formatted(formatPrice(currentUser.getBalance())), kyb.backBtn);
    }


    public void cancelCheck(User user, String text) {
        if (text.equals(backButton)) {
            start(user);
            return;
        }

        Long chatId = user.getHelperChatId();

        // Foydalanuvchiga xabar yuborish
        bot.sendMessage(chatId, "❌ Chekingiz qabul qilinmadi\n\nBuning sabab: %s".formatted(text));

        // Admin yoki boshqa foydalanuvchiga xabar yuborish
        bot.sendMessage(user.getChatId(), "✅ Foydalanuvchiga ushbu xabar yuborildi\n\n❌ Chekingiz qabul qilinmadi\n\nBuning sabab: %s".formatted(text), kyb.backBtn);
    }

    public void reply(User user, CallbackQuery callbackQuery, Integer messageId, String data) {
        Long chatId = Long.valueOf(data.split("_")[1]);
        user.setHelperChatId(chatId);
        userService.save(user);
        bot.sendMessage(user.getChatId(), "✍️ Iltimos, javobingizni yozing:", kyb.setKeyboards(new String[]{backButton}, 1));
        eventCode(user, "reply");
    }

    public void reply(User user, String text) {
        // Foydalanuvchiga admin javobi
        if (text.equals(backButton)) {
            start(user);
            return;
        }
        bot.sendMessage(
                user.getHelperChatId(),
                "📬 <b>Admindan sizga javob keldi:</b>\n\n<i>" + text + "</i>",
                kyb.replyBtn(user.getChatId(), "uz")
        );

        // Adminga tasdiq xabari
        bot.sendMessage(
                user.getChatId(),
                "✅ Xabaringiz foydalanuvchiga muvaffaqiyatli yetkazildi!",
                kyb.menu
        );

        eventCode(user, "menu");
    }

    public void reklama(User user, Integer messageId) {
        user.setMessageId(messageId);
        user.setEventCode("user has reply");
        bot.sendMessage(user.getChatId(), "Ushbu reklamaga javob yozish imkoniyati qo'shilsinmi ?", kyb.isSuccess("uz"));
        userService.save(user);
    }

    public void userHasReply(User user, String text) {
        boolean hasReply;
        if (text.equals("✅ Ha")) {
            hasReply = true;
        } else if (text.equals("❌ Yo'q")) {
            hasReply = false;
        } else return;
        List<User> users = userService.findAll().getData();
        long count = 0;
        for (User u : users) {
            try {
                InlineKeyboardMarkup markup = kyb.replyBtn(user.getChatId(), "uz");
                bot.copyMessage(u.getChatId(), user.getChatId(), user.getMessageId(), hasReply ? markup : null);
                count++;
            } catch (Exception e) {
                log.error(e);
            }
        }
        bot.sendMessage(
                user.getChatId(),
                "📨 Sizning xabaringiz <b>%d</b> kishiga yuborildi.\n✅ Ulardan <b>%d</b> nafari muvaffaqiyatli qabul qildi."
                        .formatted(users.size(), count)
        );
        start(user);
    }
}