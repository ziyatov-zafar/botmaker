package org.example.newbot.bot.online_magazine_bot.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.newbot.dto.Json;
import org.example.newbot.dto.ResponseDto;
import org.example.newbot.model.*;
import org.example.newbot.repository.BotInfoRepository;
import org.example.newbot.repository.BranchRepository;
import org.example.newbot.service.*;
import org.springframework.data.domain.Page;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.example.newbot.bot.StaticVariable.*;
import static org.example.newbot.bot.Status.DRAFT;
import static org.example.newbot.bot.Status.OPEN;

@RequiredArgsConstructor
@Log4j2

public class AdminOnlineMagazineFunction {
    private final BotInfoRepository botInfoRepository;
    private final BotUserService botUserService;
    private final DynamicBotService bot;
    private final AdminOnlineMagazineKyb kyb;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final ProductVariantService productVariantService;
    private final BranchRepository branchRepository;

    public void start(BotInfo botInfo, BotUser user) {
        List<Branch> branches = branchRepository.findAllByActiveIsTrueAndStatusAndBotIdOrderByIdAsc(OPEN, botInfo.getId());
        boolean emptyBranches = branches.isEmpty();
        if (emptyBranches) {
            bot.sendMessage(botInfo.getId(), user.getChatId(), """
                    📢 **Filiallar bo'limiga o'ting:**
                    
                    Hali birorta filial kiritmagansiz. Iltimos, filiallar bo'limiga o'tib, kerakli filiallarni kiritishingiz kerak. Aks holda botda xatoliklar yuzaga kelishi mumkin.
                    
                    ✅ **Filiallar qo'shish muhim!**
                    """);

        }
        bot.sendMessage(botInfo.getId(), user.getChatId(), "\uD83D\uDCCC *Asosiy menyuga xush kelibsiz!", kyb.menu);
        eventCode(user, "menu");
    }

    private void eventCode(BotUser user, String eventCode) {
        user.setPage(0);
        botUserService.save(user);
        user.setEventCode(eventCode);
        botUserService.save(user);
    }

    public void menu(Long botId, BotUser user, String text, BotInfo botInfo, Long adminChatId) {
        if (text.equals(adminOnlineMagazineMenu[0])) {
            bot.sendMessage(botId, user.getChatId(), text, kyb.usersPage());
            eventCode(user, "users page");
        } else if (text.equals(adminOnlineMagazineMenu[1])) {

        } else if (text.equals(adminOnlineMagazineMenu[2])) {
            ResponseDto<List<Category>> checkCategories = categoryService.findAllByBotId(botId);
            if (!checkCategories.isSuccess()) {
                errorResponseUserPage(user, botId, kyb.menu);
                return;
            }

            List<Category> categories = checkCategories.getData();

            if (categories.isEmpty()) {
                String message = "⚠️ Sizda hozircha hech qanday kategoriya mavjud emas.\nYangi kategoriya qo'shish uchun \"%s\" tugmasini bosing.".formatted(addCategory);
                bot.sendMessage(botId, user.getChatId(), message, kyb.setCategories(categories, true));
            } else {
                String message = "📂 Sizning mavjud kategoriyalaringiz (" + categories.size() + " ta):";
                bot.sendMessage(botId, user.getChatId(), message, kyb.setCategories(categories, false));
            }
            eventCode(user, "category menu");
        } else if (text.equals(adminOnlineMagazineMenu[3])) {
            List<BotUser> list = botUserService.findAll(botId).getData();
            bot.sendMessage(botId, user.getChatId(), UsersStatisticsForOnlineMagazine(list, adminChatId, botInfo.getBotUsername()), kyb.backBtn);
        } else if (text.equals(adminOnlineMagazineMenu[4])) {
            bot.sendMessage(botId, user.getChatId(), "Xabar yubormoqchi bo'lgan odamingizni chat id sini kiriting", kyb.backBtn);
            eventCode(user, "send msg");
        } else if (text.equals(adminOnlineMagazineMenu[5])) {
            bot.sendMessage(botId, user.getChatId(), text, kyb.branchMenu);
            eventCode(user, "branch menu");
        } else if (text.equals(backButton)) {
            start(botInfo, user);
        } else wrongBtn(botId, user.getChatId(), kyb.menu);
    }

    public void usersPage(Long botId, BotInfo botInfo, BotUser user, String text) {
        user.setPage(0);
        botUserService.save(user);
        if (text.equals(adminOnlineMagazineUsersPage[0])) {
            ResponseDto<Page<BotUser>> checkUsers = botUserService.findAll(user.getPage(), bot.size, botId);
            if (checkUsers.isSuccess()) {
                Page<BotUser> list = botUserService.findAll(user.getPage(), bot.size, botId).getData();
                bot.sendMessage(botInfo.getId(), user.getChatId(), text, kyb.backBtn);
                bot.sendMessage(botId, user.getChatId(), adminOnlineMagazineAboutUsers(list, botInfo.getBotUsername(), user.getPage(), bot.size), kyb.getUsers(list.getContent()));
                eventCode(user, "get all users");
            } else {
                errorResponseUserPage(user, botId);
            }
        } else if (text.equals(adminOnlineMagazineUsersPage[1])) {
            ResponseDto<Page<BotUser>> userPage = botUserService.findAllByRole(user.getPage(), bot.size, botId, "block");
            if (userPage.isSuccess()) {
                Page<BotUser> users = userPage.getData();
                if (users.isEmpty()) {
                    bot.sendMessage(botInfo.getId(), user.getChatId(), "Bloklangan foydalanuvchilar mavjud emas", kyb.usersPage());
                } else {
                    bot.sendMessage(botInfo.getId(), user.getChatId(), text, kyb.backBtn);
                    bot.sendMessage(botId, user.getChatId(), adminOnlineMagazineAboutUsers(users, botInfo.getBotUsername(), user.getPage(), bot.size), kyb.getUsers(users.getContent()));
                    eventCode(user, "get all block users");
                }
            } else errorResponseUserPage(user, botId);
        } else if (text.equals(adminOnlineMagazineUsersPage[2])) {
            String messageText = """
                    <b>🔍 Foydalanuvchi qidirish</b>
                    
                    Qidirish uchun quyidagilardan birini yuboring:
                    
                    • <b>Username</b> - <code>@foydalanuvchi</code> yoki <code>foydalanuvchi</code>
                    • <b>Nikname</b> - <code>nomi</code> (foydalanuvchi taxallusi)
                    • <b>Telefon</b> - <code>+998901234567</code> yoki <code>90123</code>
                    
                    <i>💡 Qidiruv xususiyatlari:</i>
                    - Katta-kichik harflar farqi yo'q
                    - Qisman moslik qo'llab-quvvatlanadi
                    - @, + kabi belgilarsiz ham ishlaydi
                    
                    <b>📌 Misollar:</b>
                    <code>@user1</code> yoki <code>user</code> (username)
                    <code>alixon</code> yoki <code>ali</code> (nickname)
                    <code>+99890</code> yoki <code>1234</code> (telefon)""";

            bot.sendMessage(botInfo.getId(), user.getChatId(), messageText, kyb.backBtn);
            eventCode(user, "searching users");
        } else if (text.equals(adminOnlineMagazineUsersPage[3])) {
            String requestMessage = """
                    <b>🔎 Foydalanuvchi qidirish (ID bo'yicha)</b>
                    
                    Iltimos, foydalanuvchi ID raqamini kiriting:
                    
                    <i>ℹ️ ID raqam odatda quyidagi formatda bo'ladi:</i>
                    • <code>12345</code> (5-10 xonali raqam)
                    • Foydalanuvchi ma'lumotlarida 🆔 belgisi bilan
                    
                    <b>📌 Misol uchun:</b> <code>54231</code>""";

            bot.sendMessage(botId, user.getChatId(), requestMessage, kyb.backBtn);

            eventCode(user, "find by id");
        } else if (text.equals(backButton)) {
            start(botInfo, user);
        } else wrongBtn(botId, user.getChatId(), kyb.usersPage());
    }

    private void errorResponseUserPage(BotUser user, Long botId) {
        bot.sendMessage(botId, user.getChatId(), """
                ⚠️ *Kutilmagan xatolik yuz berdi* ⚠️
                
                Iltimos, birozdan keyin qayta urinib ko'ring.
                Agar muammo takrorlansa, @me_mrx ga murojaat qiling.""", kyb.usersPage());
    }

    private void errorResponseUserPage(BotUser user, Long botId, ReplyKeyboardMarkup markup) {
        bot.sendMessage(botId, user.getChatId(), """
                ⚠️ *Kutilmagan xatolik yuz berdi* ⚠️
                
                Iltimos, birozdan keyin qayta urinib ko'ring.
                Agar muammo takrorlansa, @me_mrx ga murojaat qiling.""", markup);
    }


    private void wrongBtn(Long botId, Long chatId, ReplyKeyboardMarkup m) {
        bot.sendMessage(botId, chatId, "❌ Iltimos, tugmalardan foydalaning", m);
    }

    public void getAllUsers(Long botId, BotInfo botInfo, BotUser user, String data, CallbackQuery callbackQuery, Integer messageId) {
        getAllUsersAndBlockUsers(botId, botInfo, user, data, callbackQuery, messageId, null, bot.adminChatId);
    }

