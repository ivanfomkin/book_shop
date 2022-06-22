package com.github.ivanfomkin.bookshop.security;

import com.github.ivanfomkin.bookshop.AbstractTest;
import com.github.ivanfomkin.bookshop.entity.enums.ContactType;
import com.github.ivanfomkin.bookshop.entity.user.UserContactEntity;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;
import com.github.ivanfomkin.bookshop.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

class BookStoreUserDetailsServiceTest extends AbstractTest {
    private final BookStoreUserDetailsService userDetailsService;

    @MockBean
    private UserRepository userRepository;

    private static String testUserEmail;

    @BeforeAll
    static void setUp() {
        testUserEmail = "test@user.ru";
    }

    @Autowired
    BookStoreUserDetailsServiceTest(BookStoreUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Test
    void loadUserByUsername_loadExistingUser_resultIsNotNull() {
        var fakeUser = new UserEntity();
        var fakeContact = new UserContactEntity();
        fakeContact.setType(ContactType.EMAIL);
        fakeContact.setContact(testUserEmail);
        fakeUser.setContacts(List.of(fakeContact));
        doReturn(fakeUser).when(userRepository).findUserEntityByContacts_contact(testUserEmail);
        var userDetails = userDetailsService.loadUserByUsername(testUserEmail);
        assertNotNull(userDetails);
    }

    @Test
    void loadUserByUsername_loadIncorrectUser_throwUsernameNotFoundException() {
        doThrow(new UsernameNotFoundException("user not found")).when(userRepository).findUserEntityByContacts_contact(testUserEmail);
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(testUserEmail));
    }
}