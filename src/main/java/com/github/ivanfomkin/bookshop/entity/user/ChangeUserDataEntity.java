package com.github.ivanfomkin.bookshop.entity.user;

import com.github.ivanfomkin.bookshop.dto.user.UpdateProfileDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "change_user_data")
@NoArgsConstructor
public class ChangeUserDataEntity {
    @Id
    private ChangeUserDataEntityId id;
    private String name;
    private String phone;
    private String mail;
    private String password;
    private String token;

    public ChangeUserDataEntity(UserEntity user, UpdateProfileDto updateProfileDto) {
        this.id = new ChangeUserDataEntityId(user);
        this.setData(updateProfileDto);
    }

    private boolean validateField(String field) {
        return field != null && !field.isBlank();
    }

    public void setData(UpdateProfileDto updateProfileDto) {
        if (validateField(updateProfileDto.getPassword())) {
            this.password = updateProfileDto.getPassword();
        } else {
            this.password = null;
        }
        if (validateField(updateProfileDto.getName())) {
            this.name = updateProfileDto.getName();
        } else {
            this.name = null;
        }
        if (validateField(updateProfileDto.getPhone())) {
            this.phone = updateProfileDto.getPhone();
        } else {
            this.phone = null;
        }
        if (validateField(updateProfileDto.getMail())) {
            this.mail = updateProfileDto.getMail();
        } else {
            this.mail = null;
        }
        this.token = UUID.randomUUID().toString();
    }
}
