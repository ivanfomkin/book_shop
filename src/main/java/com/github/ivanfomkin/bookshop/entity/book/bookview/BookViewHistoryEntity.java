package com.github.ivanfomkin.bookshop.entity.book.bookview;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "book_view_history")
@AllArgsConstructor
@NoArgsConstructor
public class BookViewHistoryEntity implements Serializable {

    @EmbeddedId
    private BookViewHistoryId bookViewHistoryId;
    @UpdateTimestamp
    private LocalDateTime lastViewDate;
}
