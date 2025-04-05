package org.example.newbot.bot.online_magazine_bot.user;

import jakarta.ws.rs.core.Variant;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.newbot.dto.CartItemDto;
import org.example.newbot.dto.Json;
import org.example.newbot.dto.ResponseDto;
import org.example.newbot.model.*;
import org.example.newbot.repository.BranchRepository;
import org.example.newbot.repository.CartItemRepository;
import org.example.newbot.repository.CartRepository;
import org.example.newbot.repository.LocationRepository;
import org.example.newbot.service.*;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.ArrayList;
import java.util.List;

import static org.example.newbot.bot.StaticVariable.*;
import static org.example.newbot.bot.Status.DRAFT;
import static org.example.newbot.bot.Status.OPEN;
import static org.example.newbot.bot.online_magazine_bot.user.ConstVariable.*;

@RequiredArgsConstructor
@Log4j2

public class UserOnlineMagazineFunction {
    private final BotUserService userService;
    private final DynamicBotService bot;
    private final UserOnlineMagazineKyb kyb;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final ProductVariantService productVariantService;
    private final UserOnlineMagazineMsg msg;
    private final LocationRepository locationRepository;
    private final BranchRepository branchRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public void reply(BotInfo botInfo, Long chatId, BotUser user, Long newChatId, Integer messageId, boolean isAdmin) {
        user.setChatIdHelp(newChatId);
        user.setEventCode("reply message");
        userService.save(user);
        String lang = user.getLang();
        bot.deleteMessage(botInfo.getId(), chatId, messageId);
        if (lang == null) lang = "uz";
        String message = (lang.equals("uz") || isAdmin) ? "📩 Xabaringizni yuboring" : "📩 Отправьте ваше сообщение";
        bot.sendMessage(botInfo.getId(), chatId, message, kyb.backBtn(lang));
    }

    public void replyMessage(BotInfo botInfo, BotUser user, String text, boolean isAll) {
        String lang = user.getLang();
        if (inArray(text, backButton, backButtonRu)) {
            start(botInfo, user, false);
            return;
        }


        String username = user.getUsername() != null ? "@" + user.getUsername() : "—";
        if (!isAll) {
            Long chatIdHelp = user.getChatIdHelp();
            BotUser botUser = userService.findByUserChatId(chatIdHelp, botInfo.getId()).getData();
            if (lang == null) lang = "uz";
            bot.sendMessage(botInfo.getId(), chatIdHelp, """
                    🆔 Foydalanuvchi ma'lumotlari:
                    
                    🏷️ Niki: %s
                    🔗 Username: %s
                    🆔 ID: %d
                    💬 Chat ID: %d
                    
                    📝 Foydalanuvchi qoldirgan izoh:
                    %s
                    """.formatted(user.getNickname(), username, user.getId(), botUser.getChatId(), text), kyb.replyBtn(user.getChatId(), "uz"));
        } else {
            List<BotUser> admins = userService.findAllByRole(botInfo.getId(), "admin").getData();
            ResponseDto<BotUser> checkSuperAdmin = userService.findByUserChatId(bot.adminChatId, botInfo.getId());
            if (checkSuperAdmin.getData() != null && checkSuperAdmin.isSuccess()) {
                admins.add(checkSuperAdmin.getData());
            }
            for (BotUser admin : admins) {
                bot.sendMessage(botInfo.getId(), admin.getChatId(), """
                        🆔 Foydalanuvchi ma'lumotlari:
                        
                        🏷️ Niki: %s
                        🔗 Username: %s
                        🆔 ID: %d
                        💬 Chat ID: %d
                        
                        📝 Foydalanuvchi qoldirgan izoh:
                        %s
                        """.formatted(user.getNickname(), username, user.getId(), admin.getChatId(), text), kyb.replyBtn(user.getChatId(), "uz"));
            }
        }

        bot.sendMessage(botInfo.getId(), user.getChatId(),
                lang.equals("uz") ? "✅ Xabaringiz muvaffaqiyatli yetkazildi" : "✅ Ваше сообщение успешно доставлено",
                kyb.backBtn(lang));

        start(botInfo, user, false);
    }

