package com.example.MyBookShopApp.service.impl;

import com.example.MyBookShopApp.model.Author;
import com.example.MyBookShopApp.repository.AuthorRepository;
import com.example.MyBookShopApp.service.AuthorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Primary
public class AuthorServiceJpaImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    public AuthorServiceJpaImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public Map<String, List<Author>> getAuthorsMap() {
        log.info("Получаю авторов в JPA репозитории");
        var allAuthors = authorRepository.findAll();
        return allAuthors.stream().collect(Collectors.groupingBy(a -> a.getLastName().substring(0, 1)));
    }
}
