package com.github.ivanfomkin.bookshop.service.impl;

import com.github.ivanfomkin.bookshop.dto.author.AuthorElementDto;
import com.github.ivanfomkin.bookshop.entity.author.AuthorEntity;
import com.github.ivanfomkin.bookshop.exception.NotFoundException;
import com.github.ivanfomkin.bookshop.repository.AuthorRepository;
import com.github.ivanfomkin.bookshop.service.AuthorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        return authorRepository.findAuthorEntityBySlug(slug).orElseThrow(NotFoundException::new);
    }

    @Override
    public List<AuthorElementDto> convertAuthorsToDto(List<AuthorEntity> authorEntities) {
        List<AuthorElementDto> authorElementDtoList = new ArrayList<>();
        for (AuthorEntity authorEntity : authorEntities) {
            authorElementDtoList.add(new AuthorElementDto(authorEntity.getName(), authorEntity.getSlug()));
        }
        return authorElementDtoList;
    }
}