    private boolean inArray(String text, String s1, String s2) {
        return text.equals(s1) || text.equals(s2);
    }

    private boolean inArray(String text, String s1) {
        return text.equals(s1);
    }

    public void start(BotInfo botInfo, BotUser user, boolean isMenu) {
        Long botId = botInfo.getId();
        String lang = user.getLang();
        String eventCode;
        String s;
        ReplyKeyboardMarkup markup;
        if (isMenu || user.getLang() == null) {
            if (user.getLang() == null) s = msg.requestLang(user.getNickname());
            else s = msg.changeLang(lang);
            markup = kyb.requestLang;
            eventCode = "request_lang";
        } else if (user.getPhone() == null) {
            s = msg.requestContact(lang);
            markup = kyb.requestContact(msg.contactBtn(lang));
            eventCode = "request_contact";
        } else {
            s = msg.menu(user.getLang());
            markup = kyb.menu(lang);
            eventCode = "menu";
        }
        eventCode(user, eventCode);
        bot.sendMessage(botId, user.getChatId(), s, markup);
    }

    private void start(BotInfo botInfo, BotUser user) {
        start(botInfo, user, false);
    }

    private void eventCode(BotUser user, String eventCode) {
        user.setEventCode(eventCode);
        userService.save(user);
    }

    public void requestLang(BotInfo botInfo, BotUser user, String text) {
        String lang;
        if (text.equals("\uD83C\uDDFA\uD83C\uDDFF O'zbek tili")) {
            lang = "uz";
        } else if (text.equals("\uD83C\uDDF7\uD83C\uDDFA Русский язык")) {
            lang = "ru";
        } else {
            wrongBtn(botInfo, user, kyb.requestLang);
            return;
        }
        user.setLang(lang);
        userService.save(user);
        start(botInfo, user, false);
    }

    public void requestContact(BotInfo botInfo, BotUser user, Contact contact) {
        if (contact.getUserId().equals(user.getChatId())) {
            String phone = contact.getPhoneNumber();
            if (phone.charAt(0) != '+') phone = "+" + phone;
            user.setPhone(phone);
            userService.save(user);
            start(botInfo, user, false);
        } else {
            wrongBtn(botInfo, user, kyb.requestContact(msg.contactBtn(user.getLang())));
        }
    }

    private void wrongBtn(BotInfo botInfo, BotUser user, ReplyKeyboardMarkup markup) {
        bot.sendMessage(botInfo.getId(), user.getChatId(), msg.errorBtn(user.getLang()), markup);
    }

    public void requestContact(BotInfo botInfo, BotUser user, String text) {
        wrongBtn(botInfo, user, kyb.requestContact(msg.contactBtn(user.getLang())));
    }

    public void menu(BotInfo botInfo, BotUser user, String text) {
        String[] menu = menuBtn(user.getLang());
        String lang = user.getLang();
        if (text.equals(menu[0])) {
            bot.sendMessage(botInfo.getId(), user.getChatId(), msg.deliveryType(lang), kyb.deliveryType(lang));
            eventCode(user, "deliveryType");
        } else if (text.equals(menu[1])) {

        } else if (text.equals(menu[2])) {
            bot.sendMessage(botInfo.getId(), user.getChatId(), msg.commentMsg(lang), kyb.backBtn(lang));
            eventCode(user, "commentToAdmin");
        } else if (text.equals(menu[3])) {
            start(botInfo, user, true);
        } else {
            wrongBtn(botInfo, user, kyb.menu(user.getLang()));
            return;
        }
    }

    public void commentToAdmin(BotInfo botInfo, BotUser user, String text) {
        if (inArray(text, backButton, backButtonRu)) {
            start(botInfo, user, false);
        } else {
            replyMessage(botInfo, user, text, true);
        }
    }

