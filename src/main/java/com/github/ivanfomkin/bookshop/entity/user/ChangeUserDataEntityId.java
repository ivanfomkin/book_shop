package com.github.ivanfomkin.bookshop.entity.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.io.Serializable;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ChangeUserDataEntityId implements Serializable {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChangeUserDataEntityId that = (ChangeUserDataEntityId) o;

        return userEntity.getId().equals(that.userEntity.getId());
    }

    @Override
    public int hashCode() {
        return userEntity.getId().hashCode();
    }
}
