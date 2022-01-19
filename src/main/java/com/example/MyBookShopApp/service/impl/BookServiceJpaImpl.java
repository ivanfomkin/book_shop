package com.example.MyBookShopApp.service.impl;

import com.example.MyBookShopApp.dto.book.BookFileDto;
import com.example.MyBookShopApp.dto.book.BookListDto;
import com.example.MyBookShopApp.dto.book.BookListElement;
import com.example.MyBookShopApp.dto.book.BookSlugDto;
import com.example.MyBookShopApp.entity.author.AuthorEntity;
import com.example.MyBookShopApp.entity.book.BookEntity;
import com.example.MyBookShopApp.entity.enums.Book2UserType;
import com.example.MyBookShopApp.entity.genre.GenreEntity;
import com.example.MyBookShopApp.entity.tag.TagEntity;
import com.example.MyBookShopApp.entity.user.UserEntity;
import com.example.MyBookShopApp.repository.BookRepository;
import com.example.MyBookShopApp.service.AuthorService;
import com.example.MyBookShopApp.service.BookService;
import com.example.MyBookShopApp.service.BookVoteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookServiceJpaImpl implements BookService {
    private final BookRepository bookRepository;
    private final AuthorService authorService;
    private final DateTimeFormatter dateTimeFormatter;
    private final BookVoteService bookVoteService;

    private final String manyAuthorsAppender = " и другие";
    private final LocalDate minLocalDate;
    private final LocalDate maxLocalDate;

    public BookServiceJpaImpl(BookRepository bookRepository, AuthorService authorService, BookVoteService bookVoteService) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
        this.bookVoteService = bookVoteService;
        dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        minLocalDate = LocalDate.of(1000, 1, 1);
        maxLocalDate = LocalDate.of(3000, 12, 31);
    }

    @Override
    public BookListDto getPageableRecommendedBooks(int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit);
        Page<BookEntity> bookEntityPage = bookRepository.finRecommendedBooks(pageable);
        return createBookListDtoFromPage(bookEntityPage);
    }

    @Override
    public BookListDto getPageableRecentBooks(int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit);
        Page<BookEntity> bookEntityPage = bookRepository.findRecentBooks(pageable);
        return createBookListDtoFromPage(bookEntityPage);
    }

    @Override
    public BookListDto getPageablePopularBooks(int offset, int limit) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<BookEntity> bookEntityPage = bookRepository.findPopularBooks(pageable);
        return createBookListDtoFromPage(bookEntityPage);
    }

    @Override
    public BookListDto getPageableRecentBooks(int offset, int limit, String fromDate, String toDate) {
        Pageable pageable = PageRequest.of(offset, limit);
        LocalDate from = parseFromDate(fromDate);
        LocalDate to = from.equals(LocalDate.MIN) ? parseToDate(toDate) : LocalDate.now();
        Page<BookEntity> bookEntityPage =
                bookRepository.findBookEntitiesByPublishDateBetweenOrderByPublishDateDesc(from, to, pageable);
        return createBookListDtoFromPage(bookEntityPage);
    }

    @Override
    public BookListDto getPageableBooksByTag(int offset, int limit, String tag) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<BookEntity> bookEntityPage = bookRepository.findBookEntityByTagName(tag, pageable);
        return createBookListDtoFromPage(bookEntityPage);
    }

    @Override
    public BookListDto getPageableBooksByTitle(int offset, int limit, String title) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<BookEntity> bookEntityPage = bookRepository.findBookEntityByTitleContainingOrderByTitle(title, pageable);
        return createBookListDtoFromPage(bookEntityPage);
    }

    @Override
    public BookListDto getPageableBooksByGenre(int offset, int limit, GenreEntity genre) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<BookEntity> bookEntityPage = bookRepository.findBookEntityByGenresContaining(genre, pageable);
        return createBookListDtoFromPage(bookEntityPage);
    }

    @Override
    public BookListDto getPageableBooksByAuthor(int offset, int limit, AuthorEntity author) {
        Pageable pageable = PageRequest.of(offset, limit, Sort.Direction.DESC, "isBestseller", "discount");
        Page<BookEntity> bookEntityPage = bookRepository.findBookEntityByAuthorsContaining(author, pageable);
        return createBookListDtoFromPage(bookEntityPage);
    }

    @Override
    public BookListDto getPageableBooksByAuthorSlug(int offset, int limit, String authorSlug) {
        Pageable pageable = PageRequest.of(offset, limit, Sort.Direction.DESC, "isBestseller", "discount");
        Page<BookEntity> bookEntityPage = bookRepository.findBookEntityByAuthorsSlug(authorSlug, pageable);
        return createBookListDtoFromPage(bookEntityPage);
    }

    @Override
    public BookSlugDto getBookBySlug(String slug) {
        return convertSingleBookEntityToBookSlugDto(bookRepository.findBookEntityBySlug(slug));
    }

    @Transactional
    @Override
    public void updateBookImageBySlug(String slug, String imagePath) {
        bookRepository.updateBookImageBySlug(slug, imagePath);
    }

    @Override
    public List<BookEntity> getBooksByUserAndType(UserEntity user, Book2UserType book2UserType) {
        return bookRepository.findBookEntitiesByUserAndType(user, book2UserType);
    }

    @Override
    public Integer getBookIdBuSlug(String slug) {
        return bookRepository.findBookIdBySlug(slug);
    }

    private LocalDate parseToDate(String stringDate) {
        return stringDate.equals("0") ? maxLocalDate : LocalDate.parse(stringDate, dateTimeFormatter);
    }

    private LocalDate parseFromDate(String stringDate) {
        return stringDate.equals("0") ? minLocalDate : LocalDate.parse(stringDate, dateTimeFormatter);
    }

    private BookListDto createBookListDtoFromPage(Page<BookEntity> bookEntityPage) {
        BookListDto dto = new BookListDto();
        dto.setCount(bookEntityPage.getTotalElements());
        dto.setBooks(convertManyBookEntityToBookDto(bookEntityPage.getContent()));
        return dto;
    }

    private List<BookListElement> convertManyBookEntityToBookDto(List<BookEntity> bookEntities) {
        if (bookEntities == null || bookEntities.size() == 0) {
            return new ArrayList<>();
        } else {
            return bookEntities.stream().map(this::convertSingleBookEntityToBookListElementDto).toList();
        }
    }

    private BookSlugDto convertSingleBookEntityToBookSlugDto(BookEntity bookEntity) {
        BookSlugDto dto = new BookSlugDto();
        dto.setAuthors(authorService.convertAuthorsToDto(bookEntity.getAuthors()));
        dto.setDescription(bookEntity.getDescription());
        dto.setTitle(bookEntity.getTitle());
        dto.setPrice(bookEntity.getPrice());
        dto.setDiscountPrice(calculateBookDiscountPrice(bookEntity.getPrice(), bookEntity.getDiscount()));
        dto.setRating(bookVoteService.getBookRatingDto(bookEntity));
        dto.setImage(bookEntity.getImage());
        dto.setSlug(bookEntity.getSlug());
        dto.setTags(bookEntity.getTags().stream().map(TagEntity::getName).toList());
        dto.setFiles(bookEntity.getFiles().stream().map(f -> new BookFileDto(f.getHash(), f.getBookFileExtensionString())).toList());
        return dto;
    }

    private BookListElement convertSingleBookEntityToBookListElementDto(BookEntity bookEntity) {
        BookListElement dto = new BookListElement();
        dto.setId(bookEntity.getId());
        dto.setSlug(bookEntity.getSlug());
        dto.setImage(bookEntity.getImage());
        if (bookEntity.getAuthors().size() > 1) {
            dto.setAuthors(bookEntity.getAuthors().get(0).getName() + manyAuthorsAppender);
        } else {
            dto.setAuthors(bookEntity.getAuthors().get(0).getName());
        }
        dto.setTitle(bookEntity.getTitle());
        dto.setDiscount(bookEntity.getDiscount());
        dto.setBestseller(bookEntity.getIsBestseller());
        dto.setRating(bookVoteService.getBookRating(bookEntity));
        dto.setStatus("false"); //ToDo (ivan.fomkin) 21.12.21: Пока тут заглушка. Реализовать корректное заполнение этого поля
        dto.setPrice(bookEntity.getPrice());
        dto.setDiscountPrice(calculateBookDiscountPrice(bookEntity.getPrice(), bookEntity.getDiscount()));
        return dto;
    }

    @Override
    public int calculateBookDiscountPrice(int price, Short percentDiscount) {
        if (percentDiscount == null || percentDiscount == 0) {
            return price;
        }
        var discountAmount = price * percentDiscount / 100;
        return price - discountAmount;
    }
}
