package com.example.MyBookShopApp.controller;

import com.example.MyBookShopApp.AbstractTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MainPageControllerTest extends AbstractTest {
    @Test
    void mainPage_getMainPage_statusIsOk() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }
}