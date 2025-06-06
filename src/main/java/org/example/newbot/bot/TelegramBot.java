package org.example.newbot.bot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.example.newbot.bot.roleadmin.AdminFunction;
import org.example.newbot.bot.roleadmin.AdminKyb;
import org.example.newbot.bot.roleadmin.RoleAdmin;
import org.example.newbot.bot.roleuser.RoleUser;
import org.example.newbot.bot.roleuser.UserFunction;
import org.example.newbot.bot.roleuser.UserKyb;
import org.example.newbot.dto.ResponseDto;
import org.example.newbot.model.BotInfo;
import org.example.newbot.model.BotUser;
import org.example.newbot.model.Channel;
import org.example.newbot.model.User;
import org.example.newbot.repository.BotInfoRepository;
import org.example.newbot.repository.BotPriceRepository;
import org.example.newbot.repository.ChannelRepository;
import org.example.newbot.repository.PaymentRepository;
import org.example.newbot.service.BotPriceService;
import org.example.newbot.service.DynamicBotService;
import org.example.newbot.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
@Getter
@RequiredArgsConstructor

public class TelegramBot extends TelegramLongPollingBot {

    private final UserService userService;
    private final UserKyb userKyb;
    private final AdminKyb adminKyb;
    private final BotPriceService botPriceService;
    private final DynamicBotService dynamicBotService;
    private final TelegramBotsApi telegramBotsApi;
    private final BotInfoRepository botInfoRepository;
    private final BotPriceRepository botPriceRepository;
    private final PaymentRepository paymentRepository;
    private final ChannelRepository channelRepository;
    @Value("${bot.token}")
    public String botToken;
    @Value("${bot.username}")
    public String botUsername;
    @Value("${size}")
    public int size;
    @Value("${admin.chat.id}")
    public Long superAdminChatId;


    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        String username, firstname, lastname, nickname;
        Long chatId;
        if (update.hasMessage()) {
            Message message = update.getMessage();
            username = message.getFrom().getUserName();
            firstname = message.getFrom().getFirstName();
            lastname = message.getFrom().getLastName();
            chatId = message.getChatId();

        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            username = callbackQuery.getFrom().getUserName();
            firstname = callbackQuery.getFrom().getFirstName();
            lastname = callbackQuery.getFrom().getLastName();
            chatId = callbackQuery.getFrom().getId();
        } else return;
        nickname = firstname + " " + (lastname != null ? lastname : "");
        ResponseDto<User> checkUser = userService.checkUser(chatId);
        User user;
        if (checkUser.isSuccess()) {
            user = checkUser.getData();
        } else {
            user = new User();
            user.setChatId(chatId);
            user.setEventCode("aLOM");
            user.setRole("user");
            user.setBalance(0.0);
            user.setHelperBalance(0.0);
            user.setPage(0);
        }
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setNickname(nickname);
        user.setUsername(username);
        userService.save(user);