    private void getAllUsersAndBlockUsers(Long botId, BotInfo botInfo, BotUser user, String data, CallbackQuery callbackQuery, Integer messageId, String role, Long adminChatId) {
        Integer page = user.getPage();
        int size = bot.size;
        if (data.equals("next")) page++;
        else if (data.equals("prev")) {
            if (page == 0) {
                bot.alertMessage(botId, callbackQuery, "Siz eng birinchi sahifadasiz");
                return;
            }
            page--;
        } else {
            if (data.equals("back")) {
                Page<BotUser> list;
                if (role == null) {
                    list = botUserService.findAll(page, size, botId).getData();
                } else if (role.equals("block")) {
                    list = botUserService.findAllByRole(page, size, botId, "block").getData();
                } else if (role.equals("search")) {
                    list = botUserService.searchPhoneAndUsernameAndNickname(botId, page, size, user.getHelperValue()).getData();
                } else return;
                if (list.isEmpty() && (role == null || role.equals("block"))) {
                    String alertText;
                    if (role == null)
                        alertText = "ℹ️ Hozirda foydalanuvchilar mavjud emas. " + "Shu sababli siz foydalanuvchilar bo'limiga yo'naltirildingiz";
                    else
                        alertText = "ℹ️ Hozirda bloklangan foydalanuvchilar mavjud emas. " + "Shu sababli siz foydalanuvchilar bo'limiga yo'naltirildingiz";
                    bot.alertMessage(botId, callbackQuery, alertText);
                    bot.deleteMessage(botId, user.getChatId(), messageId);
                    menu(botId, user, adminOnlineMagazineMenu[0], botInfo, adminChatId);
                    return;
                }
                bot.editMessageText(botId, user.getChatId(), messageId, adminOnlineMagazineAboutUsers(list, botInfo.getBotUsername(), user.getPage(), bot.size), kyb.getUsers(list.getContent()));
            } else if (data.equals("block") || data.equals("unblock")) {
                blockOrUnblock(botId, user, data, callbackQuery, messageId);
            } else {
                Long userId = Long.valueOf(data);
                ResponseDto<BotUser> checkUser = botUserService.findByUserId(userId, botId);
                if (!checkUser.isSuccess()) {
                    bot.alertMessage(botId, callbackQuery, checkUser.getMessage());
                    return;
                }
                BotUser botUser = checkUser.getData();
                user.setBotUserId(botUser.getId());
                botUserService.save(user);
                bot.editMessageText(botId, user.getChatId(), messageId, adminOnlineMagazineAboutUser(botUser), kyb.updateUser(botUser.getRole().equals("block"), true));

            }
            return;
        }
        Page<BotUser> list;
        String s = null;
        if (role == null) {
            list = botUserService.findAll(page, size, botId).getData();
        } else if (role.equals("block")) {
            list = botUserService.findAllByRole(page, size, botId, "block").getData();
        } else if (role.equals("search")) {
            list = botUserService.searchPhoneAndUsernameAndNickname(botId, page, size, user.getHelperValue()).getData();
        } else return;
        if (list.getContent().isEmpty()) {
            bot.alertMessage(botId, callbackQuery, "Siz eng oxirgi sahifadasiz");
            return;
        }
        bot.editMessageText(botId, user.getChatId(), messageId, adminOnlineMagazineAboutUsers(list, botInfo.getBotUsername(), user.getPage(), bot.size), kyb.getUsers(list.getContent()));
        user.setPage(page);
        botUserService.save(user);
    }

    private void blockOrUnblock(Long botId, BotUser user, String action, CallbackQuery callbackQuery, int messageId) {
        // 1. Foydalanuvchini topish va yangilash
        BotUser targetUser = botUserService.findByUserId(user.getBotUserId(), botId).getData();

        if (action.equals("unblock")) {
            targetUser.setRole("user");
            bot.alertMessage(botId, callbackQuery, "✅ Foydalanuvchi muvaffaqiyatli blokdan olindi!");
        } else {
            if (targetUser.getId().equals(user.getId())) {
                String warningMessage = """
                        ⚠️ *Operatsiya rad etildi*
                        
                        Siz o'zingizni o'zingiz bloklay olmaysiz !
                        
                        Boshqa foydalanuvchini tanlang yoki 
                        @me_mrx bilan bog'laning.""";

                bot.alertMessage(botId, callbackQuery, warningMessage);
                return;
            }
            if (targetUser.getRole().equals("admin")) {
                String warningMessage = """
                        ⚠️ *Operatsiya rad etildi*
                        
                        Siz adminlarni bloklay olmaysiz !
                        
                        Boshqa foydalanuvchini tanlang yoki 
                        @me_mrx bilan bog'laning.""";

                bot.alertMessage(botId, callbackQuery, warningMessage);
                return;
            }
            targetUser.setRole("block");
            bot.alertMessage(botId, callbackQuery, "⚠️ Foydalanuvchi muvaffaqiyatli bloklandi!");
        }

        botUserService.save(targetUser);

        // 2. Admin panelini yangilash
        String updatedUserInfo = adminOnlineMagazineAboutUser(targetUser);
        InlineKeyboardMarkup userActions = kyb.updateUser(targetUser.getRole().equals("block"), true);
        bot.editMessageText(botId, user.getChatId(), messageId, updatedUserInfo, userActions);

        // 3. Foydalanuvchiga xabar yuborish
        String userNotification = targetUser.getRole().equals("user") ? "🎉 Tabriklaymiz! Siz blokdan olindingiz. Botdan foydalanish uchun /start tugmasini bosing." : "❌ Kechirasiz, siz admin tomonidan bloklandingiz. Botdan foydalana olmaysiz.";

        ReplyKeyboardMarkup keyboard = targetUser.getRole().equals("user") ? kyb.setKeyboards(new String[]{"/start"}, 1) : null;

        if (targetUser.getRole().equals("block")) {
            bot.sendMessage(botId, targetUser.getChatId(), userNotification, true);
        } else {
            bot.sendMessage(botId, targetUser.getChatId(), userNotification, keyboard);
        }
    }

    public void handleBackToUsersPage(Long id, BotInfo botInfo, BotUser user, String text, String eventCode) {
        if (text.equals(backButton)) {
            menu(id, user, adminOnlineMagazineMenu[0], botInfo, bot.adminChatId);
        } else {
            wrongBtn(id, user.getChatId(), null);
            usersPage(id, botInfo, user, adminOnlineMagazineUsersPage[adminOnlineMagazineUsersPageNumber(eventCode)]);
        }
    }

    private int adminOnlineMagazineUsersPageNumber(String eventCode) {
        if (eventCode.equals("get all users")) return 0;
        else if (eventCode.equals("get all block users")) return 1;
        else return -1;
    }

    public void getAllBlockUsers(Long botId, BotInfo botInfo, BotUser user, String data, CallbackQuery callbackQuery, Integer messageId) {
        getAllUsersAndBlockUsers(botId, botInfo, user, data, callbackQuery, messageId, "block", bot.adminChatId);
    }

    public void searchingUsers(BotInfo botInfo, BotUser user, String text) {
        if (text.equals(backButton)) {
            handleBackToUsersPage(botInfo.getId(), botInfo, user, text, "get all block users");
        } else {
            ResponseDto<Page<BotUser>> checkSearch = botUserService.searchPhoneAndUsernameAndNickname(botInfo.getId(), user.getPage(), bot.size, text);
            if (checkSearch.isSuccess()) {
                text = text.trim();
                if (text.equals("null")) text = null;
                List<BotUser> foundUsers = checkSearch.getData().getContent();

                // Foydalanuvchi qidiruv so'rovini saqlash
                user.setHelperValue(text);
                botUserService.save(user);

                if (foundUsers.isEmpty()) {
                    // Agar foydalanuvchilar topilmasa
                    String notFoundMessage = """
                            🔍 *Qidiruv natijalari*
                            
                            "%s" bo'yicha hech qanday foydalanuvchi topilmadi.
                            
                            Iltimos, quyidagilarni tekshiring:
                            • Qidiruv so'rovi to'g'ri yozilganligi
                            • Boshqa kalit so'zlar bilan sinab ko'ring""".formatted(text);

                    bot.sendMessage(botInfo.getId(), user.getChatId(), notFoundMessage, kyb.backBtn);
                } else {
                    // Agar foydalanuvchilar topilsa
                    String resultsHeader = """
                            🔍 *Qidiruv natijalari*
                            
                            So'rov: "%s"
                            Topildi: %d ta foydalanuvchi""".formatted(text, foundUsers.size());

                    // Natijalarni yuborish
                    bot.sendMessage(botInfo.getId(), user.getChatId(), resultsHeader, kyb.backBtn

                    );

                    // Foydalanuvchilar ro'yxatini yuborish
                    String usersList = adminOnlineMagazineAboutUsers(checkSearch.getData(), botInfo.getBotUsername(), user.getPage(), bot.size);

                    bot.sendMessage(botInfo.getId(), user.getChatId(), usersList, kyb.getUsers(foundUsers));
                }
            } else {
                bot.sendMessage(botInfo.getId(), user.getChatId(), "Bunday nomli foydalanuvchilar topilmadi, boshqatdan harakat qilib ko'ring", kyb.backBtn);
            }
        }
    }

    public void searchingUsers(Long botId, BotInfo botInfo, BotUser user, String data, CallbackQuery callbackQuery, Integer messageId) {
        getAllUsersAndBlockUsers(botId, botInfo, user, data, callbackQuery, messageId, "search", bot.adminChatId);
    }

    public void findById(BotInfo botInfo, Long botId, BotUser user, String text) {
        if (text.equals(backButton)) {
            handleBackToUsersPage(botInfo.getId(), botInfo, user, text, "get all users");
            return;
        }

        try {
            Long userId = Long.valueOf(text);
            ResponseDto<BotUser> userResponse = botUserService.findByUserId(userId, botId);

            if (!userResponse.isSuccess()) {
                String notFoundMessage = """
                        ❌ Foydalanuvchi topilmadi
                        
                        ID: `%d` bo'yicha hech qanday foydalanuvchi topilmadi.
                        Iltimos, quyidagilarni tekshiring:
                        - ID raqam to'g'ri kiritilganligi
                        - Boshqa ID bilan qayta urinib ko'ring""".formatted(userId);

                bot.sendMessage(botId, user.getChatId(), notFoundMessage, kyb.backBtn);
                return;
            }

            // Success case - user found
            BotUser foundUser = userResponse.getData();
            String userInfo = adminOnlineMagazineAboutUser(foundUser);
            boolean isBlock = "block".equals(foundUser.getRole());
            bot.sendMessage(botId, user.getChatId(), """
                    ℹ️ *Siz kiritgan ID bo'yicha qidiruv natijalari*
                    
                    Kiritilgan ID: `%s`
                    
                    %s
                    
                    Qo'shimcha amallar uchun quyidagi tugmalardan foydalaning:""".formatted(text, userResponse.isSuccess() ? "✅ Foydalanuvchi muvaffaqiyatli topildi" : "❌ Foydalanuvchi topilmadi"), kyb.backBtn);
            bot.sendMessage(botId, user.getChatId(), userInfo, kyb.updateUser(isBlock, false));
            user.setBotUserId(userId);
            botUserService.save(user);
        } catch (NumberFormatException e) {
            String errorMessage = """
                    ⚠️ Noto'g'ri format
                    
                    Iltimos, faqat raqamlardan foydalaning.
                    Foydalanuvchi ID odatda quyidagicha bo'ladi:
                    `12345` yoki `67890`
                    
                    Qayta urinib ko'ring yoki orqaga qayting""";

            bot.sendMessage(botId, user.getChatId(), errorMessage, kyb.backBtn);
            log.error("Invalid user ID format entered: {}", text, e);
        } catch (Exception e) {
            String errorMessage = """
                    ‼️ Xatolik yuz berdi
                    
                    Foydalanuvchi ma'lumotlarini olishda texnik xatolik.
                    Iltimos, keyinroq qayta urinib ko'ring.
                    """;

            bot.sendMessage(botId, user.getChatId(), errorMessage, kyb.backBtn);
            log.error("Error finding user by ID: {}", text, e);
        }
    }

