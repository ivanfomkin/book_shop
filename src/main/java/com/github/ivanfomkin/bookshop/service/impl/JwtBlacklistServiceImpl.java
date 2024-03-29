package com.github.ivanfomkin.bookshop.service.impl;

import com.github.ivanfomkin.bookshop.entity.other.JwtBlackListEntity;
import com.github.ivanfomkin.bookshop.repository.JwtBlackListRepository;
import com.github.ivanfomkin.bookshop.security.jwt.JWTUtil;
import com.github.ivanfomkin.bookshop.service.JwtBlacklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtBlacklistServiceImpl implements JwtBlacklistService {
    private final JWTUtil jwtUtil;
    private final JwtBlackListRepository jwtBlackListRepository;

    @Override
    public boolean existInBlacklist(String token) {
        return jwtBlackListRepository.existsByToken(token);
    }

    @Override
    public void storeInBlacklist(String token) {
        LocalDateTime expirationDate = jwtUtil.extractExpiration(token);
        if (expirationDate.isAfter(LocalDateTime.now())) {
            JwtBlackListEntity jwtBlackListEntity = new JwtBlackListEntity(token, expirationDate);
            jwtBlackListRepository.save(jwtBlackListEntity);
        }
    }

    @Scheduled(cron = "${bookshop.auth.blacklist.delete.cron}")
    @Override
    public void deleteExpiredTokens() {
        int tokenCount = jwtBlackListRepository.deleteExpiredTokens();
        log.info("Удаление неактуальных jwt-токенов прошло успешно. Удалено {} токенов", tokenCount);
    }
}
