package com.github.ivanfomkin.bookshop.entity.user;

import com.github.ivanfomkin.bookshop.entity.book.BookEntity;
import com.github.ivanfomkin.bookshop.entity.book.review.BookVoteEntity;
import com.github.ivanfomkin.bookshop.entity.enums.ContactType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

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
    private Double balance = 0.;

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

    @OneToMany(mappedBy = "user")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<UserRoleEntity> roles;

    public UserContactEntity getPhone() {
        return contacts.stream().filter(c -> c.getType() == ContactType.PHONE).findFirst().orElse(null);
    }

    public UserContactEntity getEmail() {
        return contacts.stream().filter(c -> c.getType() == ContactType.EMAIL).findFirst().orElse(null);
    }
}
