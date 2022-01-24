package com.example.MyBookShopApp.repository;

import com.example.MyBookShopApp.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    UserEntity findUserEntityByHash(String sessionId);
    UserEntity findUserEntityByContacts_contact(String contact);
}
