package com.github.ivanfomkin.bookshop.service.impl;

import com.github.ivanfomkin.bookshop.aop.annotation.ExecutionTimeLog;
import com.github.ivanfomkin.bookshop.dto.review.BookSlugInfoDto;
import com.github.ivanfomkin.bookshop.dto.user.UserBookShelfDto;
import com.github.ivanfomkin.bookshop.entity.book.BookEntity;
import com.github.ivanfomkin.bookshop.entity.book.links.Book2UserEntity;
import com.github.ivanfomkin.bookshop.entity.enums.Book2UserType;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;
import com.github.ivanfomkin.bookshop.exception.NotFoundException;
import com.github.ivanfomkin.bookshop.repository.Book2UserRepository;
import com.github.ivanfomkin.bookshop.repository.Book2UserTypeRepository;
import com.github.ivanfomkin.bookshop.repository.UserRepository;
import com.github.ivanfomkin.bookshop.service.Book2UserService;
import com.github.ivanfomkin.bookshop.service.BookService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class Book2UserServiceImpl implements Book2UserService {
    private final BookService bookService;
    private final UserRepository userRepository;
    private final Book2UserRepository book2UserRepository;
    private final Book2UserTypeRepository book2UserTypeRepository;

    public Book2UserServiceImpl(Book2UserRepository book2UserRepository, BookService bookService, UserRepository userRepository, Book2UserTypeRepository book2UserTypeRepository) {
        this.bookService = bookService;
        this.book2UserRepository = book2UserRepository;
        this.userRepository = userRepository;
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
            bookStatus.setType(book2UserTypeRepository.findBook2UserTypeEntityByName(status));
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
            bookStatus.setType(book2UserTypeRepository.findBook2UserTypeEntityByName(status));
            book2UserRepository.save(bookStatus);
        } else {
            changeBookStatus(bookStatus, status);
        }
    }

    private void changeBookStatus(Book2UserEntity bookStatus, Book2UserType requestedStatus) {
        var currentStatus = book2UserTypeRepository.findById(bookStatus.getType().getId()).orElseThrow();
        var newBookStatus = book2UserTypeRepository.findBook2UserTypeEntityByName(requestedStatus);
        if (currentStatus.getName() != newBookStatus.getName()) {
            bookStatus.setType(newBookStatus);
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

    @Override
    public List<UserBookShelfDto> getUserBookShelf(Integer userId) {
        var book2UserList = book2UserRepository.findAllByUser_IdOrderByTimeDesc(userId);
        return book2UserList.stream().map(b2u -> {
            var dto = new UserBookShelfDto();
            dto.setBook2UserId(b2u.getId());
            dto.setStatus(b2u.getType().getName().name());
            dto.setStatusDate(b2u.getTime());
            dto.setBook(new BookSlugInfoDto(b2u.getBook().getSlug(), b2u.getBook().getTitle()));
            return dto;
        }).toList();
    }

    @Override
    @Transactional
    public Integer deleteByBook2UserId(Integer bookToUserId) {
        var book2UserEntity = book2UserRepository.findById(bookToUserId).orElseThrow(NotFoundException::new);
        var userId = book2UserEntity.getUser().getId();
        book2UserRepository.deleteById(bookToUserId);
        return userId;
    }

    @Override
    @Transactional
    public void changeBookStatus(Integer userId, String[] bookSlugs, Book2UserType book2UserType) {
        var book2Users = book2UserRepository.findAllByUser_IdOrderByTimeDesc(userId);
        List<Book2UserEntity> entitiesForSave = new ArrayList<>();
        for (String slug : bookSlugs) {
            var book2UserEntity = book2Users.stream().filter(b2u -> b2u.getBook().getSlug().equals(slug)).findFirst().orElse(new Book2UserEntity());
            book2UserEntity.setBook(bookService.getBookEntityBySlug(slug));
            book2UserEntity.setUser(userRepository.findById(userId).orElseThrow(NotFoundException::new));
            book2UserEntity.setType(book2UserTypeRepository.findBook2UserTypeEntityByName(book2UserType));
            entitiesForSave.add(book2UserEntity);
        }
        book2UserRepository.saveAll(entitiesForSave);
    }
}
