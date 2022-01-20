package com.example.MyBookShopApp.service.impl;

import com.example.MyBookShopApp.entity.user.UserEntity;
import com.example.MyBookShopApp.repository.UserRepository;
import com.example.MyBookShopApp.service.UserService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.time.Instant;
import java.time.ZoneId;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserEntity getUserBySession(HttpSession session) {
        var user = userRepository.findUserEntityByHash(session.getId());
        if (user == null) {
            user = new UserEntity();
            user.setName("Empty User");
            user.setRegTime(Instant.ofEpochMilli(session.getCreationTime()).atZone(ZoneId.systemDefault()).toLocalDateTime());
            user.setHash(session.getId());
            userRepository.save(user);
        }
        return user;
    }

    @Override
    public boolean isAuthorized(HttpSession httpSession) {
        return true; // TODO: 20.01.2022 Заглушка. Исправить позже
    }
}