    public void deliveryType(BotInfo botInfo, BotUser user, String text) {
        String lang = user.getLang(), s, eventCode;
        String[] buttons = ConstVariable.deliveryType(lang);
        if (inArray(text, backButton, backButtonRu)) {
            start(botInfo, user, false);
            return;
        } else if (inArray(text, buttons[0])) {
            user.setDeliveryType("delivery");
            user.setEventCode("chooseLocation");
            userService.save(user);
            bot.sendMessage(botInfo.getId(), user.getChatId(), msg.getLocation(lang), kyb.getLocation(lang));
        } else if (inArray(text, buttons[1])) {
            user.setDeliveryType("pickup");
            user.setEventCode("chooseBranch");
            List<Branch> branches = branchRepository.findAllByActiveIsTrueAndStatusAndBotIdOrderByIdAsc(OPEN, botInfo.getId());
            if (branches.isEmpty()) {
                bot.sendMessage(botInfo.getId(), user.getChatId(), msg.notFoundBranch(lang, text), kyb.deliveryType(user.getLang()));
                return;
            }
            bot.sendMessage(botInfo.getId(), user.getChatId(), msg.chooseBranch(lang), kyb.chooseBranch(user.getLang(), branches));
            userService.save(user);
        }

    }

    public void chooseLocation(BotInfo botInfo, BotUser user, Location location) {
        try {
            org.example.newbot.model.Location l = new org.example.newbot.model.Location();
            Double longitude = location.getLongitude();
            Double latitude = location.getLatitude();
            Json json = new Json().setAddress(latitude, longitude);
            if (json == null || json.getAddress() == null || !json.getCountry().getCode().equals("uz")) {
                bot.sendMessage(botInfo.getId(), user.getChatId(), msg.wrongLocation(user.getLang()), kyb.getLocation(user.getLang()));
                return;
            }
            org.example.newbot.model.Location checkAddress = locationRepository.findByAddressAndUserId(json.getAddress(), user.getId());
            boolean isSave = true;
            if (checkAddress != null) {
                isSave = false;
                l = checkAddress;
            } else {
                l.setAddress(json.getAddress());
                l.setLatitude(latitude);
                l.setLongitude(longitude);
                l.setUserId(user.getId());
            }
            l.setActive(false);
            locationRepository.save(l);
            user.setLatitude(latitude);
            user.setLongitude(longitude);
            user.setAddress(json.getAddress());
            userService.save(user);
            bot.sendMessage(botInfo.getId(), user.getChatId(), msg.isSuccessLocation(user.getLang(), json.getAddress()), kyb.isSuccessLocation(user.getLang()));
        } catch (Exception e) {
            bot.sendMessage(botInfo.getId(), user.getChatId(), e.getMessage());
        }

    }

    public void chooseLocation(BotInfo botInfo, BotUser user, String text) {
        String lang = user.getLang();
        String[] locations = location(lang);
        if (text.equals(locations[0])) {
            List<org.example.newbot.model.Location> list = locationRepository.findAllByUserIdAndActiveIsTrueOrderByIdAsc(user.getId());
            if (list.isEmpty()) {
                bot.sendMessage(botInfo.getId(), user.getChatId(), msg.emptyLocation(lang), kyb.getLocation(lang));
                return;
            }
            bot.sendMessage(botInfo.getId(), user.getChatId(), msg.locationList(lang), kyb.locationList(list, lang));
            eventCode(user, "locationList");
        } else if (text.equals(locations[2])) {
//            deliveryType(botInfo, user, ConstVariable.deliveryType(lang)[0]);
            menu(botInfo, user, menuBtn(user.getLang())[0]);
        } else if (text.equals(isSuccessForText(lang)[0])) {
            org.example.newbot.model.Location checkAddress = locationRepository.findByAddressAndUserId(user.getAddress(), user.getId());
            checkAddress.setActive(true);
            locationRepository.save(checkAddress);
            if (sendCategories(botInfo, user)) {
                eventCode(user, "deliveryCategoryMenu");
            }
        } else if (text.equals(isSuccessForText(lang)[1])) {
            deliveryType(botInfo, user, ConstVariable.deliveryType(lang)[0]);
        } else wrongBtn(botInfo, user, kyb.getLocation(lang));
    }

