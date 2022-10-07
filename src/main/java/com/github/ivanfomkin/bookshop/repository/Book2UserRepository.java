package com.github.ivanfomkin.bookshop.repository;

import com.github.ivanfomkin.bookshop.entity.book.BookEntity;
import com.github.ivanfomkin.bookshop.entity.book.links.Book2UserEntity;
import com.github.ivanfomkin.bookshop.entity.enums.Book2UserType;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface Book2UserRepository extends JpaRepository<Book2UserEntity, Integer> {
    @Modifying
    @Transactional
    @Query("DELETE FROM Book2UserEntity b2u WHERE b2u.id IN (SELECT b2u.id FROM Book2UserEntity b2ue JOIN b2ue.user u JOIN b2ue.book b WHERE u = :user AND b.slug = :slug)")
    void deleteBookStatusBySlugAndUser(UserEntity user, String slug);

    @Modifying
    @Transactional
    @Query("DELETE FROM Book2UserEntity b2u WHERE b2u.id IN (SELECT b2u.id FROM Book2UserEntity b2ue JOIN b2ue.user u JOIN b2ue.book b WHERE u = :user AND b = :book)")
    void deleteBookStatusByBookAndUser(UserEntity user, BookEntity book);

    @Query("SELECT count(b2u) FROM Book2UserEntity b2u JOIN b2u.user u JOIN b2u.type b2ut WHERE u = :user AND b2ut.name = :type")
    int countBooksByUserAndStatus(UserEntity user, Book2UserType type);

    @Query("SELECT count(b2u) FROM Book2UserEntity b2u JOIN b2u.user u JOIN b2u.type b2ut WHERE u = :user AND b2ut.name IN :types")
    int countBooksByUserAndStatusIn(UserEntity user, List<Book2UserType> types);

    @Query("SELECT b2u from Book2UserEntity b2u JOIN b2u.user u JOIN b2u.book b where u = :user AND b.slug = :slug")
    Book2UserEntity findBookStatusByUserAndSlug(UserEntity user, String slug);

    @Query("SELECT b2ut.name FROM Book2UserTypeEntity b2ut JOIN Book2UserEntity b2u ON b2u.type = b2ut JOIN b2u.user u JOIN b2u.book b where u = :user AND b.slug = :slug")
    Book2UserType findBook2UserTypeByUserAndSlug(UserEntity user, String slug);

    @Query("SELECT b2u from Book2UserEntity b2u JOIN b2u.user u JOIN b2u.book b where u = :user AND b = :book")
    Book2UserEntity findBookStatusByUserAndBook(UserEntity user, BookEntity book);

    @Modifying
    @Transactional
    void deleteAllByBookSlug(String slug);

    List<Book2UserEntity> findAllByUserIdOrderByTimeDesc(Integer userId);
}
