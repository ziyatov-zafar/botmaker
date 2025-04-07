package org.example.newbot.config;


import lombok.extern.log4j.Log4j2;
import org.example.newbot.model.BotInfo;
import org.example.newbot.model.BotPrice;
import org.example.newbot.model.Payment;
import org.example.newbot.repository.BotInfoRepository;
import org.example.newbot.repository.BotPriceRepository;
import org.example.newbot.repository.PaymentRepository;
import org.example.newbot.service.DynamicBotService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.List;
import java.util.UUID;

@Configuration
@Log4j2
public class DynamicBotConfig {

    @Bean
    public TelegramBotsApi telegramBotsApi() throws TelegramApiException {
        return new TelegramBotsApi(DefaultBotSession.class);
    }

    @Bean
    public CommandLineRunner initializeBots(TelegramBotsApi botsApi,
                                            BotInfoRepository botInfoRepository,
                                            DynamicBotService botService, BotPriceRepository botPriceRepository, PaymentRepository paymentRepository) {
        for (BotPrice botPrice : botPriceRepository.findAll()) {
            botPrice.setActive(false);
            botPrice.setType(UUID.randomUUID().toString() + botPrice.getId());
            botPrice.setTypeText(UUID.randomUUID().toString() + botPrice.getId() + botPrice.getId());
            botPriceRepository.save(botPrice);
        }
        BotPrice botPrice = new BotPrice();
        botPrice.setDescription("Online dokon uchun bot, vazifasi mahsulotlaringizni online tarzda sotish uchun imkoniyat yaratib beradi");
        botPrice.setActive(true);
        botPrice.setPrice(2000000D);
        botPrice.setStatus("open");
        botPrice.setType("online-magazine");
        botPrice.setTypeText("Onlayn dokon");
        botPriceRepository.save(botPrice);

        updatePayment(paymentRepository);

        return args -> {
            List<BotInfo> activeBots = botInfoRepository.findAllByActiveIsTrueOrderByIdDesc();
            for (BotInfo botInfo : activeBots) {
                try {
                    botService.createAndRegisterBot(botsApi, botInfo);
                    log.info("Bot yuklandi: {}", botInfo.getBotUsername());
                } catch (Exception e) {
                    System.err.println("Bot yuklanmadi: " + botInfo.getBotUsername());
                    log.error(e);
                    botInfo.setActive(false);
                    botInfo.setType(UUID.randomUUID() + String.valueOf(false));
                    botInfo.setBotToken(botInfo.getBotToken() + UUID.randomUUID().toString() + botInfo.getId());
                    botInfo.setBotUsername(botInfo.getBotToken() + UUID.randomUUID().toString() + botInfo.getId());
                    botInfoRepository.save(botInfo);
                }
            }
        };
    }

    private void updatePayment(PaymentRepository paymentRepository) {
        List<Payment> list = paymentRepository.findAll();
        if (list.isEmpty()) {
            paymentRepository.save(Payment.builder().img("https://trustbank.uz/bitrix/templates/main_2020/tb/images/card__logo_humo.png").owner("Ziyatov Zafar").type("HUMO").number("9860100126717766").build());
        }
    }
}