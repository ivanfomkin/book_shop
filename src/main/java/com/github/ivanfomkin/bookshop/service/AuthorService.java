package com.github.ivanfomkin.bookshop.service;

import com.github.ivanfomkin.bookshop.dto.author.AuthorEditDto;
import com.github.ivanfomkin.bookshop.dto.author.AuthorListDto;
import com.github.ivanfomkin.bookshop.dto.author.AuthorElementDto;
import com.github.ivanfomkin.bookshop.entity.author.AuthorEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface AuthorService {
    Map<String, List<AuthorEntity>> getAuthorsMap();

    AuthorEntity getAuthorBySlug(String slug);

    List<AuthorElementDto> convertAuthorsToDto(Iterable<AuthorEntity> authorEntities);

    List<AuthorElementDto> getAllAuthors();


    AuthorListDto getPageableAllAuthors(Pageable pageable, String searchQuery);

    AuthorEditDto getAuthorEditDtoBySlug(String slug);

    void updateAuthorEntity(AuthorEditDto editDto) throws IOException;

    void createAuthorEntity(AuthorEditDto editDto) throws IOException;

    void deleteAuthorBySlug(String slug);
}
