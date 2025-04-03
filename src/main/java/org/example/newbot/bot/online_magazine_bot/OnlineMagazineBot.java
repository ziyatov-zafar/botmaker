package org.example.newbot.bot.online_magazine_bot;

import lombok.extern.log4j.Log4j2;
import org.example.newbot.bot.online_magazine_bot.admin.AdminOnlineMagazineFunction;
import org.example.newbot.bot.online_magazine_bot.user.UserOnlineMagazineFunction;
import org.example.newbot.dto.Json;
import org.example.newbot.dto.ResponseDto;
import org.example.newbot.model.BotInfo;
import org.example.newbot.model.BotUser;
import org.example.newbot.repository.BotInfoRepository;
import org.example.newbot.service.BotUserService;
import org.example.newbot.service.DynamicBotService;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

import static org.example.newbot.bot.StaticVariable.mainMenu;
import static org.example.newbot.bot.StaticVariable.mainMenuRu;

@Log4j2
public class OnlineMagazineBot {
    private final DynamicBotService bot;
    private final BotInfoRepository botInfoRepository;
    private final BotUserService botUserService;
    private final AdminOnlineMagazineFunction adminFunction;
    private final UserOnlineMagazineFunction userFunction;

    public OnlineMagazineBot(DynamicBotService bot, BotInfoRepository botInfoRepository, BotUserService botUserService, AdminOnlineMagazineFunction adminFunction, UserOnlineMagazineFunction userFunction) {
        this.bot = bot;
        this.botInfoRepository = botInfoRepository;
        this.botUserService = botUserService;
        this.adminFunction = adminFunction;
        this.userFunction = userFunction;
    }

