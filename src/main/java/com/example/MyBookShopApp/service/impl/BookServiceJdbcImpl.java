package com.example.MyBookShopApp.service.impl;

import com.example.MyBookShopApp.model.Author;
import com.example.MyBookShopApp.model.Book;
import com.example.MyBookShopApp.service.BookService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookServiceJdbcImpl implements BookService {

    private final JdbcTemplate jdbcTemplate;

    public BookServiceJdbcImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Book> getBooksData() {
        List<Book> books = jdbcTemplate.query("SELECT b.id, b.title, b.price_old, b.price, a.last_name, a.first_name AS author " +
                "FROM books b JOIN authors a ON a.id = b.author_id", (rs, rowNum) -> {
            Book book = new Book();
            book.setId(rs.getLong("id"));
            book.setAuthor(new Author(rs.getString("last_name"), rs.getString("first_name")));
            book.setTitle(rs.getString("title"));
            book.setPriceOld(rs.getFloat("price_old"));
            book.setPrice(rs.getFloat("price"));
            return book;
        });
        return new ArrayList<>(books);
    }
}
