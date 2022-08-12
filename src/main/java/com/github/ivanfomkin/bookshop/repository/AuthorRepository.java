package com.github.ivanfomkin.bookshop.repository;

import com.github.ivanfomkin.bookshop.entity.author.AuthorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<AuthorEntity, Integer> {
    Optional<AuthorEntity> findAuthorEntityBySlug(String slug);
}
