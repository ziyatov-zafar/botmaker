package org.example.newbot.bot.online_magazine_bot;

import lombok.extern.log4j.Log4j2;
import org.example.newbot.bot.Status;
import org.example.newbot.bot.online_magazine_bot.admin.AdminOnlineMagazineFunction;
import org.example.newbot.bot.online_magazine_bot.user.BranchUtil;
import org.example.newbot.bot.online_magazine_bot.user.UserOnlineMagazineFunction;
import org.example.newbot.dto.Json;
import org.example.newbot.dto.ResponseDto;
import org.example.newbot.model.BotInfo;
import org.example.newbot.model.BotUser;
import org.example.newbot.model.Branch;
import org.example.newbot.repository.BotInfoRepository;
import org.example.newbot.repository.BranchRepository;
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
import static org.example.newbot.bot.Status.OPEN;

@Log4j2
public class OnlineMagazineBot {
    private final DynamicBotService bot;
    private final BotInfoRepository botInfoRepository;
    private final BotUserService botUserService;
    private final AdminOnlineMagazineFunction adminFunction;
    private final UserOnlineMagazineFunction userFunction;
    private final BranchRepository branchRepository;

    public OnlineMagazineBot(DynamicBotService bot, BotInfoRepository botInfoRepository, BotUserService botUserService, AdminOnlineMagazineFunction adminFunction, UserOnlineMagazineFunction userFunction, BranchRepository branchRepository) {
        this.bot = bot;
        this.botInfoRepository = botInfoRepository;
        this.botUserService = botUserService;
        this.adminFunction = adminFunction;
        this.userFunction = userFunction;
        this.branchRepository = branchRepository;
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
            bot.setActive(botInfo.getId(), chatId);
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
        boolean inAdmin = isAdmin || chatId.equals(adminChatId);
        if (user.getRole().equals("block"))
            return;
        if (inAdmin) {
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
                            case "get new product name uz", "get new product name ru", "get new product desc uz",
                                 "get new product desc ru", "get new product variant name uz",
                                 "get new product variant name ru", "get new product variant price",
                                 "is add product to category" -> adminFunction.addProduct(botInfo, user, text);
                            case "get new product variant img" -> adminFunction.addProduct(botInfo, user, null);
                            case "is finished add product to category" ->
                                    adminFunction.isFinishedAddProductToCategory(botInfo, user, text);
                            case "crud product" -> adminFunction.crudProduct(botInfo, user, text);
                            case "edit name uz", "edit name ru", "edit desc uz", "edit desc ru", "edit price" ->
                                    adminFunction.editProductAndProductVariant(botInfo, botInfo.getId(), user, text);
                            case "add product variant to product get name uz",
                                 "add product variant to product get name ru",
                                 "add product variant to product get price", "is add product variant to product" ->
                                    adminFunction.addProductVariant(botInfo, user, text);
                            case "send msg" -> adminFunction.sendMsg(botInfo, user, text);
                            case "send msg text" -> adminFunction.sendMsgText(botInfo, user, text);
                            case "reply message" -> adminFunction.replyMessage(botInfo, user, text);
                            case "branch menu" -> adminFunction.branchMenu(botInfo, user, text, botInfo.getId());
                            case "get new branch name", "get new branch description", "get new branch destination",
                                 "get new branch working hours", "get new branch phone", "add new branch has image",
                                 "is add branch" -> adminFunction.addBranch(botInfo, user, text, eventCode);
                            case "get branches lists" -> adminFunction.getBranchesLists(botInfo, user, text);
                            case "change_address", "change_phone", "change_description", "change_destination",
                                 "change_working_hours", "change_name" ->
                                    adminFunction.editBranch(botInfo, user, text, null, message.getMessageId());
                            case "add_img" -> adminFunction.addImg(botInfo.getId(), user, text, message);
                        }
                    }
                } else if (message.hasPhoto()) {
                    String fileId = message.getPhoto().get(message.getPhoto().size() - 1).getFileId();
                    switch (eventCode) {
                        case "get new product variant img" -> adminFunction.addProduct(botInfo, user, fileId);
                        case "add product variant to product get img" ->
                                adminFunction.addProductVariant(botInfo, user, fileId);
                        case "get new branch image" -> adminFunction.addBranch(botInfo, user, message.getPhoto());
                        case "change_image" ->
                                adminFunction.editBranch(botInfo, user, message.getPhoto().get(message.getPhoto().size() - 1).getFileId(), null, message.getMessageId());
                        case "add_img" ->
                                adminFunction.addImg(botInfo.getId(), user, message.getPhoto().get(message.getPhoto().size() - 1));
                    }
                } else if (message.hasLocation()) {
                    if (eventCode.equals("get new branch location")) {
                        adminFunction.addBranch(botInfo, user, message.getLocation());
                    } else if (eventCode.equals("change_address")) {
                        adminFunction.editBranch(botInfo, user, null, message.getLocation(), message.getMessageId());
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
                if (data.startsWith("finish")){
                    adminFunction.finish(botInfo , user,data,messageId,callbackQuery);
                    return;
                }
                if (data.startsWith("cancelorder")){
                    adminFunction.finish1(botInfo , user,data,messageId,callbackQuery);
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
                    case "get branches lists" ->
                            adminFunction.getBranchesLists(botInfo, user, data, callbackQuery, messageId);

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
                            case "reply message" -> userFunction.replyMessage(botInfo, user, text, false);
                            case "request_lang" -> userFunction.requestLang(botInfo, user, text);
                            case "request_contact" -> userFunction.requestContact(botInfo, user, text);
                            case "menu" -> userFunction.menu(botInfo, user, text);
                            case "commentToAdmin" -> userFunction.commentToAdmin(botInfo, user, text);
                            case "deliveryType" -> userFunction.deliveryType(botInfo, user, text);
                            case "chooseLocation" -> userFunction.chooseLocation(botInfo, user, text);
                            case "locationList" -> userFunction.locationList(botInfo, user, text);
                            case "chooseBranch" -> userFunction.chooseBranch(botInfo, user, text);
                            case "deliveryCategoryMenu" -> userFunction.deliveryCategoryMenu(botInfo, user, text);
                            case "pickupCategoryMenu" -> userFunction.pickupCategoryMenu(botInfo, user, text);
                            case "pickupProductMenu" -> userFunction.pickupProductMenu(botInfo, user, text);
                            case "deliveryProductMenu" -> userFunction.deliveryProductMenu(botInfo, user, text);
                            case "chooseProductVariant" -> userFunction.chooseProductVariant(botInfo,user,text);
                            case "getPhoneNumber" -> userFunction.getPhoneNumber(botInfo , user,text);
                            case "branchLists" -> userFunction.branchLists(botInfo , user,text);
                        }
                    }
                } else if (message.hasContact()) {
                    if (eventCode.equals("request_contact")) {
                        userFunction.requestContact(botInfo, user, message.getContact());
                    }
                } else if (message.hasLocation()) {
                    Location location = message.getLocation();
//                    List<Branch> branches = branchRepository.findAllByActiveIsTrueAndStatusAndBotIdOrderByIdAsc(OPEN, botInfo.getId());
//                    List<Branch> branches = branchRepository.findAll();
//                    for (Branch branch : branches) {
//                        Double km = BranchUtil.findNearBranchKm(branches, location.getLatitude(), location.getLongitude());
//                        bot.sendMessage(botInfo.getId(), user.getChatId(),aboutBranch(branch) + "\n\nOrangizdagi masofa "+(km<1?"%.0f":"%.1f").formatted(km<1 ? km * 1000 : km) + (km < 1 ? " m":" km"));
//                    }
//                    bot.sendMessage(botInfo.getId(), user.getChatId(),"Sizga eng yaqin filial"+aboutBranch(BranchUtil.findNearestBranch(branches,location.getLatitude(),location.getLongitude())));

                    switch (eventCode) {
                        case "chooseLocation" -> userFunction.chooseLocation(botInfo, user, location);
                        case "chooseBranch" -> userFunction.chooseBranch(botInfo, user, location);
                        case "branchLists" -> userFunction.branchLists(botInfo , user,location);
                    }
                }
            } else if (update.hasCallbackQuery()) {
                CallbackQuery callbackQuery = update.getCallbackQuery();
                String data = callbackQuery.getData();
                Integer messageId = callbackQuery.getMessage().getMessageId();
                if (data.startsWith("reply")) {
                    userFunction.reply(botInfo, user.getChatId(), user, Long.valueOf(data.split("_")[1]), messageId, false);
                } else if (data.startsWith("cancelorder")) {
                    userFunction.cancelOrder(botInfo , user,data,callbackQuery,messageId) ;
                } else {
                    if (eventCode.equals("chooseProductVariant")){
                       userFunction.chooseProductVariant(botInfo,user,data , messageId,callbackQuery);
                    } else if (eventCode.equals("showBasket")) {
                        userFunction.showBasket(botInfo,user,data,messageId,callbackQuery);
                    }
                }
            }
        }
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

}
