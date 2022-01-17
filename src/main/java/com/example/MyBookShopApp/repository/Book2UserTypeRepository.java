package com.example.MyBookShopApp.repository;

import com.example.MyBookShopApp.entity.book.links.Book2UserTypeEntity;
import com.example.MyBookShopApp.entity.enums.Book2UserType;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Book2UserTypeRepository extends JpaRepository<Book2UserTypeEntity, Integer> {

    @Cacheable(cacheNames = "book2UserTypes", key = "#name", unless = "#result == null")
    Book2UserTypeEntity findBook2UserTypeEntityByName(Book2UserType name);
}
