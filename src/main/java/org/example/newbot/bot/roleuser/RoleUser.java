package org.example.newbot.bot.roleuser;

import org.example.newbot.model.User;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.example.newbot.bot.StaticVariable.mainMenu;

public class RoleUser {
    private final UserFunction function;

    public RoleUser(UserFunction function) {
        this.function = function;
    }

    public void menu(User user, Update update) {
        String eventCode = user.getEventCode();
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.hasText()) {
                String text = message.getText();

                if (text.equals("/start")) {
                    function.start(user);
                } else {
                    if (text.equals(mainMenu)) {
                        function.start(user);
                        return;
                    }
                    if (function.isFinished(user, function.bot)) {
                        function.updateChannel(user, function.bot, function.kyb);
                        return;
                    }
                    switch (eventCode) {
                        case "menu" -> function.menu(user, text);
                        case "choose bot" -> function.chooseBot(user, text);
                        case "is buy bot" -> function.isBuyBot(user, text);
                        case "top up account" -> function.topUpAccount(user, text);
                        case "payment menu" -> function.paymentMenu(user, text);
                        case "admin to msg" -> function.adminToMsg(user, text);
                        case "reply" -> function.reply(user,text);
                    }
                }
            } else if (message.hasPhoto()) {
                PhotoSize photo = message.getPhoto().get(message.getPhoto().size() - 1);
                if (eventCode.equals("top up account") || eventCode.equals("payment menu"))
                    function.topUpAccount(user, photo);
            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String data = callbackQuery.getData();
            Integer messageId = callbackQuery.getMessage().getMessageId();
            if (data.startsWith("reply")) {
                function.reply(user,data,messageId,callbackQuery);
                return;
            }
            if (data.equals("success")) {
                if (function.success(callbackQuery, user, function.bot, function.kyb)) {
                    function.start(user);
                    return;
                }
            }

            if (eventCode.equals("menu")) {
//                if (data.startsWith("bot_")) {
                function.menu(user, data, messageId, callbackQuery);
//                }
            }

        }
    }
}
