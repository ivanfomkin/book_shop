package com.github.ivanfomkin.bookshop.repository;

import com.github.ivanfomkin.bookshop.entity.other.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailMessageRepository extends JpaRepository<MessageEntity, Integer> {
}
