package com.example.MyBookShopApp.service.impl;

import com.example.MyBookShopApp.entity.book.BookEntity;
import com.example.MyBookShopApp.entity.book.links.Book2UserEntity;
import com.example.MyBookShopApp.entity.enums.Book2UserType;
import com.example.MyBookShopApp.entity.user.UserEntity;
import com.example.MyBookShopApp.repository.Book2UserRepository;
import com.example.MyBookShopApp.repository.Book2UserTypeRepository;
import com.example.MyBookShopApp.service.Book2UserService;
import com.example.MyBookShopApp.service.BookService;
import org.springframework.stereotype.Service;

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
    public void changeBookStatus(UserEntity user, String slug, Book2UserType status) {
        if (status == Book2UserType.UNLINK) {
            book2UserRepository.deleteBookStatusBySlugAndUser(user, slug);
            return;
        }
        var bookStatus = book2UserRepository.findBookStatusByUserAndSlug(user, slug);
        if (bookStatus == null) {
            bookStatus = new Book2UserEntity();
            bookStatus.setUserId(user.getId());
            bookStatus.setBookId(bookService.getBookIdBuSlug(slug));
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
            bookStatus.setUserId(user.getId());
            bookStatus.setBookId(bookEntity.getId());
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
}