    public void locationList(BotInfo botInfo, BotUser user, String text) {
        String lang = user.getLang();
        if (inArray(text, backButton, backButtonRu)) {
            deliveryType(botInfo, user, ConstVariable.deliveryType(lang)[0]);
        } else {
            org.example.newbot.model.Location checkLocation = locationRepository.findByAddressAndUserId(text, user.getId());
            if (checkLocation != null) {
                user.setLatitude(checkLocation.getLatitude());
                user.setLongitude(checkLocation.getLongitude());
                user.setAddress(checkLocation.getAddress());
                userService.save(user);
                if (sendCategories(botInfo, user)) {
                    eventCode(user, "deliveryCategoryMenu");
                }
            } else {
                wrongBtn(botInfo, user, kyb.locationList(
                        locationRepository.findAllByUserIdAndActiveIsTrueOrderByIdAsc(
                                user.getId()
                        ), lang)
                );
            }
        }
    }

    private boolean sendCategories(BotInfo botInfo, BotUser user) {
        String lang = user.getLang();
        Long botId = botInfo.getId();
        List<Category> list = categoryService.findAllByBotId(botId).getData();
        if (list.isEmpty()) {
            bot.sendMessage(botInfo.getId(), user.getChatId(), msg.emptyCategory(lang) + "\n\n" + msg.menu(lang), kyb.menu(user.getLang()));
            eventCode(user, "menu");
            return false;
        } else {
            bot.sendMessage(botInfo.getId(), user.getChatId(), msg.categoryMenu(lang), kyb.setCategories(list, lang));
            return true;
        }
    }

    private boolean sendProducts(BotInfo botInfo, BotUser user) {
        String lang = user.getLang();
        Long botId = botInfo.getId();
        List<Product> list = productService.findAllByCategoryId(user.getCategoryId()).getData();
        Category category = categoryService.findById(user.getCategoryId()).getData();
        String categoryName = lang.equals("uz") ? category.getNameUz() : category.getNameRu();
        if (list.isEmpty()) {
            bot.sendMessage(botInfo.getId(), user.getChatId(), msg.emptyProducts(categoryName, lang), kyb.setCategories(categoryService.findAllByBotId(botId).getData(), lang));
            return false;
        } else {
            bot.sendMessage(botInfo.getId(), user.getChatId(), msg.categoryMenu(lang), kyb.setProductsUser(list, lang));
            return true;
        }
    }

    public void chooseBranch(BotInfo botInfo, BotUser user, String text) {
        if (text.equals(backButton)) {
            menu(botInfo, user, menuBtn(user.getLang())[0]);
        } else {
            Branch checkBranch = branchRepository.findByNameAndActiveIsTrue(text);
            if (checkBranch != null) {
                user.setLatitude(checkBranch.getLatitude());
                user.setLongitude(checkBranch.getLongitude());
                user.setAddress(checkBranch.getAddress());
                userService.save(user);
                if (sendCategories(botInfo, user)) {
                    eventCode(user, "pickupCategoryMenu");
                }
            } else {
                wrongBtn(botInfo, user, kyb.chooseBranch(user.getLang(), branchRepository.findAllByActiveIsTrueAndStatusAndBotIdOrderByIdAsc(OPEN, botInfo.getId())));
            }
        }
    }

    public void chooseBranch(BotInfo botInfo, BotUser user, Location location) {
        List<Branch> branches = branchRepository.findAllByActiveIsTrueAndStatusAndBotIdOrderByIdAsc(OPEN, botInfo.getId());
        Branch branch = BranchUtil.findNearestBranch(branches, location.getLatitude(), location.getLongitude());
        if (!branch.getHasImage())
            bot.sendMessage(botInfo.getId(), user.getChatId(), msg.branchInformationWithDistance(user.getLang(), branch, location));
        else {
            bot.sendPhoto(botInfo.getId(), user.getChatId(), branch.getImageUrl(), kyb.deliveryType(user.getLang()), false, msg.branchInformationWithDistance(user.getLang(), branch, location));
        }
        if (sendCategories(botInfo, user)) {
            eventCode(user, "pickupCategoryMenu");
        }
    }

    public void deliveryCategoryMenu(BotInfo botInfo, BotUser user, String text) {
        handleCategory(botInfo, user, text);
    }

