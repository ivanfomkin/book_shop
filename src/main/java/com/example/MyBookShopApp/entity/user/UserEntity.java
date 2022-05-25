package com.example.MyBookShopApp.entity.user;

import com.example.MyBookShopApp.entity.book.BookEntity;
import com.example.MyBookShopApp.entity.book.review.BookVoteEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "users")
public class UserEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String hash;

    @CreationTimestamp
    @Column(columnDefinition = "TIMESTAMP NOT NULL")
    private LocalDateTime regTime;

    @Column(columnDefinition = "INT NOT NULL")
    private Integer balance = 0;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String name;

    private String password;

    @Column(name = "oauth2_id")
    private String oauthId;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<UserContactEntity> contacts;

    @ManyToMany
    @JoinTable(name = "book2user",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id"))
    private List<BookEntity> userBooks;

    @OneToMany(mappedBy = "user")
    private List<BookVoteEntity> userVotes;
}
