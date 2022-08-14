package com.github.ivanfomkin.bookshop.entity.book.bookview;

import com.github.ivanfomkin.bookshop.entity.book.BookEntity;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;


@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class BookViewHistoryId implements Serializable {
    @ManyToOne
    private UserEntity user;
    @ManyToOne
    private BookEntity book;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BookViewHistoryId that = (BookViewHistoryId) o;

        if (!user.getId().equals(that.user.getId())) return false;
        return book.getId().equals(that.book.getId());
    }

    @Override
    public int hashCode() {
        int result = user.getId().hashCode();
        result = 31 * result + book.getId().hashCode();
        return result;
    }
}
