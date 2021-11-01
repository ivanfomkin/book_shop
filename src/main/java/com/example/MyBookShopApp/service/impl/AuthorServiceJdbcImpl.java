package com.example.MyBookShopApp.service.impl;

import com.example.MyBookShopApp.model.Author;
import com.example.MyBookShopApp.service.AuthorService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.util.List;

@Service
public class AuthorServiceJdbcImpl implements AuthorService {
    private final JdbcTemplate jdbcTemplate;

    public AuthorServiceJdbcImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Cacheable("authors")
    public List<Author> findAll() {
        return jdbcTemplate.query("SELECT name FROM authors ORDER BY name", (ResultSet rs, int rowNumber) -> {
            Author author = new Author();
            author.setName(rs.getString("name"));
            return author;
        });
    }

}
