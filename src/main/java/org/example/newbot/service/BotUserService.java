package org.example.newbot.service;

import org.example.newbot.dto.ResponseDto;
import org.example.newbot.model.BotUser;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BotUserService {
    ResponseDto<BotUser> findByUserId(Long id , Long botId);
    ResponseDto<BotUser> findByUserChatId(Long chatId,Long botId);
    ResponseDto<List<BotUser>> findAll(Long botId);
    ResponseDto<Page<BotUser>>findAll(int page, int size , Long botId);
    ResponseDto<Page<BotUser>>findAllByRole(int page, int size , Long botId,String role);
    ResponseDto<Void> save(BotUser user);


    ResponseDto<Page<BotUser>>searchPhoneAndUsernameAndNickname(Long botId,int page,int size, String searchValue);
}
