package com.github.ivanfomkin.bookshop.controller;

import com.github.ivanfomkin.bookshop.AbstractTest;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MainPageControllerTest extends AbstractTest {
    @Test
    void mainPage_getMainPage_statusIsOk() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }
}