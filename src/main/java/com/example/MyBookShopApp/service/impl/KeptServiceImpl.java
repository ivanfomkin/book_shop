package com.example.MyBookShopApp.service.impl;

import com.example.MyBookShopApp.entity.enums.Book2UserType;
import com.example.MyBookShopApp.entity.user.UserEntity;
import com.example.MyBookShopApp.repository.Book2UserRepository;
import com.example.MyBookShopApp.service.KeptService;
import org.springframework.stereotype.Service;

@Service
public class KeptServiceImpl implements KeptService {
    private final Book2UserRepository book2UserRepository;

    public KeptServiceImpl(Book2UserRepository book2UserRepository) {
        this.book2UserRepository = book2UserRepository;
    }

    @Override
    public int getKeptAmount(UserEntity user) {
        return book2UserRepository.countBooksByUserAndStatus(user, Book2UserType.KEPT);
    }
}
