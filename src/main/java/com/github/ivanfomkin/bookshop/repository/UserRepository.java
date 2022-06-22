package com.github.ivanfomkin.bookshop.repository;

import com.github.ivanfomkin.bookshop.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    UserEntity findUserEntityByContacts_contact(String contact);

    UserEntity findUserEntityByOauthId(String oAuth2Id);
}
