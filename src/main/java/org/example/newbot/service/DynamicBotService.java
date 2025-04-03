package org.example.newbot.service;

import lombok.extern.log4j.Log4j2;
import org.example.newbot.bot.TelegramBot;
import org.example.newbot.bot.online_magazine_bot.admin.AdminOnlineMagazineFunction;
import org.example.newbot.bot.online_magazine_bot.OnlineMagazineBot;
import org.example.newbot.bot.online_magazine_bot.admin.AdminOnlineMagazineKyb;
import org.example.newbot.bot.online_magazine_bot.user.UserOnlineMagazineFunction;
import org.example.newbot.bot.online_magazine_bot.user.UserOnlineMagazineKyb;
import org.example.newbot.dto.ResponseDto;
import org.example.newbot.model.BotInfo;
import org.example.newbot.repository.BotInfoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class DynamicBotService {
    private final List<BotInstance> activeBots = new ArrayList<>();
    private final BotInfoRepository botInfoRepository;
    private final BotUserService botUserService;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final ProductVariantService productVariantService;

    @Value("${size}")
    public int size;
    @Value("${admin.chat.id}")
    public Long adminChatId;


    public DynamicBotService(BotInfoRepository botInfoRepository, BotUserService botUserService, CategoryService categoryService, ProductService productService, ProductVariantService productVariantService) {
        this.botInfoRepository = botInfoRepository;
        this.botUserService = botUserService;
        this.categoryService = categoryService;
        this.productService = productService;
        this.productVariantService = productVariantService;

    }


    private static class BotInstance {
        private final Long botId;
        private final TelegramLongPollingBot bot;
        private final BotSession session;
        private final String username;

        public BotInstance(Long botId, TelegramLongPollingBot bot, BotSession session, String username) {
            this.botId = botId;
            this.bot = bot;
            this.session = session;
            this.username = username;
        }

        public Long getBotId() {
            return botId;
        }

        public TelegramLongPollingBot getBot() {
            return bot;
        }

        public BotSession getSession() {
            return session;
        }

        public String getUsername() {
            return username;
        }
    }

    public String createAndRegisterBot(TelegramBotsApi botsApi, BotInfo botInfo) {
        try {
            TelegramLongPollingBot bot = new TelegramLongPollingBot(botInfo.getBotToken()) {
                @Override
                public String getBotUsername() {
                    // Avval saqlangan username ni qaytarish
                    return findBotById(botInfo.getId())
                            .map(BotInstance::getUsername)
                            .orElseGet(() -> {
                                String username = fetchBotUsername(this);
                                if (username == null) {
                                    return null;
                                }
                                log.info("Yangi username olingan: {}", username);
                                botInfo.setBotUsername(username);
                                botInfoRepository.save(botInfo);
                                return username;
                            });
                }

                @Override
                public void onUpdateReceived(Update update) {
                    handleUpdate(update, botInfo.getId());
                }
            };

            // Bot username ni olish
            String username = fetchBotUsername(bot);

            BotSession session = botsApi.registerBot(bot);
            activeBots.add(new BotInstance(botInfo.getId(), bot, session, username));
            log.info("Bot qo'shildi: {} (@{})", botInfo.getId(), username);
            return username;
        } catch (Exception e) {
            log.error("Bot yaratishda xato: {}", e.getMessage(), e);
            return null;
        }
    }

    public String createAndRegisterBot(TelegramBotsApi botsApi, BotInfo botInfo, int messageId, Long chatId, String msg, TelegramBot bot1) {
        try {
            TelegramLongPollingBot bot = new TelegramLongPollingBot(botInfo.getBotToken()) {
                @Override
                public String getBotUsername() {
                    // Avval saqlangan username ni qaytarish
                    return findBotById(botInfo.getId())
                            .map(BotInstance::getUsername)
                            .orElseGet(() -> {
                                bot1.editMessageText(chatId, messageId + 1, msg);
                                String username = fetchBotUsername(this);
                                if (username == null) {
                                    return null;
                                }
                                log.info("Yangi username olingan: {}", username);
                                botInfo.setBotUsername(username);
                                botInfoRepository.save(botInfo);
                                return username;
                            });
                }

                @Override
                public void onUpdateReceived(Update update) {
                    handleUpdate(update, botInfo.getId());
                }
            };

            // Bot username ni olish
            String username = fetchBotUsername(bot);

            BotSession session = botsApi.registerBot(bot);
            activeBots.add(new BotInstance(botInfo.getId(), bot, session, username));
            log.info("Bot qo'shildi: {} (@{})", botInfo.getId(), username);
            return username;
        } catch (Exception e) {
            log.error("Bot yaratishda xato: {}", e.getMessage(), e);
            return null;
        }
    }

    private String fetchBotUsername(TelegramLongPollingBot bot) {
        try {
            GetMe getMe = new GetMe();
            User botUser = bot.execute(getMe);
            return botUser.getUserName();
        } catch (TelegramApiException e) {
            log.error("Username olishda xato: {}", e.getMessage(), e);
            return null;
        }
    }

    private void handleUpdate(Update update, Long botId) {
        Long chatId;
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getFrom().getId();
        } else {
            log.warn("Not supported update type");
            return;
        }
        Optional<BotInfo> bOp = botInfoRepository.findById(botId);
        if (bOp.isEmpty())
            return;
        BotInfo botInfo = bOp.get();
        if (botInfo.getType().equals("online-magazine")) {
            OnlineMagazineBot onlineMagazineBot = new OnlineMagazineBot(
                    this, botInfoRepository,
                    botUserService, new AdminOnlineMagazineFunction(
                    botInfoRepository, botUserService, this,
                    new AdminOnlineMagazineKyb(), categoryService,
                    productService, productVariantService
            ), new UserOnlineMagazineFunction(
                    botUserService, this, new UserOnlineMagazineKyb(),
                    categoryService, productService, productVariantService
            ));
            onlineMagazineBot.onlineMagazineBotMenu(botInfo, chatId, update, adminChatId);
        } else if (botInfo.getType().equals("logistic-bot")) {
            sendMessage(botId, chatId, "online logistik xush kelibsiz");
        } else return;
    }

    public ResponseDto<Void> sendMessage(Long botId, Long chatId, String text) {
        Optional<BotInstance> botInstance = findBotById(botId);
        if (botInstance.isEmpty()) {
            log.warn("Bot topilmadi, xabar yuborish imkonsiz. Bot ID: {}", botId);
            return new ResponseDto<>(false, "Bot topilmadi, xabar yuborish imkonsiz. Bot ID: %s}".formatted(botId));
        }
        try {
            SendMessage message = new SendMessage();
            message.setChatId(chatId.toString());
            message.setText(text);
            message.enableHtml(true);
            botInstance.get().getBot().execute(message);
            return new ResponseDto<>(true, "Ok");
        } catch (TelegramApiException e) {
            log.error("Xabar yuborishda xato. Bot ID: {}, Chat ID: {}. Xato: {}",
                    botId, chatId, e.getMessage(), e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    public ResponseDto<Void> sendMessage(Long botId, Long chatId, String text, boolean b) {
        Optional<BotInstance> botInstance = findBotById(botId);
        if (botInstance.isEmpty()) {
            log.warn("Bot topilmadi, xabar yuborish imkonsiz. Bot ID: {}", botId);
            return new ResponseDto<>(false, "Bot topilmadi, xabar yuborish imkonsiz. Bot ID: %s}".formatted(botId));
        }
        try {
            SendMessage message = new SendMessage();
            message.setChatId(chatId.toString());
            message.setText(text);
            message.enableHtml(true);
            message.setReplyMarkup(new ReplyKeyboardRemove(b));
            botInstance.get().getBot().execute(message);
            return new ResponseDto<>(true, "Ok");
        } catch (TelegramApiException e) {
            log.error("Xabar yuborishda xato. Bot ID: {}, Chat ID: {}. Xato: {}",
                    botId, chatId, e.getMessage(), e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    public ResponseDto<Void> sendMessage(Long botId, Long chatId, String text, ReplyKeyboardMarkup markup) {
        Optional<BotInstance> botInstance = findBotById(botId);
        if (botInstance.isEmpty()) {
            log.warn("Bot topilmadi, xabar yuborish imkonsiz. Bot ID: {}", botId);
            return new ResponseDto<>(false, "Bot topilmadi, xabar yuborish imkonsiz. Bot ID: %s}".formatted(botId));
        }

        try {
            SendMessage message = new SendMessage();
            message.setChatId(chatId.toString());
            message.setText(text);
            message.setReplyMarkup(markup);
            message.enableHtml(true);
            botInstance.get().getBot().execute(message);
            return new ResponseDto<>(true, "Ok");
        } catch (TelegramApiException e) {
            log.error("Xabar yuborishda xato. Bot ID: {}, Chat ID: {}. Xato: {}",
                    botId, chatId, e.getMessage(), e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    public ResponseDto<Void> deleteMessage(Long botId, Long chatId, Integer messageId) {
        Optional<BotInstance> botInstance = findBotById(botId);
        if (botInstance.isEmpty()) {
            log.warn("Bot topilmadi, xabar yuborish imkonsiz. Bot ID: {}", botId);
            return new ResponseDto<>(false, "Bot topilmadi, xabar yuborish imkonsiz. Bot ID: %s}".formatted(botId));
        }
        try {
            DeleteMessage message = new DeleteMessage("" + chatId, messageId);
            botInstance.get().getBot().execute(message);
            return new ResponseDto<>(true, "Ok");
        } catch (TelegramApiException e) {
            log.error("Xabar yuborishda xato. Bot ID: {}, Chat ID: {}. Xato: {}",
                    botId, chatId, e.getMessage(), e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    public ResponseDto<Void> editMessageText(Long botId, Long chatId, Integer messageId, String text, InlineKeyboardMarkup markup) {
        Optional<BotInstance> botInstance = findBotById(botId);
        if (botInstance.isEmpty()) {
            log.warn("Bot topilmadi, xabar yuborish imkonsiz. Bot ID: {}", botId);
            return new ResponseDto<>(false, "Bot topilmadi, xabar yuborish imkonsiz. Bot ID: %s}".formatted(botId));
        }
        try {
            EditMessageText message = new EditMessageText();
            message.setChatId(chatId.toString());
            message.setText(text);
            message.setMessageId(messageId);
            message.setReplyMarkup(markup);
            message.enableHtml(true);
            botInstance.get().getBot().execute(message);
            return new ResponseDto<>(true, "Ok");
        } catch (TelegramApiException e) {
            log.error("Xabar yuborishda xato. Bot ID: {}, Chat ID: {}. Xato: {}",
                    botId, chatId, e.getMessage(), e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    public ResponseDto<Void> editCaption(Long botId, Long chatId, Integer messageId, String text, InlineKeyboardMarkup markup) {
        Optional<BotInstance> botInstance = findBotById(botId);
        if (botInstance.isEmpty()) {
            log.warn("Bot topilmadi, xabar yuborish imkonsiz. Bot ID: {}", botId);
            return new ResponseDto<>(false, "Bot topilmadi, xabar yuborish imkonsiz. Bot ID: %s}".formatted(botId));
        }
        try {
            EditMessageCaption message = new EditMessageCaption();
            message.setChatId(chatId.toString());
            message.setCaption(text);
            message.setMessageId(messageId);
            message.setReplyMarkup(markup);
            message.setParseMode("html");
            botInstance.get().getBot().execute(message);
            return new ResponseDto<>(true, "Ok");
        } catch (TelegramApiException e) {
            log.error("Xabar yuborishda xato. Bot ID: {}, Chat ID: {}. Xato: {}",
                    botId, chatId, e.getMessage(), e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    public ResponseDto<Void> editMessageText(Long botId, Long chatId, Integer messageId, String text) {
        Optional<BotInstance> botInstance = findBotById(botId);
        if (botInstance.isEmpty()) {
            log.warn("Bot topilmadi, xabar yuborish imkonsiz. Bot ID: {}", botId);
            return new ResponseDto<>(false, "Bot topilmadi, xabar yuborish imkonsiz. Bot ID: %s}".formatted(botId));
        }
        try {
            EditMessageText message = new EditMessageText();
            message.setChatId(chatId.toString());
            message.setText(text);
            message.setMessageId(messageId);
            message.enableHtml(true);
            botInstance.get().getBot().execute(message);
            return new ResponseDto<>(true, "Ok");
        } catch (TelegramApiException e) {
            log.error("Xabar yuborishda xato. Bot ID: {}, Chat ID: {}. Xato: {}",
                    botId, chatId, e.getMessage(), e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    public ResponseDto<Void> alertMessage(Long botId, CallbackQuery callbackQuery, String alertMessageText) {
        // 1. Botni ID bo'yicha qidirish
        Optional<BotInstance> botInstance = findBotById(botId);

        // 2. Agar bot topilmasa
        if (botInstance.isEmpty()) {
            String xatoXabari = "Bot topilmadi. Bot ID: " + botId;
            log.warn(xatoXabari);
            return new ResponseDto<>(false, xatoXabari);
        }

        try {
            // 3. Ogohlantirish xabarini tayyorlash
            AnswerCallbackQuery answer = new AnswerCallbackQuery();
            answer.setCallbackQueryId(callbackQuery.getId());
            answer.setText(alertMessageText);
            answer.setShowAlert(true); // Foydalanuvchiga popup tarzida ko'rinadi

            // 4. Xabarni yuborish
            botInstance.get().getBot().execute(answer);

            return new ResponseDto<>(true, "Ogohlantirish xabari muvaffaqiyatli yuborildi");

        } catch (TelegramApiException e) {
            String xatoTafsiloti = "Xabar yuborishda xatolik yuz berdi. Sabab: " + e.getMessage();
            log.error("Bot ID: {}, Xato: {}", botId, xatoTafsiloti, e);
            return new ResponseDto<>(false, xatoTafsiloti);
        }
    }

    public ResponseDto<Void> sendMessage(Long botId, Long chatId, String text, InlineKeyboardMarkup markup) {
        Optional<BotInstance> botInstance = findBotById(botId);
        if (botInstance.isEmpty()) {
            log.warn("Bot topilmadi, xabar yuborish imkonsiz. Bot ID: {}", botId);
            return new ResponseDto<>(false, "Bot topilmadi, xabar yuborish imkonsiz. Bot ID: %s".formatted(botId));
        }
        try {
            SendMessage message = new SendMessage();
            message.setChatId(chatId.toString());
            message.setText(text);
            message.setReplyMarkup(markup);
            message.enableHtml(true);
            botInstance.get().getBot().execute(message);
            return new ResponseDto<>(true, "Ok");
        } catch (TelegramApiException e) {
            log.error("Xabar yuborishda xato. Bot ID: {}, Chat ID: {}. Xato: {}",
                    botId, chatId, e.getMessage(), e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    public ResponseDto<Void> sendPhoto(Long botId, Long chatId, String fileId,
                                       InlineKeyboardMarkup markup, boolean protectContent, String caption
    ) {
        Optional<BotInstance> botInstance = findBotById(botId);
        if (botInstance.isEmpty()) {
            log.warn("Bot topilmadi, xabar yuborish imkonsiz. Bot ID: {}", botId);
            return new ResponseDto<>(false, "Bot topilmadi, xabar yuborish imkonsiz. Bot ID: %s}".formatted(botId));
        }
        try {
            SendPhoto message = new SendPhoto();
            message.setChatId(chatId.toString());
            message.setPhoto(new InputFile(fileId));
            message.setReplyMarkup(markup);
            message.setCaption(caption);
            message.setProtectContent(protectContent);
            botInstance.get().getBot().execute(message);
            return new ResponseDto<>(true, "Ok");
        } catch (TelegramApiException e) {
            log.error("Xabar yuborishda xato. Bot ID: {}, Chat ID: {}. Xato: {}",
                    botId, chatId, e.getMessage(), e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    public ResponseDto<Void> sendPhoto(Long botId, Long chatId, String fileId,
                                       ReplyKeyboardMarkup markup, boolean protectContent, String caption
    ) {
        Optional<BotInstance> botInstance = findBotById(botId);
        if (botInstance.isEmpty()) {
            log.warn("Bot topilmadi, xabar yuborish imkonsiz. Bot ID: {}", botId);
            return new ResponseDto<>(false, "Bot topilmadi, xabar yuborish imkonsiz. Bot ID: %s}".formatted(botId));
        }
        try {
            SendPhoto message = new SendPhoto();
            message.setChatId(chatId.toString());
            message.setPhoto(new InputFile(fileId));
            message.setReplyMarkup(markup);
            message.setCaption(caption);
            message.setProtectContent(protectContent);
            botInstance.get().getBot().execute(message);
            return new ResponseDto<>(true, "Ok");
        } catch (TelegramApiException e) {
            log.error("Xabar yuborishda xato. Bot ID: {}, Chat ID: {}. Xato: {}",
                    botId, chatId, e.getMessage(), e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    public ResponseDto<Void> editMessageMedia(Long botId, Long chatId, Integer messageId, InlineKeyboardMarkup markup, String caption, String photoId) {
        Optional<BotInstance> botInstance = findBotById(botId);
        if (botInstance.isEmpty()) {
            log.warn("Bot topilmadi, xabar yuborish imkonsiz. Bot ID: {}", botId);
            return new ResponseDto<>(false, "Bot topilmadi, xabar yuborish imkonsiz. Bot ID: %s".formatted(botId));
        }
        try {
            EditMessageMedia message = new EditMessageMedia();
            message.setChatId(chatId.toString());
            message.setMessageId(messageId);
            InputMediaPhoto mediaPhoto = new InputMediaPhoto();
            mediaPhoto.setMedia(photoId);
            mediaPhoto.setCaption(caption);
            message.setMedia(mediaPhoto);
            message.setReplyMarkup(markup);
            botInstance.get().getBot().execute(message);
            return new ResponseDto<>(true, "Ok");
        } catch (TelegramApiException e) {
            log.error("Xabar yuborishda xato. Bot ID: {}, Chat ID: {}. Xato: {}",
                    botId, chatId, e.getMessage(), e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }


    public void stopBot(Long botId) {
        Optional<BotInstance> botInstance = findBotById(botId);
        botInstance.ifPresent(instance -> {
            instance.getSession().stop();
            activeBots.remove(instance);
            log.info("Bot to'xtatildi: {} (@{})", botId, instance.getUsername());
        });
    }

    public boolean isBotRunning(Long botId) {
        return findBotById(botId).isPresent();
    }

    public Optional<String> getBotUsername(Long botId) {
        return findBotById(botId).map(BotInstance::getUsername);
    }

    private Optional<BotInstance> findBotById(Long botId) {
        return activeBots.stream()
                .filter(bot -> bot.getBotId().equals(botId))
                .findFirst();
    }
}