    private void handleCategory(BotInfo botInfo, BotUser user, String text) {
        if (inArray(text, backButton, backButtonRu)) {
            if (user.getEventCode().equals("deliveryCategoryMenu")) {
                deliveryType(botInfo, user, ConstVariable.deliveryType(user.getLang())[0]);
            } else if (user.getEventCode().equals("pickupCategoryMenu")) {
                deliveryType(botInfo, user, ConstVariable.deliveryType(user.getLang())[1]);
            }
            return;
        }
        ResponseDto<Category> checkCategory = checkCategory(botInfo, user, text);
        if (checkCategory.isSuccess()) {
            Category category = checkCategory.getData();
            user.setCategoryId(category.getId());
            if (sendProducts(botInfo, user)) {
                user.setEventCode(user.getDeliveryType() + "ProductMenu");
                userService.save(user);
            }
        } else {
            List<Category> list = categoryService.findAllByBotId(botInfo.getId()).getData();
            wrongBtn(botInfo, user, kyb.setCategories(list, user.getLang()));
        }
    }


    private ResponseDto<Category> checkCategory(BotInfo botInfo, BotUser user, String text) {
        ResponseDto<Category> checkCategory;
        if (user.getLang().equals("uz")) {
            checkCategory = categoryService.findByNameUz(botInfo.getId(), text);
        } else checkCategory = categoryService.findByNameRu(botInfo.getId(), text);
        return checkCategory;
    }

    public void pickupCategoryMenu(BotInfo botInfo, BotUser user, String text) {
        handleCategory(botInfo, user, text);
    }

    public void pickupProductMenu(BotInfo botInfo, BotUser user, String text) {
        handleProduct(botInfo, user, text);
    }

    private void handleProduct(BotInfo botInfo, BotUser user, String text) {
        if (inArray(text, backButton, backButtonRu)) {
            eventCode(user, user.getDeliveryType() + "CategoryMenu");
            sendCategories(botInfo, user);
            return;
        }
         if (text.equals(cardBtn(user.getLang()))){
             showBasket(botInfo, user);
             return;
         }
        ResponseDto<Product> checkProduct;
        if (user.getLang().equals("uz")) {
            checkProduct = productService.findByNameUz(
                    text, user.getCategoryId()
            );
        } else {
            checkProduct = productService.findByNameRu(
                    text, user.getCategoryId()
            );
        }
        if (checkProduct.isSuccess()) {
            List<ProductVariant> variants = productVariantService.findAllByProductId(checkProduct.getData().getId()).getData();
            user.setProductVariantId(variants.get(0).getId());
            user.setProductId(checkProduct.getData().getId());
            user.setCount(1);
            user.setEventCode("chooseProductVariant");
            userService.save(user);
            setVariants(botInfo.getId(), user, text, variants, checkProduct.getData(), variants.get(0));
        } else {
            eventCode(user, user.getDeliveryType() + "CategoryMenu");
            sendCategories(botInfo, user);
        }
    }

    private void setVariants(Long botId, BotUser user, String text, List<ProductVariant> variants, Product product, ProductVariant variant) {
        String caption = msg.productCaption(user.getLang(), product, variant, user.getCount());
        InlineKeyboardMarkup markup = kyb.setProductVariant(user.getLang(), variants, variant, variants.size() == 1, 1);
        bot.sendMessage(botId, user.getChatId(), text, kyb.backAndBasket(user.getLang()));
        bot.sendPhoto(botId, user.getChatId(), variant.getImg(), markup, true, caption);
    }


    public void deliveryProductMenu(BotInfo botInfo, BotUser user, String text) {
        handleProduct(botInfo, user, text);
    }

    public void chooseProductVariant(BotInfo botInfo, BotUser user, String text) {
        Category category = categoryService.findById(user.getCategoryId()).getData();
        if (inArray(text, backButton, backButtonRu)) {
            user.setCount(1);
            userService.save(user);
            handleCategory(botInfo, user, user.getLang().equals("uz") ? category.getNameUz() : category.getNameRu());
        } else if (text.equals(cardBtn(user.getLang()))) {
            showBasket(botInfo,user);
        }
    }

