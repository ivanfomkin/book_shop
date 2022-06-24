package com.github.ivanfomkin.bookshop.service.impl;

import com.github.ivanfomkin.bookshop.AbstractTest;
import com.github.ivanfomkin.bookshop.dto.security.ContactConfirmationRequestDto;
import com.github.ivanfomkin.bookshop.dto.security.ContactConfirmationResponse;
import com.github.ivanfomkin.bookshop.dto.security.RegistrationFormDto;
import com.github.ivanfomkin.bookshop.entity.enums.ContactType;
import com.github.ivanfomkin.bookshop.entity.user.UserContactEntity;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;
import com.github.ivanfomkin.bookshop.repository.UserContactRepository;
import com.github.ivanfomkin.bookshop.repository.UserRepository;
import com.github.ivanfomkin.bookshop.security.BookStoreUserDetails;
import com.github.ivanfomkin.bookshop.security.BookStoreUserDetailsService;
import com.github.ivanfomkin.bookshop.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceImplTest extends AbstractTest {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @MockBean
    private UserRepository userRepositoryMock;

    @MockBean
    private UserContactRepository userContactRepositoryMock;

    @MockBean
    private BookStoreUserDetailsService userDetailsService;

    private static RegistrationFormDto registrationFormDto;

    private static String testUserEmail;
    private static String testUserPassword;

    @Autowired
    UserServiceImplTest(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @BeforeAll
    static void setUp() {
        registrationFormDto = new RegistrationFormDto();
        registrationFormDto.setName("Test user");
        registrationFormDto.setEmail("test@testuser.com");
        registrationFormDto.setPhone("89302223311");
        registrationFormDto.setPassword("qwerty");

        testUserEmail = "test@user.ru";
        testUserPassword = "{noop}qwerty";
    }


    @Test
    void registerNewUser_validRegistrationForm_userEntityFieldsEqualsRegistrationFormFields() {
        var registeredUser = userService.registerNewUser(registrationFormDto);
        var registeredUserContacts = registeredUser.getContacts().stream().map(UserContactEntity::getContact).toList();
        assertNotNull(registeredUser);
        assertTrue(passwordEncoder.matches(registrationFormDto.getPassword(), registeredUser.getPassword()));
        assertEquals(registeredUser.getName(), registrationFormDto.getName());
        assertTrue(registeredUserContacts.contains(registrationFormDto.getEmail()));
        verify(userRepositoryMock, times(1)).save(any(UserEntity.class));
        verify(userContactRepositoryMock, times(1)).saveAll(any());
    }

    @Test
    void registerNewUser_userAlreadyExistsByContact_userEqualsNull() {
        doReturn(true).when(userContactRepositoryMock).existsByContactIn(any());
        var registeredUser = userService.registerNewUser(registrationFormDto);
        assertNull(registeredUser);
        verify(userRepositoryMock, never()).save(any());
        verify(userContactRepositoryMock, never()).saveAll(any());
    }

    @Test
    void jwtLogin_loginWithValidTestUserWithCorrectPassword_responseContainNotEmptyTokenAndDoesNotContainErrorMessage() {
        var dto = new ContactConfirmationRequestDto();
        dto.setCode("qwerty");
        dto.setContact(testUserEmail);
        doReturn(new BookStoreUserDetails(generateTestUser())).when(userDetailsService).loadUserByUsername(testUserEmail);
        var contactConfirmationResponse = userService.jwtLogin(dto);
        assertTrue(contactConfirmationResponse.isResult());
        assertNotNull(contactConfirmationResponse.getToken());
        assertFalse(contactConfirmationResponse.getToken().isEmpty());
        assertNull(contactConfirmationResponse.getError());
    }

    @Test
    void jwtLogin_loginWithValidTestUserWithIncorrectPassword_responseResultIsFalseAndResultDoesNotContainTokenAndContainErrorMessage() {
        var dto = new ContactConfirmationRequestDto();
        dto.setCode("gbsdyhbsajkdfnsajknkj");
        dto.setContact(testUserEmail);
        doReturn(new BookStoreUserDetails(generateTestUser())).when(userDetailsService).loadUserByUsername(testUserEmail);
        var contactConfirmationResponse = userService.jwtLogin(dto);
        assertFalse(contactConfirmationResponse.isResult());
        assertNull(contactConfirmationResponse.getToken());
        assertNotNull(contactConfirmationResponse.getError());
        assertFalse(contactConfirmationResponse.getError().isEmpty());
    }

    @Test
    void jwtLogin_loginWithInvalidUser_responseResultIsFalseAndResultDoesNotContainTokenAndContainErrorMessage() {
        var dto = new ContactConfirmationRequestDto();
        dto.setCode("gbsdyhbsajkdfnsajknkj");
        String email = "asajd@maddaada.c";
        dto.setContact(email);
        doThrow(new UsernameNotFoundException("Can't load user " + email)).when(userDetailsService).loadUserByUsername(email);
        var contactConfirmationResponse = userService.jwtLogin(dto);
        assertFalse(contactConfirmationResponse.isResult());
        assertNull(contactConfirmationResponse.getToken());
        assertNotNull(contactConfirmationResponse.getError());
        assertFalse(contactConfirmationResponse.getError().isEmpty());
    }

    private UserEntity generateTestUser() {
        var user = new UserEntity();
        var emailContact = new UserContactEntity();
        emailContact.setType(ContactType.EMAIL);
        emailContact.setContact(testUserEmail);
        user.setContacts(List.of(emailContact));
        user.setPassword(testUserPassword);
        return user;
    }
}