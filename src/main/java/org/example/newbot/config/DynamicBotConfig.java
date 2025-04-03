package org.example.newbot.config;


import lombok.extern.log4j.Log4j2;
import org.example.newbot.model.BotInfo;
import org.example.newbot.repository.BotInfoRepository;
import org.example.newbot.service.DynamicBotService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.List;

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
                                            DynamicBotService botService) {
        return args -> {
            // Dastur ishga tushganda faol botlarni yuklash
            List<BotInfo> activeBots = botInfoRepository.findAllByActiveIsTrueOrderByIdDesc();
            for (BotInfo botInfo : activeBots) {
                try {
                    botService.createAndRegisterBot(botsApi, botInfo);
                    System.out.println("Bot download: " + botInfo.getBotUsername());
                } catch (Exception e) {
                    System.err.println("Bot do not download: " + botInfo.getBotUsername());
                    log.error(e);
                }
            }
        };
    }
}