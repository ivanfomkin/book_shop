package com.example.MyBookShopApp.repository;

import com.example.MyBookShopApp.entity.user.UserContactEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserContactRepository extends JpaRepository<UserContactEntity, Integer> {
    Boolean existsAllByContactIn(List<String> contacts);
}
