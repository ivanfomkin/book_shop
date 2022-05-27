package com.example.MyBookShopApp.entity.other;

import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "jwt_blacklist")
@NoArgsConstructor
public class JwtBlackListEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String token;
    @UpdateTimestamp
    private LocalDateTime logDate;
    private LocalDateTime expirationDate;

    public JwtBlackListEntity(String token, LocalDateTime expirationDate) {
        this.token = token;
        this.expirationDate = expirationDate;
    }
}
