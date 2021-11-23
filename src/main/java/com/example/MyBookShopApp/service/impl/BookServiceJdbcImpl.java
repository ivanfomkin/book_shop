package com.example.MyBookShopApp.service.impl;

import com.example.MyBookShopApp.model.Book;
import com.example.MyBookShopApp.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookServiceJdbcImpl implements BookService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BookServiceJdbcImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Cacheable("books")
    public List<Book> getBooksData() {
        List<Book> books = jdbcTemplate.query("SELECT b.id, b.title, b.price_old, b.price, CONCAT(a.last_name, ' ', a.first_name) AS author " +
                "FROM books b JOIN authors a ON a.id = b.author_id", (rs, rowNum) -> {
            Book book = new Book();
            book.setId(rs.getInt("id"));
            book.setAuthor(rs.getString("author"));
            book.setTitle(rs.getString("title"));
            book.setPriceOld(rs.getFloat("price_old"));
            book.setPrice(rs.getFloat("price"));
            return book;
        });
        return new ArrayList<>(books);
    }
}
