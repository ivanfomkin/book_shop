package com.github.ivanfomkin.bookshop.service.impl;

import com.github.ivanfomkin.bookshop.aop.annotation.ExecutionTimeLog;
import com.github.ivanfomkin.bookshop.entity.book.BookEntity;
import com.github.ivanfomkin.bookshop.entity.book.links.Book2UserEntity;
import com.github.ivanfomkin.bookshop.entity.enums.Book2UserType;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;
import com.github.ivanfomkin.bookshop.repository.Book2UserRepository;
import com.github.ivanfomkin.bookshop.repository.Book2UserTypeRepository;
import com.github.ivanfomkin.bookshop.service.Book2UserService;
import com.github.ivanfomkin.bookshop.service.BookService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Book2UserServiceImpl implements Book2UserService {
    private final BookService bookService;
    private final Book2UserRepository book2UserRepository;
    private final Book2UserTypeRepository book2UserTypeRepository;

    public Book2UserServiceImpl(Book2UserRepository book2UserRepository, BookService bookService, Book2UserTypeRepository book2UserTypeRepository) {
        this.bookService = bookService;
        this.book2UserRepository = book2UserRepository;
        this.book2UserTypeRepository = book2UserTypeRepository;
    }

    @Override
    @ExecutionTimeLog(withUserInfo = true)
    public void changeBookStatus(UserEntity user, String slug, Book2UserType status) {
        if (status == Book2UserType.UNLINK) {
            book2UserRepository.deleteBookStatusBySlugAndUser(user, slug);
            return;
        }
        var bookStatus = book2UserRepository.findBookStatusByUserAndSlug(user, slug);
        if (bookStatus == null) {
            bookStatus = new Book2UserEntity();
            bookStatus.setUser(user);
            bookStatus.setBook(bookService.getBookEntityBySlug(slug));
            bookStatus.setTypeId(book2UserTypeRepository.findBook2UserTypeEntityByName(status).getId());
            book2UserRepository.save(bookStatus);
        } else {
            changeBookStatus(bookStatus, status);
        }
    }

    @Override
    public void changeBookStatus(UserEntity user, BookEntity bookEntity, Book2UserType status) {
        if (status == Book2UserType.UNLINK) {
            book2UserRepository.deleteBookStatusByBookAndUser(user, bookEntity);
            return;
        }
        var bookStatus = book2UserRepository.findBookStatusByUserAndBook(user, bookEntity);
        if (bookStatus == null) {
            bookStatus = new Book2UserEntity();
            bookStatus.setUser(user);
            bookStatus.setBook(bookEntity);
            bookStatus.setTypeId(book2UserTypeRepository.findBook2UserTypeEntityByName(status).getId());
            book2UserRepository.save(bookStatus);
        } else {
            changeBookStatus(bookStatus, status);
        }
    }

    private void changeBookStatus(Book2UserEntity bookStatus, Book2UserType requestedStatus) {
        var currentStatus = book2UserTypeRepository.findById(bookStatus.getTypeId()).orElseThrow();
        var newBookStatus = book2UserTypeRepository.findBook2UserTypeEntityByName(requestedStatus);
        if (currentStatus.getName() != newBookStatus.getName()) {
            bookStatus.setTypeId(newBookStatus.getId());
            book2UserRepository.save(bookStatus);
        }
    }

    @Override
    public int getCartAmount(UserEntity user) {
        return book2UserRepository.countBooksByUserAndStatus(user, Book2UserType.CART);
    }

    @Override
    public int getKeptAmount(UserEntity user) {
        return book2UserRepository.countBooksByUserAndStatus(user, Book2UserType.KEPT);
    }

    @Override
    public int getMyBookAmount(UserEntity user) {
        return book2UserRepository.countBooksByUserAndStatusIn(user, List.of(Book2UserType.PAID, Book2UserType.ARCHIVED));
    }
}
