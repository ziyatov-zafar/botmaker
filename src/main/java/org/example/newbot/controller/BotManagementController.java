package org.example.newbot.controller;

import org.example.newbot.model.BotInfo;
import org.example.newbot.repository.BotInfoRepository;
import org.example.newbot.service.DynamicBotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/bots")
public class BotManagementController {

    private final DynamicBotService botService;
    private final BotInfoRepository botInfoRepository;
    private final TelegramBotsApi botsApi;

    public BotManagementController(DynamicBotService botService,
                                   BotInfoRepository botInfoRepository,
                                   TelegramBotsApi botsApi) {
        this.botService = botService;
        this.botInfoRepository = botInfoRepository;
        this.botsApi = botsApi;
    }

    @GetMapping("add-new-bot")
    public ResponseEntity<String> addNewBot(@RequestParam String token/*,@RequestParam Long id*/) throws TelegramApiException {
        try {
            BotInfo botInfo = new BotInfo();
            botInfo.setActive(true);
            botInfo.setBotToken(token);
            botInfoRepository.save(botInfo);
            botService.createAndRegisterBot(botsApi, botInfo);
            return ResponseEntity.ok("Bot qo'shildi: " + botInfo.getBotUsername());
        } catch (Exception e) {
            return ResponseEntity.ok(e.getMessage());
        }
    }

    @GetMapping("/{botId}")
    public ResponseEntity<String> stopBot(@PathVariable Long botId) {
        Optional<BotInfo> botOptional = botInfoRepository.findById(botId);
        if (botOptional.isPresent()) {
            botService.stopBot(botId);
            BotInfo bot = botOptional.get();
            bot.setActive(false);
            botInfoRepository.save(bot);
            return ResponseEntity.ok("Bot to'xtatildi: " + botId);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<BotInfo>> getAllBots() {
        return ResponseEntity.ok(botInfoRepository.findAll());
    }

    @GetMapping("/{botId}/status")
    public ResponseEntity<Boolean> getBotStatus(@PathVariable Long botId) {
        return ResponseEntity.ok(botService.isBotRunning(botId));
    }
}