        if (chatId.equals(superAdminChatId)) {
            new RoleAdmin(new AdminFunction(
                    this, userService, adminKyb,
                    botPriceService, dynamicBotService,
                    telegramBotsApi, botInfoRepository,
                    botPriceRepository, paymentRepository,
                    channelRepository
            )).menu(user, update);
        } else {
            user.setRole("user");
            String role = user.getRole();
            if (role.equals("user")) {
                new RoleUser(new UserFunction(
                        this, userService, userKyb,
                        botPriceService, dynamicBotService,
                        telegramBotsApi, botInfoRepository,
                        channelRepository, botPriceRepository,
                        paymentRepository
                )).menu(user, update);
            } else return;
        }
    }

    @SneakyThrows
    public void sendMessage(Long chatId, String text) {
        execute(
                SendMessage
                        .builder()
                        .chatId(chatId)
                        .text(text)
                        .parseMode(ParseMode.HTML)
                        .disableWebPagePreview(true)
                        .build()
        );
    }

    @SneakyThrows
    public void sendMessage(Long chatId, String text, ReplyKeyboardMarkup markup) {
        execute(
                SendMessage
                        .builder()
                        .chatId(chatId)
                        .text(text)
                        .parseMode(ParseMode.HTML)
                        .replyMarkup(markup)
                        .disableWebPagePreview(true)
                        .build()
        );
    }

    public void sendMessageToChannel(String username, String text, InlineKeyboardMarkup markup) {
        try {
            execute(
                    SendMessage
                            .builder()
                            .chatId(username)
                            .text(text)
                            .parseMode(ParseMode.HTML)
                            .replyMarkup(markup)
                            .disableWebPagePreview(true)
                            .build()
            );
        } catch (Exception e) {
            log.error(e);
        }
    }


    @SneakyThrows
    public void sendMessage(Long chatId, String text, ReplyKeyboardMarkup markup, boolean protectContent) {
        execute(
                SendMessage
                        .builder()
                        .chatId(chatId)
                        .text(text)
                        .parseMode(ParseMode.HTML)
                        .replyMarkup(markup)
                        .disableWebPagePreview(true)
                        .protectContent(protectContent)
                        .build()
        );
    }

    @SneakyThrows
    public void sendMessage(Long chatId, String text, InlineKeyboardMarkup markup) {
        execute(
                SendMessage
                        .builder()
                        .chatId(chatId)
                        .text(text)
                        .parseMode(ParseMode.HTML)
                        .replyMarkup(markup)
                        .disableWebPagePreview(true)
                        .build()
        );
    }

    @SneakyThrows
    public void sendMessage(Long chatId, String text, Boolean remove) {
        execute(
                SendMessage
                        .builder()
                        .chatId(chatId)
                        .text(text)
                        .parseMode(ParseMode.HTML)
                        .replyMarkup(new ReplyKeyboardRemove(remove))
                        .disableWebPagePreview(true)
                        .build()
        );
    }

    @SneakyThrows
    public void alertMessage(CallbackQuery callbackQuery, String alertMessageText) {
        try {

            String callbackQueryId = callbackQuery.getId();
            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
            answerCallbackQuery.setShowAlert(true);
            answerCallbackQuery.setText(alertMessageText);
            answerCallbackQuery.setCallbackQueryId(callbackQueryId);

            execute(answerCallbackQuery);
        } catch (TelegramApiException e) {
            log.error(e);
        }
    }

    @SneakyThrows
    public void sendVideo(Long chatId, String fileId, String caption, InlineKeyboardMarkup markup, boolean isAdmin) {
        execute(
                SendVideo.builder()
                        .video(new InputFile(fileId))
                        .chatId(chatId)
                        .protectContent(!isAdmin)
                        .caption(caption)
                        .parseMode("HTML")
                        .replyMarkup(markup)
                        .build()
        );
    }

    public void deleteMessage(Long chatId, int messageId) {
        try {
            execute(DeleteMessage.builder().messageId(messageId).chatId(chatId).build());
        } catch (TelegramApiException e) {
            log.error(e);
        }
    }

    public void editMessageText(Long chatId, Integer messageId, String text, InlineKeyboardMarkup markup) {
        try {
            execute(EditMessageText.builder().chatId(chatId).messageId(messageId).text(text).replyMarkup(markup).parseMode("HTML").build());
        } catch (TelegramApiException e) {
            log.error(e);
        }
    }

    public void editMessageText(Long chatId, Integer messageId, String text) {
        try {
            execute(EditMessageText.builder().chatId(chatId).messageId(messageId).text(text).parseMode("HTML").build());
        } catch (TelegramApiException e) {
            log.error(e);
        }
    }

    private void setActions(BotInfo botInfo, Long chatId) {
        SendChatAction action = new SendChatAction();
        action.setChatId(chatId);
        action.setAction(ActionType.TYPING);
        try {
            execute(action);
        } catch (TelegramApiException e) {
            log.error(e);
        }
    }

    public void sendPhoto(Long chatId, String fileId, String caption, ReplyKeyboardMarkup markup) {
        try {
            execute(
                    SendPhoto
                            .builder()
                            .chatId(chatId)
                            .caption(caption)
                            .photo(new InputFile(fileId))
                            .parseMode("HTML")
                            .replyMarkup(markup)
                            .build()
            );
        } catch (Exception e) {
            log.error(e);
        }
    }

    public void sendPhoto(Long chatId, String fileId, String caption) {
        try {
            execute(
                    SendPhoto
                            .builder()
                            .chatId(chatId)
                            .caption(caption)
                            .photo(new InputFile(fileId))
                            .parseMode("HTML")
                            .build()
            );
        } catch (Exception e) {
            log.error(e);
        }
    }

    public void sendPhoto(Long chatId, String fileId, String caption, InlineKeyboardMarkup markup) {
        try {
            execute(
                    SendPhoto
                            .builder()
                            .chatId(chatId)
                            .caption(caption)
                            .photo(new InputFile(fileId))
                            .parseMode("HTML")
                            .replyMarkup(markup)
                            .build()
            );
        } catch (Exception e) {
            log.error(e);
        }
    }

    private boolean inArray(String s, String[] a) {
        for (String string : a) {
            if (string.equals(s)) return true;
        }
        return false;
    }

    public List<Channel> getChannels(long chatId) {
        List<Channel> list = channelRepository.findAllByActiveIsTrueAndStatusOrderByIdAsc(Status.OPEN);
        List<Channel> channels = new ArrayList<>(list.size());
        String[] types = {"member", "creator", "administrator"};
        for (Channel channel : list) {
            if (!inArray(getChatMember(channel.getUsername(), chatId), types)) {
                channels.add(channel);
            }
        }
        return channels;
    }

    public String getChatMember(String chatId, long userId) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://api.telegram.org/bot" + botToken + "/getChatMember" + "?chat_id=" + chatId + "&user_id=" + userId;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            if (rootNode.path("ok").asBoolean()) {
                return rootNode.path("result").path("status").asText();
            } else {
                return "error";
            }
        } catch (Exception e) {
            log.error(e);
            return "error";
        }
    }

    @SneakyThrows
    public void copyMessage(Long userChatId, Long fromChatId, Integer messageId, InlineKeyboardMarkup markup) {
        execute(CopyMessage
                .builder()
                .chatId(userChatId)
                .fromChatId(fromChatId)
                .messageId(messageId)
                .replyMarkup(markup)
                .build());

    }
}
