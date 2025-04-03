package org.example.newbot.bot.roleuser;

import org.example.newbot.model.User;
import org.telegram.telegrambots.meta.api.objects.Message;
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
                    if (eventCode.equals("menu")) {
                        function.menu(user, text);
                    } else if (eventCode.equals("choose bot")) {
                        function.chooseBot(user, text);
                    }
                }
            }
        }
    }
}
