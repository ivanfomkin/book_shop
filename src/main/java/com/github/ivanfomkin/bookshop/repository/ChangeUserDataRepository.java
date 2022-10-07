package com.github.ivanfomkin.bookshop.repository;

import com.github.ivanfomkin.bookshop.entity.user.ChangeUserDataEntity;
import com.github.ivanfomkin.bookshop.entity.user.ChangeUserDataEntityId;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChangeUserDataRepository extends JpaRepository<ChangeUserDataEntity, ChangeUserDataEntityId> {
    Optional<ChangeUserDataEntity> findByToken(String token);

    ChangeUserDataEntity findByIdUserEntity(UserEntity user);
}
