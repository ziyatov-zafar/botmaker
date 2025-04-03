package org.example.newbot.bot;

import org.example.newbot.model.User;
import org.example.newbot.service.UserService;
import org.springframework.stereotype.Controller;

@Controller
public class Function {
    private final UserService userService;

    public Function(UserService userService) {
        this.userService = userService;
    }

    public void eventCode(User user , String eventCode) {
        user.setEventCode(eventCode);
        userService.save(user);
    }
}
