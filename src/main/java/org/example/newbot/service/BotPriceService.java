package org.example.newbot.service;

import org.example.newbot.dto.ResponseDto;
import org.example.newbot.model.BotPrice;

import java.util.List;

public interface BotPriceService {
    ResponseDto<List<BotPrice>>findAll();
    ResponseDto<BotPrice> findByTypeText(String type);
}