    public void findById(BotInfo botInfo, Long id, BotUser user, String data, CallbackQuery callbackQuery, Integer messageId) {
        blockOrUnblock(botInfo.getId(), user, data, callbackQuery, messageId);
    }


    public void categoryMenu(BotInfo botInfo, BotUser user, String text) {
        Long botId = botInfo.getId();
        if (text.equals(backButton)) start(botInfo, user);
        else if (text.equals(addCategory)) {
            bot.sendMessage(botId, user.getChatId(), """
                    <b>📥 Kategoriya nomini kiriting</b>
                    Iltimos, faqat o'zbek tilida yozing
                    """, kyb.backBtn);
            eventCode(user, "add new category name uz");
        } else {
            ResponseDto<Category> checkCategory = categoryService.findByNameUz(botId, text);
            if (checkCategory.isSuccess()) {
                Category category = checkCategory.getData();
                int size = productService.findAllByCategoryId(category.getId()).getData().size();
                bot.sendMessage(botId, user.getChatId(), text, kyb.backBtn);
                bot.sendMessage(botId, user.getChatId(), aboutCategory(true, size, category), kyb.crudCategory());
                user.setCategoryId(category.getId());
                user.setEventCode("crud category");
                botUserService.save(user);
            } else {
                List<Category> list = categoryService.findAllByBotId(botId).getData();
                wrongBtn(botId, user.getChatId(), kyb.setCategories(list, list.isEmpty()));
            }
        }
    }

    public void addNewCategoryNameUz(BotInfo botInfo, BotUser user, String text) {
        Long botId = botInfo.getId();
        if (text.equals(backButton)) menu(botId, user, adminOnlineMagazineMenu[2], botInfo, bot.adminChatId);
        else {
            ResponseDto<Category> checkCategory = categoryService.draftCategory(botId);
            Category category;
            if (checkCategory.isSuccess()) {
                category = checkCategory.getData();
            } else {
                category = new Category();
                category.setStatus("draft");
                category.setActive(false);
                category.setBotId(botId);
            }
            category.setNameUz(text);
            ResponseDto<Void> save = categoryService.save(botId, category, "uz");
            if (save.isSuccess()) {
                bot.sendMessage(botId, user.getChatId(), "<b>✅ O'zbekcha nom muvaffaqiyatli saqlandi!</b>\n" + "Endi kategoriyaning <i>ruscha</i> nomini kiriting:\n\n" + "Masalan: <code>Лаваш</code> yoki <code>Ход-дог</code>", true);
                eventCode(user, "add new category name ru");
            } else {
                bot.sendMessage(botId, user.getChatId(), """
                        ⚠️ <b>Xatolik!</b> Bu nom allaqachon mavjud.
                        
                        Iltimos, quyidagi variantlardan foydalaning:
                        • <code>Elektronika_2024</code>
                        • <code>Yangi_Kitoblar</code>
                        • <code>Maxsus_Kategoriya</code>""", kyb.backBtn);
            }
        }
    }

    public void addNewCategoryNameRu(BotInfo botInfo, BotUser user, String text) {
        Category category = categoryService.draftCategory(botInfo.getId()).getData();
        category.setStatus("open");
        category.setActive(true);
        category.setNameRu(text);
        ResponseDto<Void> save = categoryService.save(botInfo.getId(), category, "ru");
        if (save.isSuccess()) {
            bot.sendMessage(botInfo.getId(), user.getChatId(), "Muvaffaqiyatli qo'shildi");
            menu(botInfo.getId(), user, adminOnlineMagazineMenu[2], botInfo, bot.adminChatId);
        } else {
            bot.sendMessage(botInfo.getId(), user.getChatId(), """
                    ⚠️ <b>Xatolik!</b> Bu nom allaqachon mavjud.
                    
                    Iltimos, quyidagi variantlardan foydalaning:
                    • <code>Elektronika_2024</code>
                    • <code>Yangi_Kitoblar</code>
                    • <code>Maxsus_Kategoriya</code>
                    """);
        }
    }

    public void crudCategory(BotInfo botInfo, BotUser user, String text, int messageId) {
        if (text.equals(backButton)) {
            menu(botInfo.getId(), user, adminOnlineMagazineMenu[2], botInfo, bot.adminChatId);
            return;
        }
        bot.deleteMessage(botInfo.getId(), user.getChatId(), messageId);
    }

    public void crudCategory(BotInfo botInfo, BotUser user, String data, Integer messageId, CallbackQuery callbackQuery) {
        Category category = categoryService.findById(user.getCategoryId()).getData();
        List<Product> products = productService.findAllByCategoryId(category.getId()).getData();
        if (data.equals("edit_uz_name") || data.equals("edit_ru_name")) {
            String response = aboutCategory(true, products.size(), category);
            String editMessage = editNameBtn(data);
            response = response.concat(editMessage);
            bot.editMessageText(botInfo.getId(), user.getChatId(), messageId, response);
            eventCode(user, data);
        } else if (data.equals("delete_category")) {
            bot.editMessageText(botInfo.getId(), user.getChatId(), messageId, aboutCategory(true, products.size(), category) + "\n\nHaqiqatdan ham ushbu kategoriyani o'chirmoqchimisiz", kyb.isDeleteBtn());
        } else if (data.equals("delete_confirm")) {
            category.setActive(false);
            category.setNameUz(UUID.randomUUID().toString());
            category.setNameRu(UUID.randomUUID().toString());
            category.setStatus("open");
            ResponseDto<Void> save = categoryService.save(category);
            if (!save.isSuccess()) {
                bot.alertMessage(botInfo.getId(), callbackQuery, "O'chirishda xatolik yuz berdi, qaytadan urinib ko'ring");
                return;
            }
            bot.alertMessage(botInfo.getId(), callbackQuery, "✅ Muvaffaqiyatli o‘chirildi!");
            bot.deleteMessage(botInfo.getId(), user.getChatId(), messageId);
            menu(botInfo.getId(), user, adminOnlineMagazineMenu[2], botInfo, bot.adminChatId);
        } else if (data.equals("view_products")) {
            if (products.isEmpty()) {
                bot.alertMessage(botInfo.getId(), callbackQuery, "⚠️ \"%s\" kategoriyasida mahsulotlar mavjud emas.".formatted(category.getNameUz()));
            }
            bot.deleteMessage(botInfo.getId(), user.getChatId(), messageId);
            if (products.isEmpty()) bot.sendMessage(botInfo.getId(), user.getChatId(), ("""
                    ⚠️ "%s" kategoriyasida hozircha mahsulotlar mavjud emas.
                    
                    ➕ Yangi mahsulot qo‘shish uchun pastdagi tugmani bosing.""").formatted(category.getNameUz()), kyb.setProducts(products, "uz"));
            else
                bot.sendMessage(botInfo.getId(), user.getChatId(), "🛍 \"%s\" kategoriyasidagi mavjud mahsulotlar:".formatted(category.getNameUz()), kyb.setProducts(products, "uz"));

            eventCode(user, "product menu");
        } else {
            if (data.equals("delete_cancel")) {
                bot.alertMessage(botInfo.getId(), callbackQuery, "\uD83D\uDEAB Operatsiya foydalanuvchi tomonidan bekor qilindi");
            } else {
                if (!data.equals("back_action")) {
                    return;
                }
            }
            bot.editMessageText(botInfo.getId(), user.getChatId(), messageId, aboutCategory(true, products.size(), category), kyb.crudCategory());
        }
    }

    private String editNameBtn(String data) {
        String editMessage;
        if ("edit_uz_name".equals(data)) {
            editMessage = """
                    
                    
                    ℹ️ Ushbu kategoriyaning yangi o'zbekcha nomini kiriting:
                    Masalan: "Elektronika" yoki "Kitoblar"
                    
                    """;
        } else {
            editMessage = """
                    
                    
                    ℹ️ Ushbu kategoriyaning yangi ruscha nomini kiriting:
                    Masalan: "Электроника" yoki "Книги\"""";
        }
        return editMessage;
    }

    public void editName(BotInfo botInfo, BotUser user, String text) {
        Category category = categoryService.findById(user.getCategoryId()).getData();
        if (text.equals(backButton)) {
            categoryMenu(botInfo, user, category.getNameUz());
            return;
        }
        String responseMessage;
        if (user.getEventCode().equals("edit_uz_name")) {
            if (text.equalsIgnoreCase(category.getNameUz())) {
                responseMessage = "⚠️ O'zbekcha nom o'zgartirilmadi\n" + "Sababi: Yangi nom avvalgisi bilan bir xil";
            } else {
                category.setNameUz(text.trim());
                categoryService.save(category);
                responseMessage = "✅ O'zbekcha nom muvaffaqiyatli yangilandi!\n" + "Yangi nom: " + text;
            }
        } else if (user.getEventCode().equals("edit_ru_name")) {
            if (text.equalsIgnoreCase(category.getNameRu())) {
                responseMessage = "⚠️ Ruscha nom o'zgartirilmadi\n" + "Sababi: Yangi nom avvalgisi bilan bir xil";
            } else {
                category.setNameRu(text.trim());
                categoryService.save(category);
                responseMessage = "✅ Ruscha nom muvaffaqiyatli yangilandi!\n" + "Новое название: " + text;
            }
        } else {
            return; // Noto'g'ri event code
        }

        bot.sendMessage(botInfo.getId(), user.getChatId(), responseMessage);
        categoryMenu(botInfo, user, category.getNameUz());
    }

