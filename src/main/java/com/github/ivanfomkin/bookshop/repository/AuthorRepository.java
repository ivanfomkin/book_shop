package com.github.ivanfomkin.bookshop.repository;

import com.github.ivanfomkin.bookshop.entity.author.AuthorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<AuthorEntity, Integer> {
    AuthorEntity findAuthorEntityBySlug(String slug);
}
