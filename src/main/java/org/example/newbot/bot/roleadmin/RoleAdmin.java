package org.example.newbot.bot.roleadmin;

import org.example.newbot.bot.StaticVariable;
import org.example.newbot.model.User;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;

public class RoleAdmin {
    private final AdminFunction function;

    public RoleAdmin(AdminFunction adminFunction) {
        this.function = adminFunction;
    }

    public void menu(User user, Update update) {
        String eventCode = user.getEventCode();
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.hasText()) {
                String text = message.getText();
                if (text.equals("/start") || text.equals(StaticVariable.mainMenu)) {
                    function.start(user);
                } else {
                    switch (eventCode) {
                        case "menu" -> function.menu(user, text);
                        case "bot menu" -> function.botMenu(user, text);
                        case "get bot type" -> function.getBotType(user, text);
                        case "get bot token" -> function.getBotToken(user, text, message.getMessageId());
                        case "choose bot list type" -> function.chooseBotListType(user, text);
                        case "add bot admin chat id" -> function.addBotAdminChatId(user, text);
                        case "search bots" -> function.searchBots(user, text);
                        case "user page", "addBalance" -> function.userPage(user, text);
                        case "search users", "addBalance1" -> function.searchUsers(user, text);
                        case "settings menu" -> function.settingsMenu(user, text);
                        case "edit bots menu" -> function.editBotsMenu(user, text);
                        case "edit bot price", "edit bot desc", "edit bot type" ->
                                function.editBot(user, text, eventCode);
                        case "edit card number", "edit card img", "edit card owner", "edit card type" ->
                                function.editCard(user, text, eventCode);
                        case "channels menu" -> function.channelsMenu(user, text);
                        case "get edit channel username", "get edit channel name" -> function.editChannel(user, text);
                        case "is channel delete" -> function.isChannelDelete(user, text);
                        case "get new channel name", "add new channel username", "is add channel" ->
                                function.addChannel(user, text, eventCode);
                        case "check" -> function.check(user , text);
                        case "cancel check" -> function.cancelCheck(user , text);
                        case "reply" -> function.reply(user , text);
                    }
                }
            } else if (message.hasPhoto()) {
                PhotoSize photo = message.getPhoto().get(message.getPhoto().size() - 1);
                if (eventCode.equals("edit card img")) function.editCardImg(user, photo);
            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String data = callbackQuery.getData();
            Integer messageId = callbackQuery.getMessage().getMessageId();
            boolean isReturn = data.startsWith("checkConfirm") || data.startsWith("checkCancelled");

            if (isReturn) {
                function.checkService(user, data, messageId, callbackQuery);
                return;
            }

            isReturn = data.startsWith("reply_");

            if (isReturn) {
                function.reply(user , callbackQuery,messageId,data);
                return;
            }
            switch (eventCode) {
                case "choose bot list type" -> function.chooseBotListType(user, data, callbackQuery, messageId, false);
                case "search bots" -> function.searchBots(user, data, messageId, callbackQuery);
                case "user page" -> function.userPage(user, data, messageId, callbackQuery);
                case "addBalance" -> function.addBalance(user, data, messageId, callbackQuery);
                case "addBalance1" -> function.addBalance1(user, data, messageId, callbackQuery);
                case "search users" -> function.searchUsers(user, data, messageId, callbackQuery);
            }
        }
    }
}
