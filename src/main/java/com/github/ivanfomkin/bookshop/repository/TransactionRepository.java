package com.github.ivanfomkin.bookshop.repository;

import com.github.ivanfomkin.bookshop.entity.payments.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

}
