package com.github.ivanfomkin.bookshop.repository;

import com.github.ivanfomkin.bookshop.entity.other.JwtBlackListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface JwtBlackListRepository extends JpaRepository<JwtBlackListEntity, Integer> {
    Boolean existsByToken(String token);

    @Modifying
    @Transactional
    @Query("delete from JwtBlackListEntity where expirationDate < current_date")
    int deleteExpiredTokens();
}
