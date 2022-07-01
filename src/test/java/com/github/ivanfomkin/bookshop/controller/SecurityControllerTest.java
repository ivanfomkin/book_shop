package com.github.ivanfomkin.bookshop.controller;

import com.github.ivanfomkin.bookshop.AbstractTest;
import com.github.ivanfomkin.bookshop.repository.UserRepository;
import com.github.ivanfomkin.bookshop.util.CommonUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SecurityControllerTest extends AbstractTest {

    private final UserRepository userRepository;

    private final String errorRegisterMessage;
    private final String successRegisterMessage;

    private static String newUserEmail;
    private static String newUserName;
    private static String newUserPassword;
    private static String newUserPhone;
    private static String alreadyExistsUserPhone;
    private static String alreadyExistsUserEmail;

    @Autowired
    SecurityControllerTest(UserRepository userRepository, MessageSource messageSource) {
        this.userRepository = userRepository;
        errorRegisterMessage = messageSource.getMessage("registration.failed", new Object[]{}, Locale.ENGLISH);
        successRegisterMessage = messageSource.getMessage("registration.success", new Object[]{}, Locale.ENGLISH);
    }

    @BeforeAll
    static void setUp() {
        newUserEmail = "test-mail@user.com";
        newUserName = "TestUser";
        newUserPassword = "test-password123";
        newUserPhone = "+7 (999) 123-45-67";
        alreadyExistsUserPhone = "+7 (999) 999-99-99";
        alreadyExistsUserEmail = "test@user.ru";
    }

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

    @Test
    void registerUser_registerNewValidUserWithEmail_userContainsInDb() throws Exception {
        mockMvc.perform(
                        post("/registration")
                                .param("email", newUserEmail)
                                .param("name", newUserName)
                                .param("password", newUserPassword)
                )
                .andExpect(status().isOk())
                .andExpect(xpath("/html/body/div/div[2]/main/form/div/div[1]/div[1]/label/span").string(successRegisterMessage));

        assertTrue(userRepository.existsByContacts_contactIn(List.of(newUserEmail)));
    }

    @Test
    void registerUser_registerNewValidUserWithPhone_userContainsInDb() throws Exception {
        mockMvc.perform(
                        post("/registration")
                                .param("phone", newUserPhone)
                                .param("name", newUserName)
                                .param("password", newUserPassword)
                )
                .andExpect(status().isOk())
                .andExpect(xpath("/html/body/div/div[2]/main/form/div/div[1]/div[1]/label/span").string(successRegisterMessage));

        assertTrue(userRepository.existsByContacts_contactIn(List.of(CommonUtils.formatPhoneNumber(newUserPhone))));
    }

    @Test
    void registerUser_registerNewValidUserWithPhoneAndEmail_userContainsInDb() throws Exception {
        mockMvc.perform(
                        post("/registration")
                                .param("email", newUserEmail)
                                .param("phone", newUserPhone)
                                .param("name", newUserName)
                                .param("password", newUserPassword)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(xpath("/html/body/div/div[2]/main/form/div/div[1]/div[1]/label/span").string(successRegisterMessage));
        assertTrue(userRepository.existsByContacts_contactIn(List.of(CommonUtils.formatPhoneNumber(newUserPhone))));
        assertTrue(userRepository.existsByContacts_contactIn(List.of(newUserEmail)));
    }

    @Test
    @Sql(scripts = {"/sql/insert_test_users.sql"})
    void registerUser_registerUserWithAlreadyExistsPhone_registrationFailed() throws Exception {
        mockMvc.perform(
                        post("/registration")
                                .param("phone", alreadyExistsUserPhone)
                                .param("name", newUserName)
                                .param("password", newUserPassword)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(xpath("/html/body/div/div[2]/main/form/div/div[1]/div[1]/label/span").string(errorRegisterMessage));
    }

    @Test
    @Sql(scripts = {"/sql/insert_test_users.sql"})
    void registerUser_registerUserWithAlreadyExistsEmail_registrationFailed() throws Exception {
        mockMvc.perform(
                        post("/registration")
                                .param("email", alreadyExistsUserEmail)
                                .param("name", newUserName)
                                .param("password", newUserPassword)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(xpath("/html/body/div/div[2]/main/form/div/div[1]/div[1]/label/span").string(errorRegisterMessage));
    }

    @Test
    @Sql(scripts = {"/sql/insert_test_users.sql"})
    void registerUser_registerUserWithAlreadyExistsPhoneAndEmail_registrationFailed() throws Exception {
        mockMvc.perform(
                        post("/registration")
                                .param("email", alreadyExistsUserEmail)
                                .param("phone", alreadyExistsUserPhone)
                                .param("name", newUserName)
                                .param("password", newUserPassword)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(xpath("/html/body/div/div[2]/main/form/div/div[1]/div[1]/label/span").string(errorRegisterMessage));
    }
}