package org.example.newbot.service.impl;

import lombok.extern.log4j.Log4j2;
import org.example.newbot.dto.ResponseDto;
import org.example.newbot.model.User;
import org.example.newbot.repository.UserRepository;
import org.example.newbot.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class UserServiceIml implements UserService {
    private final UserRepository userRepository;

    public UserServiceIml(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public ResponseDto<User> checkUser(Long chatId) {
        try {
            User user = userRepository.findByChatId(chatId);
            if (user == null) {
                throw new Exception("Not found user");
            }
            return new ResponseDto<>(true, "Ok", user);
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<Void> save(User user) {
        try {
            userRepository.save(user);
            return new ResponseDto<>(true, "Ok");
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }



    @Override
    public ResponseDto<User> findById(Long userId) {
        try {
            Optional<User> userOp = userRepository.findById(userId);
            if (userOp.isPresent()) {
                return new ResponseDto<>(true, "Ok", userOp.get());
            }
            throw new Exception("Not found user");
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<List<User>> findAll() {
        try {
            return new ResponseDto<>(true , "Ok" , userRepository.findAll(Sort.by("id")));
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false , e.getMessage());
        }
    }


    @Override
    public ResponseDto<Page<User>> findAllByUsername(String username, int page, int size) {
        try {
            Page<User> userPage = userRepository.findByUsernameContainingIgnoreCaseOrderByIdDesc(username, PageRequest.of(page, size));
            return new ResponseDto<>(true, "Ok", userPage);
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false , e.getMessage());
        }
    }

    @Override
    public ResponseDto<Page<User>> findAllByNickname(String nickname, int page, int size) {
        try {
            Page<User> userPage = userRepository.findByNicknameContainingIgnoreCaseOrderByIdDesc(nickname, PageRequest.of(page, size));
            return new ResponseDto<>(true, "Ok", userPage);
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<Page<User>> findAllByRole(String role, int page, int size) {
        try {
            Page<User> userPage = userRepository.findAllByRoleOrderByIdAsc(role, PageRequest.of(page, size));
            return new ResponseDto<>(true, "Ok", userPage);
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<Page<User>> findAll(int page, int size) {
        try {
            Page<User> users = userRepository.findAllByOrderByIdDesc(PageRequest.of(page, size));
            return new ResponseDto<>(true , "Ok", users);
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false  , e.getMessage());
        }
    }
}