    public void productMenu(BotInfo botInfo, BotUser user, String text) {
        Category category = categoryService.findById(user.getCategoryId()).getData();
        if (text.equals(backButton)) {
            categoryMenu(botInfo, user, category.getNameUz());
        } else if (text.equals(addProduct)) {
            bot.sendMessage(botInfo.getId(), user.getChatId(), ("🆕 Yangi mahsulot qo‘shish\n" + "\n" + "📂 \"%s\" kategoriyasiga mahsulot qo‘shmoqchisiz.\n" + "✍ Iltimos, mahsulotning o‘zbekcha nomini kiriting:").formatted(category.getNameUz()), kyb.backBtn);
            eventCode(user, "get new product name uz");
        } else {
            ResponseDto<Product> checkProduct = productService.findByNameUz(text, user.getCategoryId());
            if (checkProduct.isSuccess()) {
                Product product = checkProduct.getData();
                List<ProductVariant> list = productVariantService.findAllByProductId(product.getId()).getData();
                user.setProductId(product.getId());
                bot.sendMessage(botInfo.getId(), user.getChatId(), text, kyb.backBtn);
                bot.sendPhoto(botInfo.getId(), user.getChatId(), list.get(0).getImg(), kyb.getProductVariantsAndEditProductBtn(list, list.get(0).getId()), false, aboutCategoryWithPhoto(true, product, list.get(0).getPrice(), list.get(0), "uz"));
                user.setProductVariantId(list.get(0).getId());
                user.setEventCode("crud product");
                botUserService.save(user);
            } else {
                wrongBtn(botInfo.getId(), user.getChatId(), kyb.setProducts(productService.findAllByCategoryId(category.getId()).getData(), "uz"));
            }
        }
    }

    public void addProduct(BotInfo botInfo, BotUser user, String text) {
        Category category = categoryService.findById(user.getCategoryId()).getData();
        List<Product> products = productService.findAllByCategoryId(category.getId()).getData();
        if (text != null) {
            if (text.equals(backButton)) {
                bot.sendMessage(botInfo.getId(), user.getChatId(), "🛍 \"%s\" kategoriyasidagi mavjud mahsulotlar:".formatted(category.getNameUz()), kyb.setProducts(products, "uz"));
                eventCode(user, "product menu");
                return;
            }
        }
        ResponseDto<Product> checkProduct = productService.draftProduct(category.getId());
        Product product;
        if (checkProduct.isSuccess()) {
            product = checkProduct.getData();
        } else {
            product = new Product();
            product.setActive(false);
            product.setStatus("draft");
            product.setCategoryId(category.getId());
            productService.save(product);
        }
        ResponseDto<ProductVariant> checkProductVariant = productVariantService.draftProductVariant(product.getId());
        ProductVariant productVariant;
        if (checkProduct.isSuccess() && checkProductVariant.getData() != null) {
            productVariant = checkProductVariant.getData();
        } else {
            productVariant = new ProductVariant();
            productVariant.setActive(false);
            productVariant.setStatus("draft");
            productVariant.setProductId(product.getId());
            productVariantService.save(productVariant);
        }

        String eventCode = null;
        String s;
        boolean isMarkup = false;//false bo'lsa reply makup aks hol inline reply makup
        ReplyKeyboardMarkup replyKeyboardMarkup = null;
        InlineKeyboardMarkup inlineKeyboardMarkup = null;
        if (user.getEventCode().equals("get new product name uz")) {
            product.setNameUz(text.trim());
            ResponseDto<Void> save = productService.add(category.getId(), product, "uz");
            if (save.isSuccess()) {
                s = "\uD83D\uDCDD Muvaffaqiyatli qo'shildi, endi ruscha nomini kiriting";
                eventCode = "get new product name ru";
            } else {
                s = "❗ Bu nom band, iltimos boshqa nom kiriting";

            }

        } else if (user.getEventCode().equals("get new product name ru")) {
            assert text != null;
            product.setNameRu(text.trim());
            ResponseDto<Void> save = productService.add(category.getId(), product, "ru");
            if (save.isSuccess()) {
                s = "📝 Muvaffaqiyatli qo'shildi, endi bu mahsulotning o'zbekcha tavsifini kiriting";
                eventCode = "get new product desc uz";
            } else {
                s = "❗Bu nom band iltimos boshqa nom kiriting (rus tilida)0";
            }
        } else if (user.getEventCode().equals("get new product desc uz")) {
            assert text != null;
            product.setDescriptionUz(text.trim());
            ResponseDto<Void> save = productService.save(product);
            if (save.isSuccess()) {
                s = "📝 Muvaffaqiyatli qo'shildi, endi bu mahsulotning ruscha tavsifini kiriting";
                eventCode = "get new product desc ru";
            } else {
                s = "❗Bu nom band iltimos boshqa nom kiriting (o'zbek tilida)";
            }
        } else if (user.getEventCode().equals("get new product desc ru")) {
            product.setDescriptionRu(text.trim());
            ResponseDto<Void> save = productService.save(product);
            if (save.isSuccess()) {
                s = "📝 Endi bu mahsulot turini kiriting (o'zbek tilida), agar bu nimaligini tushunmasangiz, yordam uchun %s ga murojat qiling.".formatted(adminTelegramProfile);
                eventCode = "get new product variant name uz";
                //qivoman
            } else {
                s = "❗ Bu nom band, iltimos boshqa nom kiriting (rus tilida)";
            }
        } else if (user.getEventCode().equals("get new product variant name uz")) {
            productVariant.setNameUz(text.trim());
            ResponseDto<Void> save = productVariantService.save(productVariant);
            if (save.isSuccess()) {
                s = "📝 Endi bu mahsulot turini kiriting (rus tilida), agar bu nimaligini tushunmasangiz, yordam uchun %s ga murojat qiling.".formatted(adminTelegramProfile);
                eventCode = "get new product variant name ru";
            } else s = save.getMessage();
        } else if (user.getEventCode().equals("get new product variant name ru")) {
            productVariant.setNameRu(text.trim());
            productVariantService.save(productVariant);
            s = "💸 Bu mahsulot turining narxini kiriting";
            eventCode = "get new product variant price";
        } else if (user.getEventCode().equals("get new product variant price")) {
            try {
                productVariant.setPrice(Double.parseDouble(text.trim()));
                productVariantService.save(productVariant);
                s = "🖼️ Bu mahsulot turining rasmini kiriting";
                eventCode = "get new product variant img";
            } catch (NumberFormatException e) {
                s = "❌ Narxni kiritishda faqat raqamlardan foydalanishingiz kerak";
            }
        } else if (user.getEventCode().equals("is add product to category")) {
            assert text != null;
            if (text.equals("✅ Ha")) {
                s = "📝 Mahsulot turini kiriting (o'zbek tilida), agar bu nimaligini tushunmasangiz, yordam uchun %s ga murojat qiling.".formatted(adminTelegramProfile);
                eventCode = "get new product variant name uz";
                eventCode(user, eventCode);
                bot.sendMessage(botInfo.getId(), user.getChatId(), s, true);
                return;
            } else if (text.equals("❌ Yo'q")) {
                List<ProductVariant> list = productVariantService.findAllByProductId(product.getId()).getData();
                s = aboutCategoryWithPhoto(true, product, list.get(0).getPrice(), list.get(0), "uz");
                bot.sendPhoto(botInfo.getId(), user.getChatId(), list.get(0).getImg(), kyb.isAddProduct(list, list.get(0).getId()), true, s);
                isMarkup = true;
                eventCode = "is finished add product to category";
                eventCode(user, eventCode);
                return;
            } else {
                wrongBtn(botInfo.getId(), user.getChatId(), kyb.isSuccess("uz"));
                return;
            }
        } else if (user.getEventCode().equals("is finished add product to category")) {
            assert text != null;
            if (text.equals("✅ Ha")) {
                product.setActive(true);
                product.setStatus("open");
                productService.save(product);
                bot.sendMessage(botInfo.getId(), user.getChatId(), "✅ Muvaffaqiyatli saqlandi");

                s = "🛍 \"%s\" kategoriyasidagi mavjud mahsulotlar:".formatted(category.getNameUz());
                replyKeyboardMarkup = kyb.setProducts(products, "uz");
                eventCode(user, "product menu");
            } else if (text.equals("❌ Yo'q")) {
                product.setActive(true);
                product.setStatus("open");
                productService.save(product);
                bot.sendMessage(botInfo.getId(), user.getChatId(), "🚫 Operatsiya bekor qilindi");

                s = "🛍 \"%s\" kategoriyasidagi mavjud mahsulotlar:".formatted(category.getNameUz());
                replyKeyboardMarkup = kyb.setProducts(products, "uz");
                eventCode(user, "product menu");
            } else {
                return;
            }
        } else if (user.getEventCode().equals("get new product variant img")) {
            if (text == null) s = "❌ Matn emas, rasm yuborishingiz kerak";
            else {
                productVariant.setImg(text);
                productVariant.setStatus("open");
                productVariant.setActive(true);
                ;
                productVariant.setProductId(product.getId());
                productVariantService.save(productVariant);
                s = "✅ Muvaffaqiyatli saqlandi, yana ushbu mahsulot uchun tur kiritmoqchimisiz ?";

                replyKeyboardMarkup = kyb.isSuccess("uz");
                eventCode = "is add product to category";
            }
        } else return;
        if (replyKeyboardMarkup == null) {
            bot.sendMessage(botInfo.getId(), user.getChatId(), s);
        } else {
            bot.sendMessage(botInfo.getId(), user.getChatId(), s, replyKeyboardMarkup);
        }
        if (eventCode != null) {
            eventCode(user, eventCode);
        }
    }

    public void isFinishedAddProductToCategory(BotInfo botInfo, BotUser user, String data, CallbackQuery callbackQuery, Integer messageId) {
        Product product = productService.draftProduct(user.getCategoryId()).getData();
        List<ProductVariant> list = productVariantService.findAllByProductId(product.getId()).getData();
        ProductVariant productVariant = null;
        for (ProductVariant variant : list) {
            if (variant.getId().equals(Long.valueOf(data))) {
                productVariant = variant;
                break;
            }
        }
        assert productVariant != null;
        String s = aboutCategoryWithPhoto(true, product, productVariant.getPrice(), productVariant, "uz");
//        bot.sendPhoto(botInfo.getId(), user.getChatId(), list.get(0).getImg(), kyb.isAddProduct(list, list.get(0).getId()), true);
        bot.editMessageMedia(botInfo.getId(), user.getChatId(), messageId, kyb.isAddProduct(list, productVariant.getId()), s, productVariant.getImg());
    }

