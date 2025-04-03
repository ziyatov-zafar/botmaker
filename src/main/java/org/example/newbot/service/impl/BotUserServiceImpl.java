package org.example.newbot.service.impl;

import lombok.extern.log4j.Log4j2;
import org.example.newbot.dto.ResponseDto;
import org.example.newbot.model.BotUser;
import org.example.newbot.repository.BotInfoRepository;
import org.example.newbot.repository.BotUserRepository;
import org.example.newbot.service.BotUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class BotUserServiceImpl implements BotUserService {
    private final BotUserRepository botUserRepository;

    public BotUserServiceImpl(BotUserRepository botUserRepository, BotInfoRepository botInfoRepository) {
        this.botUserRepository = botUserRepository;
    }

    @Override
    public ResponseDto<BotUser> findByUserId(Long id, Long botId) {
        try {
            Optional<BotUser> checkBotUser = botUserRepository.findByUserIdAndBotId(botId, id);
            return checkBotUser.map(botUser -> new ResponseDto<>(true, "Ok", botUser)).orElseGet(() -> new ResponseDto<>(false, "User not found"));
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<BotUser> findByUserChatId(Long chatId, Long botId) {
        try {
            Optional<BotUser> checkBotUser = botUserRepository.findUserInBot(botId, chatId);
            return checkBotUser.map(botUser -> new ResponseDto<>(true, "Ok", botUser)).orElseGet(() -> new ResponseDto<>(false, "User not found"));
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<List<BotUser>> findAll(Long botId) {
        try {
            List<BotUser> list = botUserRepository.findUsersByBotId(botId);
            return new ResponseDto<>(true, "Ok", list);
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<Page<BotUser>> searchPhoneAndUsernameAndNickname(Long botId, int page, int size, String searchValue) {
        try {
            Page<BotUser> botUsers = botUserRepository.searchUsers(botId, searchValue, PageRequest.of(page, size));
            return new ResponseDto<>(true, "Ok", botUsers);
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<Void> save(BotUser user) {
        try {
            botUserRepository.save(user);
            return new ResponseDto<>(true, "Ok");
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<Page<BotUser>> findAllByRole(int page, int size, Long botId, String role) {
        try {
            Page<BotUser> userPage = botUserRepository.findUsersByBotIdAndRole(botId, role, PageRequest.of(page, size));
            return new ResponseDto<>(true, "Ok", userPage);
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<Page<BotUser>> findAll(int page, int size, Long botId) {
        try {
            Page<BotUser> res = botUserRepository.findUsersByBotId(botId, PageRequest.of(page, size));
            return new ResponseDto<>(true, "Ok", res);
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

}
