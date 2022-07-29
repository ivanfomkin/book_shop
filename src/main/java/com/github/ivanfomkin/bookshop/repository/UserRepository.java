package com.github.ivanfomkin.bookshop.repository;

import com.github.ivanfomkin.bookshop.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    UserEntity findUserEntityByContacts_contact(String contact);

    UserEntity findUserEntityByOauthId(String oAuth2Id);

    boolean existsByContacts_contactIn(List<String> contacts);

    int countAllByContacts_contactIn(List<String> contact);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.balance = (u.balance - :amount) WHERE u = :user")
    void debtFromUserBalance(UserEntity user, Double amount);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.balance = (u.balance + :amount) WHERE u = :user")
    void addBalanceToUser(UserEntity user, Double amount);
}