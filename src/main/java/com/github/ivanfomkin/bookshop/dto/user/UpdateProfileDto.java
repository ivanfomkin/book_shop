package com.github.ivanfomkin.bookshop.dto.user;

import lombok.Data;

@Data
public class UpdateProfileDto {
    private String name;
    private String phone;
    private String mail;
    private String password;
    private String passwordReply;
}