    public void onlineMagazineBotMenu(BotInfo botInfo, Long chatId, Update update, Long adminChatId) {
        boolean isAdmin = false;
        for (Long x : botInfo.getAdminChatIds()) {
            if (x.equals(chatId)) {
                isAdmin = true;
                break;
            }
        }
        ResponseDto<BotUser> checkUser = botUserService.findByUserChatId(chatId, botInfo.getId());
        String username, firstname, lastname, nickname;
        if (update.hasMessage()) {
            Message message = update.getMessage();
            username = message.getFrom().getUserName();
            firstname = message.getFrom().getFirstName();
            lastname = message.getFrom().getLastName();
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            username = callbackQuery.getFrom().getUserName();
            firstname = callbackQuery.getFrom().getFirstName();
            lastname = callbackQuery.getFrom().getLastName();
        } else return;
        nickname = firstname + " " + (lastname != null ? lastname : "");

        BotUser user;
        if (checkUser.isSuccess()) {
            user = checkUser.getData();
        } else {
            user = new BotUser();
            user.setChatId(chatId);
            user.setRole("user");
            user.setEventCode("online");
            List<BotInfo> bots = user.getBots();
            if (bots == null) bots = new ArrayList<>();
            bots.add(botInfo);
            List<BotUser> users = botInfo.getUsers();
            users.add(user);
            botUserService.save(user);
            botInfoRepository.save(botInfo);
        }
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setNickname(nickname);
        user.setUsername(username);
        botUserService.save(user);
        String eventCode = user.getEventCode();
        if (isAdmin || chatId.equals(adminChatId)) {
            if (!adminChatId.equals(user.getChatId())) user.setRole("admin");
            else user.setRole("user");
            if (update.hasMessage()) {
                Message message = update.getMessage();
                if (message.hasText()) {
                    String text = message.getText();
                    if (text.equals("/start")) {
                        adminFunction.start(botInfo, user);
                    } else {
                        if (text.equals(mainMenu)) {
                            adminFunction.start(botInfo, user);
                            return;
                        }
                        switch (eventCode) {
                            case "menu" -> adminFunction.menu(botInfo.getId(), user, text, botInfo, adminChatId);
                            case "users page" -> adminFunction.usersPage(botInfo.getId(), botInfo, user, text);
                            case "get all users", "get all block users" ->
                                    adminFunction.handleBackToUsersPage(botInfo.getId(), botInfo, user, text, eventCode);
                            case "searching users" -> adminFunction.searchingUsers(botInfo, user, text);
                            case "find by id" -> adminFunction.findById(botInfo, botInfo.getId(), user, text);
                            case "category menu" -> adminFunction.categoryMenu(botInfo, user, text);
                            case "add new category name uz" -> adminFunction.addNewCategoryNameUz(botInfo, user, text);
                            case "add new category name ru" -> adminFunction.addNewCategoryNameRu(botInfo, user, text);
                            case "crud category" ->
                                    adminFunction.crudCategory(botInfo, user, text, message.getMessageId());
                            case "edit_ru_name", "edit_uz_name" -> adminFunction.editName(botInfo, user, text);
                            case "product menu" -> adminFunction.productMenu(botInfo, user, text);
                            case "get new product name uz", "get new product name ru",
                                 "get new product desc uz", "get new product desc ru",
                                 "get new product variant name uz", "get new product variant name ru",
                                 "get new product variant price", "is add product to category" ->
                                    adminFunction.addProduct(botInfo, user, text);
                            case "get new product variant img" -> adminFunction.addProduct(botInfo, user, null);
                            case "is finished add product to category" ->
                                    adminFunction.isFinishedAddProductToCategory(botInfo, user, text);
                            case "crud product" -> adminFunction.crudProduct(botInfo, user, text);
                            case "edit name uz", "edit name ru", "edit desc uz", "edit desc ru", "edit price" ->
                                    adminFunction.editProductAndProductVariant(botInfo, botInfo.getId(), user, text);
                            case "add product variant to product get name uz",
                                 "add product variant to product get name ru",
                                 "add product variant to product get price",
                                 "is add product variant to product" ->
                                    adminFunction.addProductVariant(botInfo, user, text);
                            case "send msg" -> adminFunction.sendMsg(botInfo, user, text);
                            case "send msg text" -> adminFunction.sendMsgText(botInfo, user, text);
                            case "reply message" -> adminFunction.replyMessage(botInfo, user, text);
                        }
                    }
                } else if (message.hasPhoto()) {
                    String fileId = message.getPhoto().get(message.getPhoto().size() - 1).getFileId();
                    if (eventCode.equals("get new product variant img")) {
                        adminFunction.addProduct(botInfo, user, fileId);
                    } else if (eventCode.equals("add product variant to product get img")) {
                        adminFunction.addProductVariant(botInfo, user, fileId);
                    }
                }
            } else {
                CallbackQuery callbackQuery = update.getCallbackQuery();
                String data = callbackQuery.getData();
                Integer messageId = callbackQuery.getMessage().getMessageId();
                if (data.startsWith("reply")) {
                    userFunction.reply(botInfo, user.getChatId(), user, Long.valueOf(data.split("_")[1]), messageId, true);
                    return;
                }
                switch (eventCode) {
                    case "get all users" ->
                            adminFunction.getAllUsers(botInfo.getId(), botInfo, user, data, callbackQuery, messageId);
                    case "get all block users" ->
                            adminFunction.getAllBlockUsers(botInfo.getId(), botInfo, user, data, callbackQuery, messageId);
                    case "searching users" ->
                            adminFunction.searchingUsers(botInfo.getId(), botInfo, user, data, callbackQuery, messageId);
                    case "find by id" ->
                            adminFunction.findById(botInfo, botInfo.getId(), user, data, callbackQuery, messageId);
                    case "crud category" -> adminFunction.crudCategory(botInfo, user, data, messageId, callbackQuery);
                    case "product menu" -> adminFunction.productMenu(botInfo, user, data);
                    case "is finished add product to category" ->
                            adminFunction.isFinishedAddProductToCategory(botInfo, user, data, callbackQuery, messageId);
                    case "crud product" ->
                            adminFunction.crudProduct(botInfo, user, data, callbackQuery, messageId, false);

                }
            }
        } else {
            if (update.hasMessage()) {
                Message message = update.getMessage();
                if (message.hasText()) {
                    String text = message.getText();
                    if (text.equals("/start") || text.equals(mainMenu) || text.equals(mainMenuRu)) {
                        userFunction.start(botInfo, user, false);
                    } else {
                        switch (eventCode) {
                            case "reply message" -> userFunction.replyMessage(botInfo, user, text);
                            case "request_lang" -> userFunction.requestLang(botInfo, user, text);
                            case "request_contact" -> userFunction.requestContact(botInfo, user, text);
                            case "menu" -> userFunction.menu(botInfo, user, text);
                            case "commentToAdmin" -> userFunction.commentToAdmin(botInfo, user, text);
                            case "deliveryType" -> userFunction.deliveryType(botInfo, user, text);
                        }
                    }
                } else if (message.hasContact()) {
                    if (eventCode.equals("request_contact")) {
                        userFunction.requestContact(botInfo, user, message.getContact());
                    }
                } else if (message.hasLocation()) {
                    Location location = message.getLocation();

                }
            } else if (update.hasCallbackQuery()) {
                CallbackQuery callbackQuery = update.getCallbackQuery();
                String data = callbackQuery.getData();
                Integer messageId = callbackQuery.getMessage().getMessageId();
                if (data.startsWith("reply")) {
                    userFunction.reply(botInfo, user.getChatId(), user, Long.valueOf(data.split("_")[1]), messageId, false);
                } else {
                }
            }
        }
    }


}
