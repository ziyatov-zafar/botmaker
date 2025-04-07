package org.example.newbot.bot;

import org.example.newbot.model.BotInfo;
import org.example.newbot.repository.BotInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExampleController {
    @Autowired
    private BotInfoRepository botInfoRepository;
    @GetMapping
    public Page<BotInfo> findAll(boolean active, int page, int size) {
        return ( botInfoRepository.findAllByActiveOrderByIdDesc(active, PageRequest.of(page, size)));
    }
}