    public void isFinishedAddProductToCategory(BotInfo botInfo, BotUser user, String text) {
        List<Product> products = productService.findAllByCategoryId(user.getCategoryId()).getData();
        Product product = productService.draftProduct(user.getCategoryId()).getData();
        if (text.equals("✅ Ha")) {
            product.setActive(true);
            product.setStatus("open");
            productService.save(product);
            products = productService.findAllByCategoryId(user.getCategoryId()).getData();
            bot.sendMessage(botInfo.getId(), user.getChatId(), "✅ Muvaffaqiyatli saqlandi");

            bot.sendMessage(botInfo.getId(), user.getChatId(), "🛍 \"%s\" kategoriyasidagi mavjud mahsulotlar:".formatted(categoryService.findById(user.getCategoryId()).getData().getNameUz()), kyb.setProducts(products, "uz"));
            eventCode(user, "product menu");
        } else if (text.equals("❌ Yo'q")) {
            ProductVariant variant = productVariantService.draftProductVariant(product.getId()).getData();
            product.setNameUz(UUID.randomUUID() + "uz" + product.getId());
            for (ProductVariant data : productVariantService.findAllByProductId(product.getId()).getData()) {
                data.setStatus("open");
                data.setActive(false);
                productVariantService.save(data);
            }
//            productVariant.setStatus("open");
            product.setNameRu(UUID.randomUUID() + "ru" + product.getId());
            productService.save(product);
            variant.setActive(false);
            productVariantService.save(variant);
            bot.sendMessage(botInfo.getId(), user.getChatId(), "🚫 Operatsiya bekor qilindi");
            bot.sendMessage(botInfo.getId(), user.getChatId(), "🛍 \"%s\" kategoriyasidagi mavjud mahsulotlar:".formatted(categoryService.findById(user.getCategoryId()).getData().getNameUz()), kyb.setProducts(products, "uz"));
            eventCode(user, "product menu");
        } else return;
    }

    public void crudProduct(BotInfo botInfo, BotUser user, String text) {
        List<Product> products = productService.findAllByCategoryId(user.getCategoryId()).getData();
        if (text.equals(backButton)) {
            bot.sendMessage(botInfo.getId(), user.getChatId(), "🛍 \"%s\" kategoriyasidagi mavjud mahsulotlar:".formatted(categoryService.findById(user.getCategoryId()).getData().getNameUz()), kyb.setProducts(products, "uz"));
            eventCode(user, "product menu");
            return;
        }
        wrongBtn(botInfo.getId(), user.getChatId(), kyb.backBtn);
        Product product = productService.findById(user.getProductId()).getData();
        List<ProductVariant> list = productVariantService.findAllByProductId(product.getId()).getData();
        user.setProductId(product.getId());
        bot.sendPhoto(botInfo.getId(), user.getChatId(), list.get(0).getImg(), kyb.getProductVariantsAndEditProductBtn(list, list.get(0).getId()), false, aboutCategoryWithPhoto(true, product, list.get(0).getPrice(), list.get(0), "uz"));
        user.setProductVariantId(list.get(0).getId());
        botUserService.save(user);
    }

    public void crudProduct(BotInfo botInfo, BotUser user, String data, CallbackQuery callbackQuery, Integer messageId, boolean isInFunction) {
        List<ProductVariant> list = productVariantService.findAllByProductId(user.getProductId()).getData();
        Product product = productService.findById(user.getProductId()).getData();
        switch (data) {
            case "edit" -> {
                ProductVariant productVariant = productVariantService.findById(user.getProductVariantId()).getData();
                String res = aboutCategoryWithPhoto(true, product, productVariant.getPrice(), productVariant, "uz");
                bot.editCaption(botInfo.getId(), user.getChatId(), messageId, res, kyb.editProductBtn());
            }
            case "add type" -> {
                bot.deleteMessage(botInfo.getId(), user.getChatId(), messageId);
                bot.sendMessage(botInfo.getId(), user.getChatId(), "<b>%s</b> mahsulotga qo'shish uchun tur nomini kiriting(O'zbek tilida)".formatted(product.getNameUz()), kyb.backBtn);
                eventCode(user, "add product variant to product get name uz");
            }
            case "delete" -> {
                InlineKeyboardMarkup deleteBtn = kyb.isDeleteBtn();
                ProductVariant productVariant = productVariantService.findById(user.getProductVariantId()).getData();
                String res = aboutCategoryWithPhoto(true, product, productVariant.getPrice(), productVariant, "uz");
                res = res.concat("""
                        
                        Ushbu mahsulotni haqiqatdan ham o'chirmoqchimisiz ?""");
                bot.editCaption(botInfo.getId(), user.getChatId(), messageId, res, deleteBtn);
            }
            case "delete_cancel", "back_action", "back" -> {
                if (data.equals("delete_cancel")) {
                    bot.alertMessage(botInfo.getId(), callbackQuery, "\uD83D\uDEAB Operatsiya foydalanuvchi tomonidan bekor qilindi");
                }
                crudProduct(botInfo, user, String.valueOf(user.getProductVariantId()), callbackQuery, messageId, true);
            }

            case "delete_confirm" -> {
                product.setNameRu(UUID.randomUUID() + "ru" + product.getId());
                product.setNameUz(UUID.randomUUID() + "uz" + product.getId());
                product.setActive(false);
                ResponseDto<Void> save = productService.save(product);
                if (!save.isSuccess()) {
                    bot.alertMessage(botInfo.getId(), callbackQuery, "Kutilmagan xatolik, iltimos qaytadan urinib ko'ring");
                    return;
                }
                crudCategory(botInfo, user, "view_products", messageId, callbackQuery);
            }
            case "edit name uz", "edit name ru", "edit desc uz", "edit desc ru", "edit price" -> {
                ProductVariant productVariant = productVariantService.findById(user.getProductVariantId()).getData();
                String price = formatPrice(productVariant.getPrice());
                bot.deleteMessage(botInfo.getId(), user.getChatId(), messageId);
                String s = switch (data) {
                    case "edit name uz" ->
                            "Avvalgi nomi (O'zbek tilida) <code>%s</code>\n\nYangi nomini kiriting".formatted(product.getNameUz());
                    case "edit name ru" ->
                            "Avvalgi nomi (Rus tilida) <code>%s</code>\n\nYangi nomini kiriting".formatted(product.getNameRu());
                    case "edit desc uz" ->
                            "Avvalgi tavsif (O'zbek tilida) <code>%s</code>\n\nYangi nomini kiriting".formatted(product.getDescriptionUz());
                    case "edit desc ru" ->
                            "Avvalgi tavsif (Rus tilida) <code>%s</code>\n\nYangi nomini kiriting".formatted(product.getDescriptionRu());
                    default -> "Avvalgi narx: <code>%s</code>\n\nYangi nomini kiriting".formatted(price);
                };
                bot.sendMessage(botInfo.getId(), user.getChatId(), s, kyb.backBtn);
                eventCode(user, data);
            }
            case "back1" -> {
                List<ProductVariant> variants = productVariantService.findAllByProductId(user.getProductId()).getData();
                ProductVariant productVariant = productVariantService.findById(user.getProductVariantId()).getData();
                InlineKeyboardMarkup m = kyb.getProductVariantsAndEditProductBtn(variants, user.getProductVariantId());
                String res = aboutCategoryWithPhoto(true, product, productVariant.getPrice(), productVariant, "uz");
                bot.editCaption(botInfo.getId(), user.getChatId(), messageId, res, m);
            }
            case "delete product variant" -> {
                List<ProductVariant> variants = productVariantService.findAllByProductId(user.getProductId()).getData();
                if (variants.size() == 1) {
                    bot.alertMessage(botInfo.getId(), callbackQuery, """
                            ⚠️ Ushbu mahsulotni o‘chirish mumkin emas, chunki faqat bitta turi mavjud.
                            
                            Agar ushbu turini o‘chirmoqchi bo‘lsangiz, avval yangi tur qo‘shing, so‘ng bu turini o‘chirishingiz mumkin.""");
                    return;
                }
                ProductVariant productVariant = productVariantService.findById(user.getProductVariantId()).getData();
                productVariant.setActive(false);
                ResponseDto<Void> save = productVariantService.save(productVariant);
                if (!save.isSuccess()) {
                    bot.alertMessage(botInfo.getId(), callbackQuery, "⚠️ O‘chirish muvaffaqiyatsiz yakunlandi. Iltimos, qayta urinib ko‘ring!");
                    return;
                }
                bot.alertMessage(botInfo.getId(), callbackQuery, "✅ Siz muvaffaqiyatli o‘chirildi!");
                String res = aboutCategoryWithPhoto(true, product, productVariant.getPrice(), productVariant, "uz");
                variants = productVariantService.findAllByProductId(user.getProductId()).getData();
                productVariant = variants.get(0);
                user.setProductVariantId(productVariant.getId());
                botUserService.save(user);
                InlineKeyboardMarkup m = kyb.getProductVariantsAndEditProductBtn(variants, user.getProductVariantId());
                bot.editMessageMedia(botInfo.getId(), user.getChatId(), messageId, m, res, productVariant.getImg());

            }
            default -> {
                Long productTypeId = Long.valueOf(data);
                ProductVariant productVariant = productVariantService.findById(productTypeId).getData();
               /* if (productTypeId.equals(user.getProductVariantId()) && !isInFunction) {
                    bot.alertMessage(botInfo.getId(), callbackQuery, "⚠️ Bitta tugmani ikki marta bosdingiz!");
                    return;
                }*/
                user.setProductVariantId(productTypeId);
                InlineKeyboardMarkup markup = kyb.productVariantIsDelete();
                String res = aboutCategoryWithPhoto(true, product, productVariant.getPrice(), productVariant, "uz");
                bot.editMessageMedia(botInfo.getId(), user.getChatId(), messageId, markup, res, productVariant.getImg());
                botUserService.save(user);
            }
        }
    }

