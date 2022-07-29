package com.github.ivanfomkin.bookshop.repository;

import com.github.ivanfomkin.bookshop.entity.payments.BookOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookOrderRepository extends JpaRepository<BookOrderEntity, Long> {
}
