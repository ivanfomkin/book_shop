package com.github.ivanfomkin.bookshop.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ivanfomkin.bookshop.AbstractTest;
import com.github.ivanfomkin.bookshop.dto.security.ContactConfirmationRequestDto;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SecurityRestControllerTest extends AbstractTest {

    private final ObjectMapper objectMapper;
    private static String alreadyExistUserEmail;
    private static String alreadyExistUserPassword;

    @Autowired
    SecurityRestControllerTest(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @BeforeAll
    static void setUp() {
        alreadyExistUserEmail = "test@user.ru";
        alreadyExistUserPassword = "qwerty";
    }

    @Test
    @Sql(scripts = {"/sql/insert_test_users.sql"})
    void login_loginWIthValidEmailAndPassword_resultIsTrueAndCookieContainToken() throws Exception {
        var dto = new ContactConfirmationRequestDto();
        dto.setCode(alreadyExistUserPassword);
        dto.setContact(alreadyExistUserEmail);
        mockMvc.perform(post("/login")
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is(true)))
                .andExpect(jsonPath("$", not(hasKey("error"))))
                .andExpect(jsonPath("$.token", not(emptyOrNullString())))
                .andExpect(cookie().exists("token"))
                .andExpect(cookie().value("token", not(emptyOrNullString())));
    }


    @Test
    @Sql(scripts = {"/sql/insert_test_users.sql"})
    void login_loginWIthValidEmailAndBadPassword_resultIsFalseAndCookieDoesNotContainToken() throws Exception {
        var dto = new ContactConfirmationRequestDto();
        dto.setCode("bad_password");
        dto.setContact(alreadyExistUserEmail);
        mockMvc.perform(post("/login")
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is(false)))
                .andExpect(jsonPath("$", hasKey("error")))
                .andExpect(jsonPath("$", not(hasKey("token"))))
                .andExpect(cookie().doesNotExist("token"));
    }

    @Test
    @Sql(scripts = {"/sql/insert_test_users.sql"})
    void login_loginWIthNonExistUser_resultIsFalseAndCookieDoesNotContainToken() throws Exception {
        var dto = new ContactConfirmationRequestDto();
        dto.setCode("bad_password");
        dto.setContact("badEmail@mail.com");
        mockMvc.perform(post("/login")
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is(false)))
                .andExpect(jsonPath("$", hasKey("error")))
                .andExpect(jsonPath("$", not(hasKey("token"))))
                .andExpect(cookie().doesNotExist("token"));
    }

    @Test
    @WithUserDetails("test@user.ru")
    @Sql(scripts = {"/sql/insert_test_users.sql"})
    void logout_logoutUser_redirectToSigningPageAndCookeDoesNotContainsToken() throws Exception {
        mockMvc.perform(post("/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/signin"))
                .andExpect(cookie().value("token", emptyOrNullString()));
    }
}