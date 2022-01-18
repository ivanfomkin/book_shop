package com.example.MyBookShopApp.repository;

import com.example.MyBookShopApp.entity.book.links.Book2UserEntity;
import com.example.MyBookShopApp.entity.enums.Book2UserType;
import com.example.MyBookShopApp.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface Book2UserRepository extends JpaRepository<Book2UserEntity, Integer> {
    @Modifying
    @Transactional
    @Query("DELETE FROM Book2UserEntity b2u WHERE b2u.id IN (SELECT b2u.id FROM Book2UserEntity JOIN UserEntity u ON u.id = b2u.userId JOIN BookEntity b ON b.id = b2u.bookId WHERE u = :user AND b.slug = :slug)")
    void deleteBookFromCart(UserEntity user, String slug);

    Boolean existsBook2UserEntityByBookIdAndAndUserId(int bookId, int userId);

    @Query("SELECT count(b2u) FROM Book2UserEntity b2u JOIN UserEntity u ON u.id = b2u.userId JOIN Book2UserTypeEntity b2ut ON b2ut.id = b2u.typeId WHERE u = :user AND b2ut.name = :type")
    int countBooksByUserAndStatus(UserEntity user, Book2UserType type);
}
