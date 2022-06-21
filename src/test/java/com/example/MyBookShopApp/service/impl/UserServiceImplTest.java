package com.example.MyBookShopApp.service.impl;

import com.example.MyBookShopApp.AbstractTest;
import com.example.MyBookShopApp.dto.security.RegistrationFormDto;
import com.example.MyBookShopApp.entity.user.UserContactEntity;
import com.example.MyBookShopApp.entity.user.UserEntity;
import com.example.MyBookShopApp.repository.UserContactRepository;
import com.example.MyBookShopApp.repository.UserRepository;
import com.example.MyBookShopApp.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    private static RegistrationFormDto registrationFormDto;

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
        doReturn(true).when(userContactRepositoryMock).existsAllByContactIn(any());
        var registeredUser = userService.registerNewUser(registrationFormDto);
        assertNull(registeredUser);
        verify(userRepositoryMock, never()).save(any());
        verify(userContactRepositoryMock, never()).saveAll(any());
    }
}