    public void editProductAndProductVariant(BotInfo botInfo, Long botId, BotUser user, String text) {
        Product product = productService.findById(user.getProductId()).getData();
        ProductVariant productVariant = productVariantService.findById(user.getProductVariantId()).getData();
        List<ProductVariant> variants = productVariantService.findAllByProductId(user.getProductId()).getData();
        String eventCode = user.getEventCode();
        if (text.equals(backButton)) {
            bot.sendPhoto(botId, user.getChatId(), productVariant.getImg(), kyb.editProductBtn(), false, aboutCategoryWithPhoto(true, product, productVariant.getPrice(), productVariant, "uz"));
            eventCode(user, "crud product");
            return;
        }
        if (eventCode.equals("edit name uz") && (text.equals(product.getNameUz()))) {
            bot.sendPhoto(botId, user.getChatId(), productVariant.getImg(), kyb.editProductBtn(), false, "🚫 Operatsiya bekor qilindi, sababi avvalgi nom bilan bir xil nom kiritdingiz \n\n" + aboutCategoryWithPhoto(true, product, productVariant.getPrice(), productVariant, "uz"));
            eventCode(user, "crud product");
            return;
        }
        if (eventCode.equals("edit name ru") && (text.equals(product.getNameRu()))) {
            bot.sendPhoto(botId, user.getChatId(), productVariant.getImg(), kyb.editProductBtn(), false, "🚫 Operatsiya bekor qilindi, sababi avvalgi nom bilan bir xil nom kiritdingiz \n\n" + aboutCategoryWithPhoto(true, product, productVariant.getPrice(), productVariant, "uz"));
            eventCode(user, "crud product");
            return;
        }
        switch (eventCode) {
            case "edit name uz" -> product.setNameUz(text);
            case "edit name ru" -> product.setNameRu(text);
            case "edit desc uz" -> product.setDescriptionUz(text);
            case "edit desc ru" -> product.setDescriptionRu(text);
            case "edit price" -> {
                try {
                    productVariant.setPrice(Double.parseDouble(text));
                    ResponseDto<Void> save = productVariantService.save(productVariant);
                    if (!save.isSuccess()) {
                        throw new Exception("Kutilmagan xatolik");
                    }
                } catch (Exception e) {
                    bot.sendMessage(botId, user.getChatId(), "\uD83D\uDE45\u200D♂️ Narxni faqat sonda kiritishingiz kerak\n\n" + ("Avvalgi narx: <code>%s</code>\n\nYangi nomini kiriting".formatted(productVariant.getPrice())), kyb.backBtn);
                    return;
                }
            }

        }
        productService.save(product);
        bot.sendPhoto(botId, user.getChatId(), productVariant.getImg(), kyb.editProductBtn(), false, "✅ Muvaffaqiyatli o'zgartirildi\n\n" + aboutCategoryWithPhoto(true, product, productVariant.getPrice(), productVariant, "uz"));
        eventCode(user, "crud product");
    }

    public void addProductVariant(BotInfo botInfo, BotUser user, String text) {
        Product product = productService.findById(user.getProductId()).getData();
        ProductVariant productVariant = productVariantService.findById(user.getProductVariantId()).getData();
        List<ProductVariant> variants = productVariantService.findAllByProductId(user.getProductId()).getData();
        String eventCode = user.getEventCode();
        if (text.equals(backButton)) {
            bot.sendPhoto(botInfo.getId(), user.getChatId(), productVariant.getImg(), kyb.getProductVariantsAndEditProductBtn(variants, productVariant.getId()), false, aboutCategoryWithPhoto(true, product, productVariant.getPrice(), productVariant, "uz"));
            eventCode(user, "crud product");
            return;
        }
        ProductVariant variant;
        ResponseDto<ProductVariant> checkVariant = productVariantService.draftProductVariant(product.getId());
        if (checkVariant.isSuccess()) {
            variant = checkVariant.getData();
        } else {
            variant = new ProductVariant();
            variant.setStatus("draft");
            variant.setActive(false);
            variant.setProductId(product.getId());
            productVariantService.save(variant);
        }
        String s;
        String e = eventCode;
        switch (eventCode) {
            case "add product variant to product get name uz" -> {
                s = "Muvaffaqiyatli saqlandi, ruscha nomini kiriting";
                e = "add product variant to product get name ru";
                variant.setNameUz(text);
                productVariantService.save(variant);
            }
            case "add product variant to product get name ru" -> {
                s = "Muvaffaqiyatli saqlandi, endi narxini kiriting";
                e = "add product variant to product get price";
                variant.setNameRu(text);
            }
            case "add product variant to product get price" -> {
                try {
                    s = "Muvaffaqiyatli saqlandi, endi mahsulot rasmini yuboring";
                    e = "add product variant to product get img";
                    variant.setPrice(Double.parseDouble(text));
                } catch (NumberFormatException ex) {
                    s = "Narxni sonda kiritishingiz kerak";
                    e = eventCode;
                }
            }
            case "is add product variant to product" -> {
                if (text.equals("✅ Ha")) {
                    variant.setActive(true);
                    variant.setStatus("open");
                    productVariantService.save(variant);
                    variants = productVariantService.findAllByProductId(user.getProductId()).getData();
                    user.setProductVariantId(variant.getId());
                    botUserService.save(user);
                    bot.sendMessage(botInfo.getId(), user.getChatId(), text , kyb.backBtn);
                    bot.sendPhoto(botInfo.getId(), user.getChatId(), variant.getImg(), kyb.getProductVariantsAndEditProductBtn(variants, user.getProductVariantId()), false, "✅ Muvaffaqiyatli qo'shildi" + aboutCategoryWithPhoto(true, product, variant.getPrice(), variant, "uz"));
                    eventCode(user, "crud product");
                } else if (text.equals("❌ Yo'q")) {
                    ProductVariant a = productVariantService.findById(user.getProductVariantId()).getData();
                    bot.sendPhoto(botInfo.getId(), user.getChatId(), productVariant.getImg(), kyb.getProductVariantsAndEditProductBtn(variants, a.getId()), false, "✅ Muvaffaqiyatli qo'shildi" + aboutCategoryWithPhoto(true, product, a.getPrice(), a, "uz"));
                    eventCode(user, "crud product");
                } else wrongBtn(botInfo.getId(), user.getChatId(), kyb.isSuccess("uz"));
                return;
            }
            case "add product variant to product get img" -> {
                s = "Ushbu turni haqiqatdan hamqo'shmoqchimisiz\n\n" + ("""
                        Tur nomi(uz) %s
                        Tur nomi(ru) %s
                        Tur narxi:  %s
                        """.formatted(variant.getNameUz(), variant.getNameRu(), formatPrice(variant.getPrice())));
                e = "is add product variant to product";
                eventCode(user, e);
                variant.setImg(text);
                productVariantService.save(variant);
                bot.sendPhoto(botInfo.getId(), user.getChatId(), text, kyb.isSuccess("uz"), false, s);
                return;
            }
            default -> {
                return;
            }
        }
        productVariantService.save(variant);
        eventCode(user, e);
        bot.sendMessage(botInfo.getId(), user.getChatId(), s);
        /*
         */
    }

    public void sendMsg(BotInfo botInfo, BotUser user, String text) {
        if (text.equals(backButton)) start(botInfo, user);
        else {
            try {
                ResponseDto<BotUser> checkUser = botUserService.findByUserChatId(user.getChatId(), botInfo.getId());
                if (checkUser.isSuccess()) {
                    user.setChatIdHelp(Long.valueOf(text));
                    botUserService.save(user);
                    bot.sendMessage(botInfo.getId(), user.getChatId(), "Yubormoqchi bo'lgan xabaringizni kiriting (Matn ko'rinishida)", kyb.backBtn);
                    eventCode(user, "send msg text");
                } else
                    bot.sendMessage(botInfo.getId(), user.getChatId(), "%d ga teng bo'lgan chat id topilmadi, boshqa chat id kiriting", kyb.backBtn);
            } catch (Exception e) {
                bot.sendMessage(botInfo.getId(), user.getChatId(), text);
            }
        }
    }

    public void sendMsgText(BotInfo botInfo, BotUser user, String text) {
        if (text.equals(backButton)) menu(botInfo.getId(), user, adminOnlineMagazineMenu[4], botInfo, bot.adminChatId);
        else {
            BotUser botUser = botUserService.findByUserChatId(user.getChatIdHelp(), botInfo.getId()).getData();
            if (botUser.getLang() == null) botUser.setLang("uz");
            ResponseDto<Void> sendMsg = bot.sendMessage(botInfo.getId(), user.getChatIdHelp(), text, kyb.replyBtn(user.getChatId(), botUser.getLang()));
            if (sendMsg.isSuccess()) {
                bot.sendMessage(botInfo.getId(), user.getChatId(), "Xabaringiz yetkazildi");
                start(botInfo, user);
                return;
            }
            bot.sendMessage(botInfo.getId(), user.getChatId(), "xabaringiz yetkazilmadi, xatolik yuz berdi", kyb.backBtn);
        }
    }


    public void replyMessage(BotInfo botInfo, BotUser user, String text) {
        if (text.equals(backButton)) {
            start(botInfo, user);
            return;
        }
        Long chatIdHelp = user.getChatIdHelp();
        BotUser botUser = botUserService.findByUserChatId(chatIdHelp, botInfo.getId()).getData();
        if (botUser.getLang() == null) botUser.setLang("uz");
        bot.sendMessage(botInfo.getId(), chatIdHelp, text, kyb.replyBtn(user.getChatId(), botUser.getLang()));
        bot.sendMessage(botInfo.getId(), user.getChatId(), "Xabaringiz muvaffaqiyatli yetkazildi", kyb.backBtn("uz"));
        start(botInfo, user);
    }

    public void branchMenu(BotInfo botInfo, BotUser user, String text, Long botId) {
        List<Branch> branches = branchRepository.findAllByActiveIsTrueAndStatusAndBotIdOrderByIdAsc(OPEN, botId);
        if (text.equals(backButton)) start(botInfo, user);
        else {
            if (text.equals(adminOnlineMagazineBranchMenu[0])) {
                if (branches.isEmpty()) {
                    bot.sendMessage(botId, user.getChatId(), "⚠️ <b>Sizda hech qanday filial mavjud emas!</b>\n\n" + "Filial qo‘shish uchun <b>\"%s\"</b> tugmasini bosing. ✅".formatted(adminOnlineMagazineBranchMenu[1]), kyb.branchMenu);

                    return;
                }
                bot.sendMessage(botId, user.getChatId(), "🏢 <b>Sizdagi mavjud filiallar ro‘yxati:</b>\n\n" + "Quyidagi ro‘yxatdan kerakli filialni tanlang.", kyb.branches(branches));
                eventCode(user, "get branches lists");
            } else if (text.equals(adminOnlineMagazineBranchMenu[1])) {
                bot.sendMessage(botId, user.getChatId(), """
                        🏢 <b>Yangi filial qo‘shish</b>
                        
                        Iltimos, yangi filialning nomini kiriting ⌨️""", kyb.backBtn);
                eventCode(user, "get new branch name");
            }
        }
    }

