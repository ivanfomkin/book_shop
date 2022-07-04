package com.github.ivanfomkin.bookshop.service.impl;

import com.github.ivanfomkin.bookshop.aop.annotation.ExecutionTimeLog;
import com.github.ivanfomkin.bookshop.dto.book.BookFileDto;
import com.github.ivanfomkin.bookshop.dto.book.BookListDto;
import com.github.ivanfomkin.bookshop.dto.book.BookListElement;
import com.github.ivanfomkin.bookshop.dto.book.BookSlugDto;
import com.github.ivanfomkin.bookshop.entity.author.AuthorEntity;
import com.github.ivanfomkin.bookshop.entity.book.BookEntity;
import com.github.ivanfomkin.bookshop.entity.enums.Book2UserType;
import com.github.ivanfomkin.bookshop.entity.genre.GenreEntity;
import com.github.ivanfomkin.bookshop.entity.tag.TagEntity;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;
import com.github.ivanfomkin.bookshop.repository.Book2UserRepository;
import com.github.ivanfomkin.bookshop.repository.BookRepository;
import com.github.ivanfomkin.bookshop.service.*;
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
    private final UserService userService;
    private final CookieService cookieService;
    private final BookRepository bookRepository;
    private final AuthorService authorService;
    private final DateTimeFormatter dateTimeFormatter;
    private final BookVoteService bookVoteService;
    private final BookReviewService bookReviewService;
    private final Book2UserRepository book2UserRepository;

    private static final String MANY_AUTHORS_APPENDER = " и другие"; // TODO: 23.06.2022 Использовать локализованное сообщение
    private final LocalDate minLocalDate;
    private final LocalDate maxLocalDate;

    public BookServiceJpaImpl(UserService userService, CookieService cookieService, BookRepository bookRepository, AuthorService authorService, BookVoteService bookVoteService, BookReviewService bookReviewService, Book2UserRepository book2UserRepository) {
        this.userService = userService;
        this.cookieService = cookieService;
        this.bookRepository = bookRepository;
        this.authorService = authorService;
        this.bookVoteService = bookVoteService;
        this.bookReviewService = bookReviewService;
        this.book2UserRepository = book2UserRepository;
        dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        minLocalDate = LocalDate.of(1000, 1, 1);
        maxLocalDate = LocalDate.of(3000, 12, 31);
    }

    @Override
    public BookListDto getPageableRecommendedBooks(int offset, int limit, String cartCookie, String keptCookie) {
        Page<BookEntity> bookEntityPage;
        Pageable pageable = PageRequest.of(offset, limit);
        var currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            var userCartAndKeptBookSlugs = cookieService.getBookSlugListFromCookie(cartCookie);
            userCartAndKeptBookSlugs.add(keptCookie);
            bookEntityPage = bookRepository.findRecommendedBooksWhereSlugsNotIn(pageable, userCartAndKeptBookSlugs);
        } else {
            bookEntityPage = bookRepository.findRecommendedBooksForUser(pageable, currentUser);
        }
        var bookListDto = createBookListDtoFromPage(bookEntityPage);
        return applyStatusesToBookListDto(bookListDto, cartCookie, keptCookie, currentUser);
    }

    private BookListDto applyStatusesToBookListDto(BookListDto bookListDto, String cartCookie, String keptCookie, UserEntity currentUser) {
        if (currentUser == null) {
            return addStatusesToAllBooks(bookListDto, cartCookie, keptCookie);
        } else {
            return addStatusesToAllBooks(bookListDto, currentUser);
        }
    }

    private BookListDto addStatusesToAllBooks(BookListDto dto, UserEntity user) {
        var books = dto.getBooks();
        for (BookListElement book : books) {
            var status = book2UserRepository.findBook2UserTypeByUserAndSlug(user, book.getSlug());
            if (status != null) {
                book.setStatus(status.toString());
            }
        }
        return dto;
    }

    private BookListDto addStatusesToAllBooks(BookListDto dto, String cartCookie, String keptCookie) {
        var books = dto.getBooks();
        var booksInCart = cookieService.getBookSlugListFromCookie(cartCookie);
        var postponedBooks = cookieService.getBookSlugListFromCookie(keptCookie);
        for (BookListElement book : books) {
            if (booksInCart.contains(book.getSlug())) {
                book.setStatus(Book2UserType.CART.toString());
            } else if (postponedBooks.contains(book.getSlug())) {
                book.setStatus(Book2UserType.KEPT.toString());
            }
        }
        return dto;
    }

    @ExecutionTimeLog(withUserInfo = true)
    @Override
    public BookListDto getPageableRecentBooks(String cartCookie, String keptCookie, int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit);
        Page<BookEntity> bookEntityPage = bookRepository.findRecentBooks(pageable);
        return applyStatusesToBookListDto(createBookListDtoFromPage(bookEntityPage), cartCookie, keptCookie, userService.getCurrentUser());
    }

    @Override
    public BookListDto getPageablePopularBooks(int offset, int limit, String cartCookie, String keptCookie) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<BookEntity> bookEntityPage = bookRepository.findPopularBooks(pageable);
        return applyStatusesToBookListDto(createBookListDtoFromPage(bookEntityPage), cartCookie, keptCookie, userService.getCurrentUser());
    }

    @Override
    public BookListDto getPageableRecentBooks(int offset, int limit, String fromDate, String toDate, String cartCookie, String keptCookie) {
        Pageable pageable = PageRequest.of(offset, limit);
        LocalDate from = parseFromDate(fromDate);
        LocalDate to = from.equals(LocalDate.MIN) ? parseToDate(toDate) : LocalDate.now();
        Page<BookEntity> bookEntityPage = bookRepository.findBookEntitiesByPublishDateBetweenOrderByPublishDateDesc(from, to, pageable);
        return applyStatusesToBookListDto(createBookListDtoFromPage(bookEntityPage), cartCookie, keptCookie, userService.getCurrentUser());
    }

    @Override
    public BookListDto getPageableBooksByTag(int offset, int limit, String tag, String cartCookie, String keptCookie) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<BookEntity> bookEntityPage = bookRepository.findBookEntityByTagName(tag, pageable);
        return applyStatusesToBookListDto(createBookListDtoFromPage(bookEntityPage), cartCookie, keptCookie, userService.getCurrentUser());
    }

    @Override
    public BookListDto getPageableBooksByTitle(int offset, int limit, String title, String cartCookie, String keptCookie) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<BookEntity> bookEntityPage = bookRepository.findBookEntityByTitleContainingIgnoreCaseOrderByTitle(title, pageable);
        return applyStatusesToBookListDto(createBookListDtoFromPage(bookEntityPage), cartCookie, keptCookie, userService.getCurrentUser());
    }

    @Override
    public BookListDto getPageableBooksByGenre(int offset, int limit, GenreEntity genre, String cartCookie, String keptCookie) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<BookEntity> bookEntityPage = bookRepository.findBookEntityByGenresContaining(genre, pageable);
        return applyStatusesToBookListDto(createBookListDtoFromPage(bookEntityPage), cartCookie, keptCookie, userService.getCurrentUser());
    }

    @Override
    public BookListDto getPageableBooksByAuthor(int offset, int limit, AuthorEntity author, String cartCookie, String keptCookie) {
        Pageable pageable = PageRequest.of(offset, limit, Sort.Direction.DESC, "isBestseller", "discount");
        Page<BookEntity> bookEntityPage = bookRepository.findBookEntityByAuthorsContaining(author, pageable);
        return applyStatusesToBookListDto(createBookListDtoFromPage(bookEntityPage), cartCookie, keptCookie, userService.getCurrentUser());
    }

    @Override
    public BookListDto getPageableBooksByAuthorSlug(int offset, int limit, String authorSlug, String cartCookie, String keptCookie) {
        Pageable pageable = PageRequest.of(offset, limit, Sort.Direction.DESC, "isBestseller", "discount");
        Page<BookEntity> bookEntityPage = bookRepository.findBookEntityByAuthorsSlug(authorSlug, pageable);
        return applyStatusesToBookListDto(createBookListDtoFromPage(bookEntityPage), cartCookie, keptCookie, userService.getCurrentUser());
    }

    @Override
    public BookSlugDto getBookSlugDtoBySlug(UserEntity currentUser, String slug, String cartCookie, String keptCookie) {
        BookSlugDto bookSlugDto = convertSingleBookEntityToBookSlugDto(bookRepository.findBookEntityBySlug(slug));
        if (currentUser != null) {
            Book2UserType book2userType = book2UserRepository.findBook2UserTypeByUserAndSlug(currentUser, slug);
            if (book2userType != null) {
                bookSlugDto.setStatus(book2userType.toString());
            }
        } else {
            if (cartCookie != null && cartCookie.contains(slug)) {
                bookSlugDto.setStatus(Book2UserType.CART.toString());
            }
            if (keptCookie != null && keptCookie.contains(slug)) {
                bookSlugDto.setStatus(Book2UserType.KEPT.toString());
            }
        }
        return bookSlugDto;
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

    @Override
    public BookEntity getBookEntityBySlug(String slug) {
        return bookRepository.findBookEntityBySlug(slug);
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
        if (bookEntities == null || bookEntities.isEmpty()) {
            return new ArrayList<>();
        } else {
            return bookEntities.stream().map(this::convertSingleBookEntityToBookListElementDto).toList();
        }
    }

    private BookSlugDto convertSingleBookEntityToBookSlugDto(BookEntity bookEntity) {
        var dto = new BookSlugDto();
        dto.setAuthors(authorService.convertAuthorsToDto(bookEntity.getAuthors()));
        dto.setDescription(bookEntity.getDescription());
        dto.setTitle(bookEntity.getTitle());
        dto.setPrice(bookEntity.getPrice());
        dto.setReviews(bookReviewService.getReviewDtoForBook(bookEntity));
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
            dto.setAuthors(bookEntity.getAuthors().get(0).getName() + MANY_AUTHORS_APPENDER);
        } else {
            dto.setAuthors(bookEntity.getAuthors().get(0).getName());
        }
        dto.setTitle(bookEntity.getTitle());
        dto.setDiscount(bookEntity.getDiscount());
        dto.setBestseller(bookEntity.getIsBestseller());
        dto.setRating(bookVoteService.getBookRating(bookEntity));
        dto.setStatus("false");
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

    @Override
    public List<BookEntity> getBooksBySlugIn(List<String> slugs) {
        return bookRepository.findBookEntitiesBySlugIn(slugs);
    }
}
