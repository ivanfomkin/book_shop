package com.example.MyBookShopApp.service.impl;

import com.example.MyBookShopApp.model.Book;
import com.example.MyBookShopApp.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookServiceJdbcImpl implements BookService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BookServiceJdbcImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Book> getBooksData() {

        List<Book> books = jdbcTemplate.query("SELECT * FROM books;", (ResultSet rs, int rowNum) -> {
            Book book = new Book();
            book.setId(rs.getInt("id"));
            book.setAuthor(rs.getString("author"));
            book.setTitle(rs.getString("title"));
            book.setPriceOld(rs.getFloat("priceold"));
            book.setPrice(rs.getFloat("price"));
            return book;
        });
        return new ArrayList<>(books);
    }
}