    public void addBranch(BotInfo botInfo, BotUser user, String text, String eventCode) {
        if (text.equals(backButton)) {
            menu(botInfo.getId(), user, adminOnlineMagazineMenu[5], botInfo, bot.adminChatId);
            return;
        }
        Branch branch = branchRepository.draftBranch();
        if (branch == null) {
            branch = new Branch();
            branch.setActive(false);
            branch.setStatus(DRAFT);
            branch.setBotId(botInfo.getId());
            branchRepository.save(branch);
        }
        if (eventCode.equals("get new branch name")) {
            Branch checkbranch = branchRepository.findByNameAndActiveIsTrue(text);
            if (checkbranch != null) {
                bot.sendMessage(botInfo.getId(), user.getChatId(), """
                        ⚠️ <b>Diqqat!</b>
                        
                        ❌ Bunday nomli filial allaqachon mavjud.
                        🔄 Iltimos, boshqa nom kiriting.""", kyb.backBtn);
                return;
            }
            branch.setName(text);
            branchRepository.save(branch);
            bot.sendMessage(botInfo.getId(), user.getChatId(), """
                    🏢 <b>%s</b> filiali uchun tavsif kiriting.
                    
                    ℹ️ Tavsif filial haqida qisqacha ma'lumot bo'lishi kerak.""".formatted(text));
            eventCode(user, "get new branch description");

        } else if (eventCode.equals("get new branch description")) {
            branch.setDescription(text);
            branchRepository.save(branch);
            bot.sendMessage(botInfo.getId(), user.getChatId(), """
                    ✅ <b>Muvaffaqiyatli saqlandi!</b>
                    
                    📍 Endi ushbu filial uchun mo'ljal kiriting.""");
            eventCode(user, "get new branch destination");
        } else if (eventCode.equals("get new branch destination")) {
            branch.setDestination(text);
            branchRepository.save(branch);
            bot.sendMessage(botInfo.getId(), user.getChatId(), """
                    ✅ <b>Muvaffaqiyatli saqlandi!</b>
                    
                    ⏳ Endi ushbu filialning <b>ish vaqtini</b> kiriting.
                    Masalan: <pre>09:00 - 18:00</pre>""");
            eventCode(user, "get new branch working hours");
        } else if (eventCode.equals("get new branch working hours")) {
            branch.setWorkingHours(text);
            branchRepository.save(branch);
            bot.sendMessage(botInfo.getId(), user.getChatId(), """
                    ✅ <b>Muvaffaqiyatli saqlandi!</b>
                    
                    📞 Endi ushbu filialning <b>telefon raqamini</b> kiriting.
                    Masalan: <pre>+998901234567</pre>""");
            eventCode(user, "get new branch phone");
        } else if (eventCode.equals("get new branch phone")) {
            branch.setPhone(text);
            branchRepository.save(branch);
            String caption = """
                    ✅ <b>Muvaffaqiyatli saqlandi!</b>
                    
                    📍 Endi ushbu filialning <b>joylashuvini (lokatsiya)</b> yuboring.
                    
                    🔹 <i>Lokatsiyani yuborish uchun Telegram'ning "📎" tugmasidan foydalaning.</i>""";
            bot.sendPhoto(botInfo.getId(), user.getChatId(), true, caption, "src/images/for-location.png");
            eventCode(user, "get new branch location");
        } else if (eventCode.equals("get new branch location")) {
            String caption = """
                    ✅ <b>Muvaffaqiyatli saqlandi!</b>
                    
                    📍 Endi ushbu filialning <b>joylashuvini (lokatsiya)</b> yuboring.
                    
                    🔹 <i>Lokatsiyani yuborish uchun Telegram'ning "📎" tugmasidan foydalaning.</i>""";
            bot.sendPhoto(botInfo.getId(), user.getChatId(), true, caption, "src/images/for-location.png");
            eventCode(user, " ");
        } else if (eventCode.equals("is add branch")) {
            if (text.equals("✅ Ha")) {
                branch.setActive(true);
                branch.setStatus(OPEN);
                branchRepository.save(branch);
                bot.sendMessage(botInfo.getId(), user.getChatId(), "✅ <b>Muvaffaqiyatli saqlandi!</b>");

                menu(botInfo.getId(), user, adminOnlineMagazineMenu[5], botInfo, bot.adminChatId);
            } else if (text.equals("❌ Yo'q")) {
                branch.setActive(false);
                branch.setStatus(OPEN);
                branchRepository.save(branch);
                bot.sendMessage(botInfo.getId(), user.getChatId(), "❌ <b>Operatsiya bekor qilindi!</b>");
                menu(botInfo.getId(), user, adminOnlineMagazineMenu[5], botInfo, bot.adminChatId);
            }
        } else if (eventCode.equals("add new branch has image")) {
            if (text.equals("✅ Ha")) {
                branch.setHasImage(true);
                bot.sendMessage(botInfo.getId(), user.getChatId(), "📸 <b>Endi filial rasmini yuboring</b>", true);
                branchRepository.save(branch);
                eventCode(user, "get new branch image");
            } else if (text.equals("❌ Yo'q")) {
                branch.setHasImage(false);
                branchRepository.save(branch);
                bot.sendMessage(botInfo.getId(), user.getChatId(), aboutBranch(branch) + "\n\n<b>Ushbu ma'lumotlar to'g'rimi?</b>", kyb.isSuccess("uz"));
                eventCode(user, "is add branch");
            } else return;
        }
    }

    public void addBranch(BotInfo botInfo, BotUser user, Location location) {
        Branch branch = branchRepository.draftBranch();
        Json json = new Json().setAddress(location.getLatitude(), location.getLongitude());
        String address = json.getAddress();
        Double latitude = location.getLatitude(), longitude = location.getLongitude();
        branch.setAddress(address);
        branch.setLatitude(latitude);
        branch.setLongitude(longitude);
        branchRepository.save(branch);
        bot.sendMessage(botInfo.getId(), user.getChatId(), """
                📸 Ushbu filialning rasmi bor bormi ?
                
                
                """, kyb.isSuccess("uz"));

        eventCode(user, "add new branch has image");
    }

    private String aboutBranch(Branch branch) {
        return """
                🏢 <b>Filial haqida ma'lumot:</b>
                
                🔹 <b>Nomi:</b> %s
                🔹 <b>Tavsifi:</b> %s
                🔹 <b>Mo'ljali:</b> %s
                🔹 <b>Ish vaqti:</b> %s
                🔹 <b>Telefon raqami:</b> %s
                🔹 <b>Manzil:</b> %s
                """.formatted(branch.getName(), branch.getDescription(), branch.getDestination(), branch.getWorkingHours(), branch.getPhone(), branch.getAddress());
    }

    public void addBranch(BotInfo botInfo, BotUser user, List<PhotoSize> photo) {
        String img = photo.get(photo.size() - 1).getFileId();
        Branch branch = branchRepository.draftBranch();
        branch.setImageUrl(img);
        branchRepository.save(branch);
        bot.sendPhoto(botInfo.getId(), user.getChatId(), img, kyb.isSuccess("uz"), false, aboutBranch(branch) + "\n\n<b>Ushbu ma'lumotlar to'g'rimi?</b>");
        eventCode(user, "is add branch");
    }

    public void getBranchesLists(BotInfo botInfo, BotUser user, String text) {
        if (text.equals(backButton)) {
            menu(botInfo.getId(), user, adminOnlineMagazineMenu[5], botInfo, bot.adminChatId);
            return;
        }
        Branch branch = branchRepository.findByNameAndActiveIsTrue(text);
        if (branch == null) {
            wrongBtn(botInfo.getId(), user.getChatId(), kyb.branches(branchRepository.findAllByActiveIsTrueAndStatusAndBotIdOrderByIdAsc(OPEN, botInfo.getId())));
            return;
        }
        Boolean hasImage = branch.getHasImage();
        user.setBranchId(branch.getId());
        if (hasImage) {
            bot.sendPhoto(botInfo.getId(), user.getChatId(), branch.getImageUrl(), kyb.crudBranch(true), false, aboutBranch(branch));
        } else {
            bot.sendMessage(botInfo.getId(), user.getChatId(), aboutBranch(branch), kyb.crudBranch(false));
        }
        botUserService.save(user);
    }

