package com.github.ivanfomkin.bookshop.service.impl;

import com.github.ivanfomkin.bookshop.dto.CommonPageableDto;
import com.github.ivanfomkin.bookshop.dto.author.AuthorEditDto;
import com.github.ivanfomkin.bookshop.dto.author.AuthorElementDto;
import com.github.ivanfomkin.bookshop.entity.author.AuthorEntity;
import com.github.ivanfomkin.bookshop.exception.NotFoundException;
import com.github.ivanfomkin.bookshop.repository.AuthorRepository;
import com.github.ivanfomkin.bookshop.service.AuthorService;
import com.github.ivanfomkin.bookshop.service.ResourceStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AuthorServiceJpaImpl implements AuthorService {
    private final AuthorRepository authorRepository;
    private final ResourceStorageService resourceStorageService;

    public AuthorServiceJpaImpl(AuthorRepository authorRepository, ResourceStorageService resourceStorageService) {
        this.authorRepository = authorRepository;
        this.resourceStorageService = resourceStorageService;
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
    public List<AuthorElementDto> convertAuthorsToDto(Iterable<AuthorEntity> authorEntities) {
        List<AuthorElementDto> authorElementDtoList = new ArrayList<>();
        for (AuthorEntity authorEntity : authorEntities) {
            authorElementDtoList.add(new AuthorElementDto(authorEntity.getId(), authorEntity.getName(), authorEntity.getSlug()));
        }
        return authorElementDtoList;
    }

    @Override
    public List<AuthorElementDto> getAllAuthors() {
        return convertAuthorsToDto(authorRepository.findAll(Sort.by(Sort.Order.asc("name"))));
    }

    @Override
    public CommonPageableDto<AuthorElementDto> getPageableAllAuthors(Pageable pageable, String searchQuery) {
        Page<AuthorEntity> authorEntityPage;
        if (searchQuery == null || searchQuery.isBlank()) {
            authorEntityPage = authorRepository.findAll(pageable);
        } else {
            authorEntityPage = authorRepository.findAuthorEntitiesByNameContainingIgnoreCase(pageable, searchQuery);
        }
        CommonPageableDto<AuthorElementDto> dto = new CommonPageableDto<>();
        dto.setTotal(authorEntityPage.getTotalElements());
        dto.setData(convertAuthorsToDto(authorEntityPage));
        dto.setPerPage(authorEntityPage.getSize());
        dto.setPage(authorEntityPage.getNumber());
        return dto;
    }

    @Override
    public AuthorEditDto getAuthorEditDtoBySlug(String slug) {
        var entity = authorRepository.findAuthorEntityBySlug(slug).orElseThrow(NotFoundException::new);
        var dto = new AuthorEditDto();
        dto.setSlug(slug);
        dto.setDescription(entity.getDescription());
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        return dto;
    }

    @Override
    @Transactional
    public void updateAuthorEntity(AuthorEditDto editDto) throws IOException {
        var authorEntity = authorRepository.findById(editDto.getId()).orElseThrow(NotFoundException::new);
        setAuthorData(authorEntity, editDto);
    }

    @Override
    @Transactional
    public void createAuthorEntity(AuthorEditDto editDto) throws IOException {
        setAuthorData(new AuthorEntity(), editDto);
    }

    @Override
    @Transactional
    public void deleteAuthorBySlug(String slug) {
        authorRepository.deleteAuthorEntityBySlug(slug);
    }

    public void setAuthorData(AuthorEntity authorEntity, AuthorEditDto authorEditDto) throws IOException {
        authorEntity.setName(authorEditDto.getName());
        authorEntity.setDescription(authorEditDto.getDescription());
        authorEntity.setSlug(authorEditDto.getSlug());
        if (authorEditDto.getPhoto() != null && authorEditDto.getPhoto().getResource().contentLength() > 0) {
            authorEntity.setPhoto(resourceStorageService.saveNewEntityImage(authorEditDto.getPhoto(), authorEditDto.getSlug()));
        }
        authorRepository.save(authorEntity);
    }
}
