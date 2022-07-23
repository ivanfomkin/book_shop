package com.github.ivanfomkin.bookshop.entity.payments;

import com.github.ivanfomkin.bookshop.entity.enums.TransactionType;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "transactions")
public class TransactionEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double amount;
    @CreationTimestamp
    private LocalDateTime transactionDate;
    private LocalDateTime approveDate;
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    private Boolean status;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
