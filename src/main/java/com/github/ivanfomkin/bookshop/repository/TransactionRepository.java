package com.github.ivanfomkin.bookshop.repository;

import com.github.ivanfomkin.bookshop.entity.payments.TransactionEntity;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    @Query("SELECT t FROM TransactionEntity t LEFT JOIN t.bookOrders b left join b.book WHERE t.user = :user AND t.status = true GROUP BY t ORDER BY t.transactionDate DESC")
    List<TransactionEntity> findAllByUser(UserEntity user);
}
