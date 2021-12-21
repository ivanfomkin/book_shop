package com.example.MyBookShopApp.service.impl;

import com.example.MyBookShopApp.dto.book.BookDto;
import com.example.MyBookShopApp.dto.book.BookListDto;
import com.example.MyBookShopApp.entity.book.BookEntity;
import com.example.MyBookShopApp.repository.BookRepository;
import com.example.MyBookShopApp.service.BookService;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
public class BookServiceJpaImpl implements BookService {
    private final BookRepository bookRepository;

    private final String manyAuthorsAppender = " и другие";

    public BookServiceJpaImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public List<BookEntity> getBooksData() {
        return bookRepository.findAll();
    }

    @Override
    public BookListDto getPageableRecommendedBooks(Integer offset, Integer limit) {
        Pageable pageable = PageRequest.of(offset, limit);
        Page<BookEntity> booksFromDb = bookRepository.finRecommendedBooks(pageable);
        BookListDto dto = new BookListDto();
        dto.setCount(booksFromDb.getTotalElements());
        dto.setBooks(convertManyBookEntityToBookDto(booksFromDb.getContent()));
        return dto;
    }

    private List<BookDto> convertManyBookEntityToBookDto(List<BookEntity> bookEntities) {
        return bookEntities.stream().map(this::convertSingleBookEntityToBookDto).toList();
    }

    private BookDto convertSingleBookEntityToBookDto(BookEntity bookEntity) {
        BookDto dto = new BookDto();
        dto.setId(bookEntity.getId());
        dto.setSlug(bookEntity.getSlug());
        dto.setImage(bookEntity.getImage());
        if (bookEntity.getAuthor().size() > 1) {
            dto.setAuthors(bookEntity.getAuthor().get(0).getName() + manyAuthorsAppender);
        } else {
            dto.setAuthors(bookEntity.getAuthor().get(0).getName());
        }
        dto.setTitle(bookEntity.getTitle());
        dto.setDiscount(bookEntity.getDiscount());
        dto.setBestseller(bookEntity.getIsBestseller());
        dto.setRating("false"); //ToDo (ivan.fomkin) 21.12.21: Пока тут заглушка. Реализовать корректное заполнение этого поля
        dto.setStatus("false"); //ToDo (ivan.fomkin) 21.12.21: Пока тут заглушка. Реализовать корректное заполнение этого поля
        dto.setPrice(bookEntity.getPrice());
        dto.setDiscountPrice(calculateDiscountPrice(bookEntity.getPrice(), bookEntity.getDiscount()));
        return dto;
    }

    private Integer calculateDiscountPrice(int price, Short percentDiscount) {
        if (percentDiscount == null || percentDiscount == 0) {
            return price;
        }
        var discountAmount = price * percentDiscount / 100;
        return price - discountAmount;
    }
}
