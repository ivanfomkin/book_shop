package com.github.ivanfomkin.bookshop.repository;

import com.github.ivanfomkin.bookshop.entity.author.AuthorEntity;
import liquibase.pro.packaged.S;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<AuthorEntity, Integer> {
    Optional<AuthorEntity> findAuthorEntityBySlug(String slug);

    Page<AuthorEntity> findAuthorEntitiesByNameContainingIgnoreCase(Pageable pageable, String name);

    @Modifying
    @Transactional
    void deleteAuthorEntityBySlug(String slug);
}
