package org.example.newbot.service.impl;

import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.example.newbot.dto.ResponseDto;
import org.example.newbot.model.BotPrice;
import org.example.newbot.repository.BotPriceRepository;
import org.example.newbot.service.BotPriceService;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
public class BotPriceServiceImpl implements BotPriceService {
    private final BotPriceRepository botPriceRepository;

    public BotPriceServiceImpl(BotPriceRepository botPriceRepository) {
        this.botPriceRepository = botPriceRepository;
    }

    @Override
    public ResponseDto<BotPrice> findByTypeText(String type) {
        try {
            BotPrice typeText = botPriceRepository.findByTypeText(type);
            if (typeText != null) {
                return new ResponseDto<>(true , "Ok", typeText);
            }return  new ResponseDto<>(false , "Not found");
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<List<BotPrice>> findAll() {
        try {
            return new ResponseDto<>(true, "Ok", botPriceRepository.findAllByActiveIsTrueAndStatus("open"));
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }
}
