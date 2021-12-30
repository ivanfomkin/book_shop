package com.example.MyBookShopApp.service.impl;

import com.example.MyBookShopApp.entity.author.AuthorEntity;
import com.example.MyBookShopApp.repository.AuthorRepository;
import com.example.MyBookShopApp.service.AuthorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AuthorServiceJpaImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    public AuthorServiceJpaImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public Map<String, List<AuthorEntity>> getAuthorsMap() {
        var allAuthors = authorRepository.findAll();
        return allAuthors.stream().collect(Collectors.groupingBy(a -> a.getName().substring(0, 1)));
    }

    @Override
    public AuthorEntity getAuthorBySlug(String slug) {
        return authorRepository.findAuthorEntityBySlug(slug);
    }
}
