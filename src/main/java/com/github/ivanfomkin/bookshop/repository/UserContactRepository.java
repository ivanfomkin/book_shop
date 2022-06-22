package com.github.ivanfomkin.bookshop.repository;

import com.github.ivanfomkin.bookshop.entity.user.UserContactEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserContactRepository extends JpaRepository<UserContactEntity, Integer> {
    boolean existsAllByContactIn(List<String> contacts);
}
