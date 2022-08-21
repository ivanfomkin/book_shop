package com.github.ivanfomkin.bookshop.service.impl;

import com.github.ivanfomkin.bookshop.aop.annotation.ExecutionTimeLog;
import com.github.ivanfomkin.bookshop.dto.book.*;
import com.github.ivanfomkin.bookshop.entity.author.AuthorEntity;
import com.github.ivanfomkin.bookshop.entity.book.BookEntity;
import com.github.ivanfomkin.bookshop.entity.enums.Book2UserType;
import com.github.ivanfomkin.bookshop.entity.enums.BookFiletype;
import com.github.ivanfomkin.bookshop.entity.genre.GenreEntity;
import com.github.ivanfomkin.bookshop.entity.tag.TagEntity;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;
import com.github.ivanfomkin.bookshop.exception.NotFoundException;
import com.github.ivanfomkin.bookshop.repository.Book2UserRepository;
import com.github.ivanfomkin.bookshop.repository.BookRepository;
import com.github.ivanfomkin.bookshop.service.*;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class BookServiceJpaImpl implements BookService {
    private final UserService userService;
    private final CookieService cookieService;
    private final BookRepository bookRepository;
    private final AuthorService authorService;
    private final Book2TagService book2TagService;
    private final BookVoteService bookVoteService;
    private final DateTimeFormatter dateTimeFormatter;
    private final BookReviewService bookReviewService;
    private final Book2GenreService book2GenreService;
    private final Book2AuthorService book2AuthorService;
    private final Book2UserRepository book2UserRepository;
    private final ResourceStorageService resourceStorageService;

    private final LocalDate minLocalDate;
    private final LocalDate maxLocalDate;

    private final MessageSource messageSource;

    public BookServiceJpaImpl(UserService userService, CookieService cookieService, BookRepository bookRepository, AuthorService authorService, Book2TagService book2TagService, Book2GenreService book2GenreService, ResourceStorageService resourceStorageService, BookVoteService bookVoteService, BookReviewService bookReviewService, Book2AuthorService book2AuthorService, Book2UserRepository book2UserRepository, MessageSource messageSource) {
        this.userService = userService;
        this.messageSource = messageSource;
        this.authorService = authorService;
        this.cookieService = cookieService;
        this.bookRepository = bookRepository;
        this.bookVoteService = bookVoteService;
        this.book2TagService = book2TagService;
        this.bookReviewService = bookReviewService;
        this.book2GenreService = book2GenreService;
        this.book2AuthorService = book2AuthorService;
        this.book2UserRepository = book2UserRepository;
        this.resourceStorageService = resourceStorageService;
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

    @Override
    public BookListDto getPageableAllBooks(Pageable pageable, String searchQuery) {
        Page<BookEntity> bookEntityPage;
        if (searchQuery == null || searchQuery.isBlank()) {
            bookEntityPage = bookRepository.findAll(pageable);
        } else {
            bookEntityPage = bookRepository.findBookEntitiesByTitleContainingIgnoreCase(pageable, searchQuery);
        }
        var bookListDto = createBookListDtoFromPage(bookEntityPage);
        return applyStatusesToBookListDto(bookListDto, "", "", null);
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
    public BookListDto getPaidBooksByCurrentUser() {
        return createBookListDtoForCurrentUserByStatus(Book2UserType.PAID);
    }

    @Override
    public BookListDto getArchivedBooksByCurrentUser() {
        return createBookListDtoForCurrentUserByStatus(Book2UserType.ARCHIVED);
    }

    private BookListDto createBookListDtoForCurrentUserByStatus(Book2UserType type) {
        var currentUser = userService.getCurrentUser();
        var userBooks = bookRepository.findBookEntitiesByUserAndType(currentUser, type);
        var dtoList = userBooks.stream().map(this::convertSingleBookEntityToBookListElementDto).toList();
        BookListDto dto = new BookListDto();
        dto.setTotal(dtoList.size());
        dto.setBooks(dtoList);
        return addStatusesToAllBooks(dto, currentUser);
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
        var bookEntity = bookRepository.findBookEntityBySlug(slug).orElseThrow(NotFoundException::new);
        BookSlugDto bookSlugDto = convertSingleBookEntityToBookSlugDto(bookEntity);
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
        return bookRepository.findBookEntityBySlug(slug).orElseThrow(NotFoundException::new);
    }

    private LocalDate parseToDate(String stringDate) {
        return stringDate.equals("0") ? maxLocalDate : LocalDate.parse(stringDate, dateTimeFormatter);
    }

    private LocalDate parseFromDate(String stringDate) {
        return stringDate.equals("0") ? minLocalDate : LocalDate.parse(stringDate, dateTimeFormatter);
    }

    private BookListDto createBookListDtoFromPage(Page<BookEntity> bookEntityPage) {
        BookListDto dto = new BookListDto();
        dto.setTotal(bookEntityPage.getTotalElements());
        dto.setBooks(convertManyBookEntityToBookDto(bookEntityPage.getContent()));
        dto.setPerPage(bookEntityPage.getSize());
        dto.setPage(bookEntityPage.getNumber());
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
            dto.setAuthors(bookEntity.getAuthors().get(0).getName() + " " + messageSource.getMessage("book.authors.many", new Object[]{}, LocaleContextHolder.getLocale()));
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

    @Override
    public BookEditDto getBookEditDtoBySlug(String slug) {
        var bookEntity = bookRepository.findBookEntityBySlug(slug).orElseThrow(NotFoundException::new);
        var bookEditDto = new BookEditDto();
        bookEditDto.setId(bookEntity.getId());
        bookEditDto.setBestseller(bookEntity.getIsBestseller());
        bookEditDto.setDescription(bookEntity.getDescription());
        bookEditDto.setDiscount(bookEntity.getDiscount());
        bookEditDto.setSlug(bookEntity.getSlug());
        bookEditDto.setPrice(bookEntity.getPrice());
        bookEditDto.setTitle(bookEntity.getTitle());
        bookEditDto.setTags(bookEntity.getTags().stream().map(TagEntity::getName).toArray(String[]::new));
        bookEditDto.setAuthorSlug(bookEntity.getAuthors().stream().map(AuthorEntity::getSlug).toArray(String[]::new));
        bookEditDto.setGenreSlug(bookEntity.getGenres().stream().map(GenreEntity::getSlug).toArray(String[]::new));
        return bookEditDto;
    }

    @Override
    @Transactional
    public void updateBookEntity(BookEditDto bookEditDto) throws IOException {
        var bookEntity = bookRepository.findById(bookEditDto.getId()).orElseThrow(NotFoundException::new);
        bookEntity.setDiscount(bookEditDto.getDiscount());
        bookEntity.setPrice(bookEntity.getPrice());
        bookEntity.setDescription(bookEntity.getDescription());
        bookEntity.setIsBestseller(bookEditDto.isBestseller());
        bookEntity.setSlug(bookEditDto.getSlug());
        bookEntity.setTitle(bookEditDto.getTitle());
        if (bookEditDto.getBookImage() != null && bookEditDto.getBookImage().getResource().contentLength() != 0) {
            var newImagePath = resourceStorageService.saveNewBookImage(bookEditDto.getBookImage(), bookEntity.getSlug());
            bookEntity.setImage(newImagePath);
        }
        if (bookEditDto.getEpubFile() != null && bookEditDto.getEpubFile().getResource().contentLength() != 0) {
            resourceStorageService.saveBookFile(bookEditDto.getEpubFile(), bookEntity, BookFiletype.EPUB);
        }
        if (bookEditDto.getPdfFile() != null && bookEditDto.getPdfFile().getResource().contentLength() != 0) {
            resourceStorageService.saveBookFile(bookEditDto.getPdfFile(), bookEntity, BookFiletype.PDF);
        }
        if (bookEditDto.getFb2File() != null && bookEditDto.getFb2File().getResource().contentLength() != 0) {
            resourceStorageService.saveBookFile(bookEditDto.getFb2File(), bookEntity, BookFiletype.FB2);
        }
        bookRepository.save(bookEntity);
        book2AuthorService.setAuthorsToBook(bookEntity, Arrays.asList(bookEditDto.getAuthorSlug()));
        book2TagService.setTagsToBook(bookEntity, Arrays.asList(bookEditDto.getTags()));
        book2GenreService.setGenresToBook(bookEntity, Arrays.asList(bookEditDto.getGenreSlug()));
    }
}