    private void showBasket(BotInfo botInfo, BotUser user) {
        Cart cart = cartRepository.findByStatusAndUserIdAndTypeAndActiveIsTrue(DRAFT, user.getId(), user.getDeliveryType());
        List<CartItem> carts = cartItemRepository.findAllByActiveIsTrueAndCartId(cart.getId());
        String basketMsg = msg.basket(convertDto(carts), user.getLang());//qilinvotti
        bot.sendMessage(botInfo.getId(), user.getChatId(), basketMsg, kyb.basket(user.getLang() , convertDto(carts) , null));
    }

    private List<CartItemDto> convertDto(List<CartItem> carts) {
        List<CartItemDto> list = new ArrayList<>();
        for (CartItem cartItem : carts) {
            Product product = productService.findById(cartItem.getProductId()).getData();
            Category category = categoryService.findById(cartItem.getCategoryId()).getData();
            ProductVariant variant = productVariantService.findById(cartItem.getProductVariantId()).getData();
            CartItemDto dto = new CartItemDto();
            dto.setId(cartItem.getId());
            dto.setPrice(variant.getPrice());
            dto.setTotalPrice(variant.getPrice() * cartItem.getQuantity());
            dto.setQuantity(cartItem.getQuantity());
            dto.setCategoryNameRu(category.getNameRu());
            dto.setCategoryNameUz(category.getNameUz());
            dto.setProductNameRu(product.getNameRu());
            dto.setProductNameUz(product.getNameUz());
            dto.setProductVariantNameRu(variant.getNameRu());
            dto.setProductVariantNameUz(variant.getNameUz());
            list.add(dto);
        }
        return list;
    }

    public void chooseProductVariant(BotInfo botInfo, BotUser user, String data, Integer messageId, CallbackQuery callbackQuery) {
        Product product = productService.findById(user.getProductId()).getData();
        Category category = categoryService.findById(product.getCategoryId()).getData();
        ProductVariant variant = productVariantService.findById(user.getProductVariantId()).getData();
        List<ProductVariant> variants = productVariantService.findAllByProductId(product.getId()).getData();
        if (data.equals("plus")) {
            user.setCount(user.getCount() + 1);
        } else if (data.equals("minus")) {
            if (user.getCount() == 1) {
                bot.alertMessage(botInfo.getId(), callbackQuery,
                        user.getLang().equals("uz") ? "1 dan kam tanlash mumkin emas" : "Невозможно выбрать меньше 1");
                return;
            }
            user.setCount(user.getCount() - 1);
        } else {
            if (data.equals("count")) {
                bot.alertMessage(botInfo.getId(), callbackQuery,
                        user.getCount() + " " + (user.getLang().equals("uz") ? "ta" : "шт"));
            } else if (data.equals("basket")) {
                addBasket(user);
                bot.alertMessage(botInfo.getId(), callbackQuery, msg.addBasketMsg(user.getLang()));
                bot.deleteMessage(botInfo.getId(), user.getChatId(), messageId);
                handleCategory(botInfo, user, user.getLang().equals("uz") ? category.getNameUz() : category.getNameRu());
            }
            return;
        }
        bot.editCaption(botInfo.getId(), user.getChatId(), messageId, msg.productCaption(
                        user.getLang(), product, variant, user.getCount()
                ), kyb.setProductVariant(
                        user.getLang(), variants, variant, variants.size() == 1, user.getCount()
                )
        );
        userService.save(user);
    }

    private void addBasket(BotUser user) {
        Cart cart = cartRepository.findByStatusAndUserIdAndTypeAndActiveIsTrue(DRAFT, user.getId(), user.getDeliveryType());
        if (cart == null) {
            cart = new Cart();
            cart.setUserId(user.getId());
            cart.setActive(true);
            cart.setStatus(DRAFT);
            cart.setType(user.getDeliveryType());
            cartRepository.save(cart);
            cart = cartRepository.findByStatusAndUserIdAndTypeAndActiveIsTrue(DRAFT, user.getId(), user.getDeliveryType());
        }
        CartItem cartItem = new CartItem();
        cartItem.setCartId(cart.getId());
        cartItem.setActive(true);
        cartItem.setCategoryId(user.getCategoryId());
        cartItem.setProductId(user.getProductId());
        cartItem.setProductVariantId(user.getProductVariantId());
        cartItem.setQuantity(user.getCount());
        cartItemRepository.save(cartItem);
    }
}
