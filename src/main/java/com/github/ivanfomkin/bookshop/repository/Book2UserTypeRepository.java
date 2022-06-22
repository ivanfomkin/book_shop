package com.github.ivanfomkin.bookshop.repository;

import com.github.ivanfomkin.bookshop.entity.book.links.Book2UserTypeEntity;
import com.github.ivanfomkin.bookshop.entity.enums.Book2UserType;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Book2UserTypeRepository extends JpaRepository<Book2UserTypeEntity, Integer> {

    @Cacheable(cacheNames = "book2UserTypes", key = "#name", unless = "#result == null")
    Book2UserTypeEntity findBook2UserTypeEntityByName(Book2UserType name);
}
