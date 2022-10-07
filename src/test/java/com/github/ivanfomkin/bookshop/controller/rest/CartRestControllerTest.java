package com.github.ivanfomkin.bookshop.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ivanfomkin.bookshop.AbstractTest;
import com.github.ivanfomkin.bookshop.dto.cart.ChangeBookStatusRequestDto;
import com.github.ivanfomkin.bookshop.entity.book.links.Book2UserEntity;
import com.github.ivanfomkin.bookshop.entity.enums.Book2UserType;
import com.github.ivanfomkin.bookshop.repository.Book2UserRepository;
import com.github.ivanfomkin.bookshop.repository.Book2UserTypeRepository;
import com.github.ivanfomkin.bookshop.repository.BookRepository;
import com.github.ivanfomkin.bookshop.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;

import javax.servlet.http.Cookie;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class CartRestControllerTest extends AbstractTest {
    private static String cartCookieName;
    private static String bookSlug;

    private static ChangeBookStatusRequestDto changeBookStatusDto;

    private static Book2UserType cartType;
    private static Book2UserType deleteFromCartType;


    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final Book2UserRepository book2UserRepository;
    private final Book2UserTypeRepository book2UserTypeRepository;

    @Autowired
    CartRestControllerTest(ObjectMapper objectMapper, Book2UserRepository book2UserRepository, Book2UserTypeRepository book2UserTypeRepository, UserRepository userRepository, BookRepository bookRepository) {
        this.objectMapper = objectMapper;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.book2UserRepository = book2UserRepository;
        this.book2UserTypeRepository = book2UserTypeRepository;
    }

    @BeforeAll
    static void setUp() {
        bookSlug = "book-330-nyc";
        cartCookieName = "cartContent";
        cartType = Book2UserType.CART;
        deleteFromCartType = Book2UserType.UNLINK;
        changeBookStatusDto = new ChangeBookStatusRequestDto();
        changeBookStatusDto.setSlugs(bookSlug);
    }

    @AfterEach
    void tearDown() {
        changeBookStatusDto.setStatus(null);
    }

    @Test
    void changeBookStatus_addBookToCardWithUnauthorizedUser_cartCookieContainsBookSlug() throws Exception {
        changeBookStatusDto.setStatus(cartType);
        mockMvc.perform(
                        post("/api/cart/changeBookStatus")
                                .content(objectMapper.writeValueAsString(changeBookStatusDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result", is(true)))
                .andExpect(cookie().value(cartCookieName, bookSlug));
    }

    @Test
    void changeBookStatus_addBookToCardWithUnauthorizedUserWhenBookAlreadyExistInCookie_cartCookieContainsBookSlugOnce() throws Exception {
        changeBookStatusDto.setStatus(cartType);
        mockMvc.perform(
                        post("/api/cart/changeBookStatus")
                                .content(objectMapper.writeValueAsString(changeBookStatusDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result", is(true)))
                .andExpect(cookie().value(cartCookieName, bookSlug))
                .andExpect(cookie().value(cartCookieName, hasLength(bookSlug.length())));
    }

    @Test
    void changeBookStatus_deleteBookFromCartWithUnauthorizedUserWhenBookAlreadyExistInCookie_cartCookieDoesNotContainsBookSlug() throws Exception {
        changeBookStatusDto.setStatus(deleteFromCartType);
        mockMvc.perform(
                        post("/api/cart//changeBookStatus")
                                .content(objectMapper.writeValueAsString(changeBookStatusDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .cookie(new Cookie(cartCookieName, bookSlug)))
                .andExpect(jsonPath("$.result", is(true)))
                .andExpect(cookie().value(cartCookieName, emptyOrNullString()));
    }


    @Test
    @Sql(scripts = {"/sql/insert_test_users.sql"})
    @WithUserDetails("test@user.ru")
    void changeBookStatus_addBookToCardWithAuthorizedUser_cartCookieContainsBookSlug() throws Exception {
        changeBookStatusDto.setStatus(cartType);
        mockMvc.perform(
                        post("/api/cart/changeBookStatus")
                                .content(objectMapper.writeValueAsString(changeBookStatusDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result", is(true)));
        var currentUser = userRepository.findUserEntityByContactsContact("test@user.ru");
        var book2userEntity = book2UserRepository.findBookStatusByUserAndSlug(currentUser, bookSlug);
        var book2UserType = book2userEntity.getType();
        assertNotNull(book2userEntity);
        assertEquals(cartType, book2UserType.getName());
    }

    @Test
    @Sql(scripts = {"/sql/insert_test_users.sql"})
    @WithUserDetails("test@user.ru")
    void changeBookStatus_addBookToCardWithAuthorizedUserWhenBookAlreadyExistInUserCart_book2UserRepoContainsCartRowOnce() throws Exception {
        changeBookStatusDto.setStatus(cartType);
        var bookEntity = bookRepository.findBookEntityBySlug(bookSlug).get();
        var currentUser = userRepository.findUserEntityByContactsContact("test@user.ru");
        var alreadyExistCartEntity = new Book2UserEntity();
        alreadyExistCartEntity.setBook(bookEntity);
        alreadyExistCartEntity.setUser(currentUser);
        alreadyExistCartEntity.setType(book2UserTypeRepository.findBook2UserTypeEntityByName(cartType));
        book2UserRepository.save(alreadyExistCartEntity);

        mockMvc.perform(
                        post("/api/cart/changeBookStatus")
                                .content(objectMapper.writeValueAsString(changeBookStatusDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result", is(true)));

        var book2userEntity = book2UserRepository.findBookStatusByUserAndSlug(currentUser, bookSlug);
        var book2UserType = book2userEntity.getType();
        assertNotNull(book2userEntity);
        assertEquals(cartType, book2UserType.getName());
        assertEquals(alreadyExistCartEntity.getId(), book2userEntity.getId());

    }


    @Test
    @Sql(scripts = {"/sql/insert_test_users.sql"})
    @WithUserDetails("test@user.ru")
    void changeBookStatus_deleteBookFromCartWithAuthorizedUserWhenBookExistsInCart_book2userRepositoryDoesNotContainsRow() throws Exception {
        changeBookStatusDto.setStatus(deleteFromCartType);
        var bookEntity = bookRepository.findBookEntityBySlug(bookSlug).get();
        var currentUser = userRepository.findUserEntityByContactsContact("test@user.ru");
        var alreadyExistCartEntity = new Book2UserEntity();
        alreadyExistCartEntity.setBook(bookEntity);
        alreadyExistCartEntity.setUser(currentUser);
        alreadyExistCartEntity.setType(book2UserTypeRepository.findBook2UserTypeEntityByName(cartType));
        book2UserRepository.save(alreadyExistCartEntity);

        mockMvc.perform(
                        post("/api/cart/changeBookStatus")
                                .content(objectMapper.writeValueAsString(changeBookStatusDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result", is(true)));

        var book2userEntity = book2UserRepository.findBookStatusByUserAndSlug(currentUser, bookSlug);
        assertNull(book2userEntity);
    }
}