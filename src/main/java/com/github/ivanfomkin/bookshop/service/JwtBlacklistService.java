package com.github.ivanfomkin.bookshop.service;

public interface JwtBlacklistService {
    /**
     * Метод проверяет, содержится ли указанный токен в черном списке
     *
     * @param token проверяемый token
     * @return true - токен в черном списке, false - токена в черном списке нет
     */
    boolean existInBlacklist(String token);

    void storeInBlacklist(String token);

    void deleteExpiredTokens();
}
