package com.example.MyBookShopApp.service.impl;

import com.example.MyBookShopApp.model.Book;
import com.example.MyBookShopApp.repository.BookRepository;
import com.example.MyBookShopApp.service.BookService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
public class BookServiceJpaImpl implements BookService {
    private final BookRepository bookRepository;

    public BookServiceJpaImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public List<Book> getBooksData() {
        return bookRepository.findAll();
    }
}
