package com.github.ivanfomkin.bookshop.dto.user;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserInfoElementDto {
    private int id;
    private String email;
    private String phone;
    private String name;
    private double balance;
    private boolean isOauthUser;
    private LocalDateTime regDate;
}
