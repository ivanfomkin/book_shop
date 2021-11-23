package com.example.MyBookShopApp.service.impl;

import com.example.MyBookShopApp.model.Author;
import com.example.MyBookShopApp.service.AuthorService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuthorServiceJdbcImpl implements AuthorService {
    private final JdbcTemplate jdbcTemplate;

    public AuthorServiceJdbcImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Cacheable("authors")
    public Map<String, List<Author>> getAuthorsMap() {
        var allAuthors = jdbcTemplate.query("SELECT * FROM authors", (rs, rowNumber) -> {
            Author author = new Author();
            author.setId(rs.getInt("id"));
            author.setFirstName(rs.getString("first_name"));
            author.setLastName(rs.getString("last_name"));
            return author;
        });
        return allAuthors.stream().collect(Collectors.groupingBy(a -> a.getLastName().substring(0, 1)));
    }

}
