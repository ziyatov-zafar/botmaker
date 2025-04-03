package org.example.newbot.bot.roleadmin;

import org.example.newbot.bot.StaticVariable;
import org.example.newbot.model.User;
import org.telegram.telegrambots.meta.api.objects.Message;
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
                    if (eventCode.equals("menu")) {
                        function.menu(user, text);
                    } else if (eventCode.equals("bot menu")) {
                        function.botMenu(user, text);
                    } else if (eventCode.equals("get bot type")) {
                        function.getBotType(user, text);
                    } else if (eventCode.equals("get bot token")) {
                        function.getBotToken(user, text,message.getMessageId());
                    }
                }
            }
        }
    }
}
