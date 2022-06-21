package com.example.MyBookShopApp.controller;

import com.example.MyBookShopApp.AbstractTest;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SecurityControllerTest extends AbstractTest {


    @Test
    void signInPage_getSignInPage_statusIsOk() throws Exception {
        mockMvc.perform(get("/signin"))
                .andExpect(status().isOk());
    }

    @Test
    void signUpPage_getSignUpPage_statusIsOk() throws Exception {
        mockMvc.perform(get("/signup"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@user.ru")
    void myPage_getMyPageForAuthorizedUser_statusIsOk() throws Exception {
        mockMvc.perform(get("/my"))
                .andExpect(status().isOk());
    }

    @Test
    void myArchive_getMyArchiveForUnauthorizedUser_redirectToSigningPage() throws Exception {
        mockMvc.perform(get("/myarchive"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/signin"));
    }

    @Test
    @WithMockUser(username = "test@user.ru")
    void myArchive_getMyArchiveForAuthorizedUser_statusIsOk() throws Exception {
        mockMvc.perform(get("/myarchive"))
                .andExpect(status().isOk());
    }


    @Test
    void profile_getProfileForUnauthorizedUser_redirectToSigningPage() throws Exception {
        mockMvc.perform(get("/profile"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/signin"));
    }

    @Test
    @WithUserDetails("test@user.ru")
    @Sql(scripts = {"/sql/insert_test_users.sql"})
    void profile_getProfileForAuthorizedUser_statusIsOk() throws Exception {
        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk());
    }
}