    public void getBranchesLists(BotInfo botInfo, BotUser user, String data, CallbackQuery callbackQuery, Integer messageId) {
        Branch branch;
        Optional<Branch> bOp = branchRepository.findById(user.getBranchId());
        if (bOp.isEmpty()) return;
        branch = bOp.get();
        Boolean hasImage = branch.getHasImage();
        switch (data) {
            case "delete" -> {
                String text = aboutBranch(branch) + "\n\n\n" + "\uD83D\uDD25 Haqiqatdan ham ushbu filialni o'chirmoqchimisiz ?";
                if (hasImage) {
                    bot.editCaption(botInfo.getId(), user.getChatId(), messageId, text, kyb.isSuccessBtn("uz"));
                } else {
                    bot.editMessageText(botInfo.getId(), user.getChatId(), messageId, text, kyb.isSuccessBtn("uz"));
                }
            }
            case "change_address", "change_phone", "change_description", "change_destination", "change_image",
                 "change_working_hours", "change_name" -> {
                eventCode(user, data);
                bot.deleteMessage(botInfo.getId(), user.getChatId(), messageId);
                String res;
                switch (data) {
                    case "change_address" ->
                            res = "📍 <b>Manzilni o'zgartirish uchun yangi joylashuv (lokatsiyas)ini yuboring:</b>\n\n" + "⛔️ Avvalgi manzil: <code>" + branch.getAddress() + "</code>";
                    case "change_phone" ->
                            res = "📱 <b>Telefon raqamingizni o'zgartirish uchun yangi raqamni yuboring:</b>\n\n" + "☎️ Avvalgi telefon raqami: <code>" + branch.getPhone() + "</code>";
                    case "change_description" ->
                            res = "📝 <b>Filial tavsifini o'zgartirish uchun yangi tavsifni yuboring:</b>\n\n" + "🗒 Avvalgi tavsif: <code>" + branch.getDescription() + "</code>";
                    case "change_destination" ->
                            res = "🎯 <b>Filial mo'ljalini o'zgartirish uchun yangi mo'ljalni yuboring:</b>\n\n" + "🎯 Avvalgi mo'ljal: <code>" + branch.getDestination() + "</code>";
                    case "change_image" -> res = "🖼 <b>Filial rasmi o'zgartirilishi uchun yangi rasmni yuboring:</b>";
                    case "change_working_hours" ->
                            res = "⏳ <b>Filial ish vaqtini o'zgartirish uchun yangi ish vaqtini yuboring:</b>\n\n" + "🕒 Avvalgi ish vaqti: <code>" + branch.getWorkingHours() + "</code>";
                    case "change_name" ->
                            res = "🏷 <b>Filial nomini o'zgartirish uchun yangi nomni yuboring:</b>\n\n" + "📝 Avvalgi filial nomi: <code>" + branch.getName() + "</code>";
                    default -> {
                        return;
                    }
                }
                bot.sendMessage(botInfo.getId(), user.getChatId(), res, kyb.backBtn);
            }
            case "remove_img" -> {
                branch.setHasImage(false);
                branchRepository.save(branch);
                bot.deleteMessage(botInfo.getId(), user.getChatId(), messageId);
                bot.sendMessage(botInfo.getId(), user.getChatId(), aboutBranch(branch), kyb.crudBranch(false));
            }
            case "add_img" -> {
                bot.deleteMessage(botInfo.getId(), user.getChatId(), messageId);
                bot.sendMessage(botInfo.getId(), user.getChatId(),
                        """
                                📷 <b>Rasm yuboring</b>
                                
                                Filial rasmini belgilash uchun istalgan rasmni jo‘nating yoki <b>ortga qaytish</b> tugmasini bosing.
                                """, kyb.backBtn);
                eventCode(user, data);

            }
            case "no delete" -> {
                bot.alertMessage(botInfo.getId(), callbackQuery, "❌ Operatsiya bekor qilindi!\n\n🚫 Ushbu filial o'chirilmadi.");
                String text = aboutBranch(branch);
                if (hasImage) {
                    bot.editCaption(botInfo.getId(), user.getChatId(), messageId, text, kyb.crudBranch(true));
                } else {
                    bot.editMessageText(botInfo.getId(), user.getChatId(), messageId, text, kyb.crudBranch(false));
                }
            }
            case "yes delete" -> {
                branch.setActive(false);
                branchRepository.save(branch);
                bot.alertMessage(botInfo.getId(), callbackQuery, "✅ Muvaffaqiyatli o'chirildi");
                bot.deleteMessage(botInfo.getId(), user.getChatId(), messageId);
                boolean isHas = !branchRepository.findAllByActiveIsTrueAndStatusAndBotIdOrderByIdAsc(OPEN, botInfo.getId()).isEmpty();
                if (isHas) branchMenu(botInfo, user, adminOnlineMagazineBranchMenu[0], botInfo.getId());
                else menu(botInfo.getId(), user, adminOnlineMagazineMenu[5], botInfo, bot.adminChatId);
            }
        }
    }

    public void editBranch(BotInfo botInfo, BotUser user, String text, Location location, Integer messageId) {
        Optional<Branch> bOp = branchRepository.findById(user.getBranchId());
        if (bOp.isEmpty()) {
            return;
        }
        Branch branch = bOp.get();
        List<Branch> branches = branchRepository.findAllByActiveIsTrueAndStatusAndBotIdOrderByIdAsc(OPEN, botInfo.getId());
        try {
            if (user.getEventCode().equals("change_address")) {
                if (text != null) {
                    if (text.equals(backButton)) {
                        eventCode(user, "get branches lists");
                        bot.sendMessage(botInfo.getId(), user.getChatId(), text, kyb.branches(branches));
                        getBranchesLists(botInfo, user, branch.getName());
                        return;
                    }
                }
                bot.deleteMessage(botInfo.getId(), user.getChatId(), messageId);
                return;
            } else {
                assert text != null;
                if (text.equals(backButton)) {
                    eventCode(user, "get branches lists");
                    bot.sendMessage(botInfo.getId(), user.getChatId(), text, kyb.branches(branches));
                    getBranchesLists(botInfo, user, branch.getName());
                    return;
                }
            }
        } catch (Exception e) {
            log.error(e);
        }
        String res;
        switch (user.getEventCode()) {
            case "change_address" -> {

                String oldAddress = branch.getAddress();
                Json json = new Json().setAddress(location.getLatitude(), location.getLongitude());
                branch.setAddress(json.getAddress());
                branch.setLatitude(location.getLatitude());
                branch.setLongitude(location.getLongitude());
                branchRepository.save(branch);
                res = """
                        ✅ <b>Muvaffaqiyatli o'zgartirildi!</b>
                        
                        📍 <b>Eski manzil:</b> <code>%s</code>
                        📍 <b>Yangi manzil:</b> <code>%s</code>
                        """.formatted(oldAddress, branch.getAddress());

            }
            case "change_phone" -> {
                String oldPhone = branch.getPhone();
                branch.setPhone(text);
                branchRepository.save(branch);
                res = """
                        ✅ <b>Muvaffaqiyatli o'zgartirildi!</b>
                        
                        📍 <b>Eski telefon raqam:</b> <code>%s</code>
                        📍 <b>Yangi telefon raqam:</b> <code>%s</code>
                        """.formatted(oldPhone, branch.getAddress());

            }
            case "change_description" -> {
                String oldDesc = branch.getDescription();
                branch.setDescription(text);
                branchRepository.save(branch);
                res = """
                        ✅ <b>Muvaffaqiyatli o'zgartirildi!</b>
                        
                        📍 <b>Eski tavsif:</b> <code>%s</code>
                        📍 <b>Yangi tavsif:</b> <code>%s</code>
                        """.formatted(oldDesc, branch.getDescription());
            }
            case "change_destination" -> {
                String oldDest = branch.getDestination();
                branch.setDestination(text);
                branchRepository.save(branch);
                res = """
                        ✅ <b>Muvaffaqiyatli o'zgartirildi!</b>
                        
                        📍 <b>Eski mo'ljal:</b> <code>%s</code>
                        📍 <b>Yangi mo'ljal:</b> <code>%s</code>
                        """.formatted(oldDest, branch.getDestination());
            }
            case "change_image" -> {
                String oldDest = branch.getImageUrl();
                branch.setImageUrl(text);
                branchRepository.save(branch);
                res = """
                        ✅ <b>Muvaffaqiyatli o'zgartirildi!</b>
                        """;
            }
            case "change_working_hours" -> {
                String oldTime = branch.getWorkingHours();
                branch.setWorkingHours(text);
                branchRepository.save(branch);
                res = """
                        ✅ <b>Muvaffaqiyatli o'zgartirildi!</b>
                        
                        📍 <b>Eski ish vaqti:</b> <code>%s</code>
                        📍 <b>Yangi ish vaqti:</b> <code>%s</code>
                        """.formatted(oldTime, branch.getWorkingHours());
            }
            case "change_name" -> {
                String oldName = branch.getName();
                Branch editBranch = branchRepository.findByNameAndActiveIsTrue(text);
                if (editBranch != null) {
                    bot.sendMessage(botInfo.getId(), user.getChatId(),
                            """
                            ❌ <b>Operatsiya bekor qilindi!</b>
                            
                            Sababi: bunday nomli boshqa filial allaqachon mavjud.
                            Iltimos, ro‘yxatdan birini tanlang yoki yangi filial qo'shing.
                            """, kyb.branches(branches));

                    eventCode(user, "get branches lists");
                    getBranchesLists(botInfo, user, branch.getName());
                    return;
                }
                if (oldName.equals(text)) {
                    res = """
                            Nomi o'zgartirilmadi!</b>
                            chunki avvalgi nom va yangi nom bir xil
                            
                            eski nom: %s
                            yangi nom: %s
                            """.formatted(oldName, branch.getName());

                } else {
                    branch.setName(text);
                    branchRepository.save(branch);
                    res = """
                            ✅ <b>Muvaffaqiyatli o'zgartirildi!</b>
                            
                            📍 <b>Eski eski nomi:</b> <code>%s</code>
                            📍 <b>Yangi yangi nomi:</b> <code>%s</code>
                            """.formatted(oldName, branch.getName());
                }
            }
            default -> {
                return;
            }
        }
        branches = branchRepository.findAllByActiveIsTrueAndStatusAndBotIdOrderByIdAsc(OPEN, botInfo.getId());
        bot.sendMessage(botInfo.getId(), user.getChatId(), res, kyb.branches(branches));
        eventCode(user, "get branches lists");
        getBranchesLists(botInfo, user, branch.getName());
    }

    public void addImg(Long id, BotUser user, PhotoSize photoSize) {
        Optional<Branch> bOp = branchRepository.findById(user.getBranchId());
        if (bOp.isEmpty()) {
            return;
        }
        Branch branch = bOp.get();
        branch.setImageUrl(photoSize.getFileId());
        branch.setHasImage(true);
        branchRepository.save(branch);
        eventCode(user, "get branches lists");
        bot.sendMessage(id, user.getChatId(), "✅ Muvaffaqiyatli o'zgartirildi!", kyb.branches(branchRepository.findAllByActiveIsTrueAndStatusAndBotIdOrderByIdAsc(OPEN, id)));
        bot.sendPhoto(id, user.getChatId(), branch.getImageUrl(), kyb.crudBranch(true), false, aboutBranch(branch));
    }

    public void addImg(Long id, BotUser user, String text, Message message) {
        if (text.equals(backButton)) {
            Optional<Branch> bOp = branchRepository.findById(user.getBranchId());
            if (bOp.isEmpty()) {
                return;
            }
            Branch branch = bOp.get();
            eventCode(user, "get branches lists");
            bot.sendMessage(id, user.getChatId(), text, kyb.branches(branchRepository.findAllByActiveIsTrueAndStatusAndBotIdOrderByIdAsc(OPEN, id)));
            if (branch.getHasImage()) {

                bot.sendPhoto(id, user.getChatId(), branch.getImageUrl(), kyb.crudBranch(false), false, aboutBranch(branch));
            } else bot.sendMessage(id, user.getChatId(), aboutBranch(branch) , kyb.crudBranch(false));
        } else {
            bot.deleteMessage(id, message.getChatId(), message.getMessageId());
        }
    }
}
