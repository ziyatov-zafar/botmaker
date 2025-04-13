package org.example.newbot.bot.online_course_bot.user;

import org.example.newbot.model.BotUser;
import org.example.newbot.model.User;

public class UserMsg {
    /**/
    public String contactBtn = "📱 Ro'yxatdan o'tish";
    public String menu = "📋 Siz asosiy menyudasiz";
    public String openMenu = "🔺 Asosiy menyu ochildi";





    public String requestContactForNewUser(BotUser user) {
        return """
            👋 Assalomu alaykum, %s!

            Botdan to‘liq foydalanish uchun iltimos, telefon raqamingizni ulashing va ro‘yxatdan o‘ting.
            """.formatted(user.getNickname());
    }
    public String wrongBtn = "❗️ Iltimos, tugmalardan foydalaning!";
}
