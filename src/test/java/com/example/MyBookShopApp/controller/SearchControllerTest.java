package com.example.MyBookShopApp.controller;

import com.example.MyBookShopApp.AbstractTest;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SearchControllerTest extends AbstractTest {
    @Test
    void searchPage_searchExistsBookByName_searchPageResultContainsRequestedBook() throws Exception {
        mockMvc.perform(get("/search/Doom"))
                .andExpect(status().isOk())
                .andExpect(xpath("/html/body/div/div/main/div[2]/div/div[1]/div[2]/strong/a").string("Sword of Doom, The (Dai-bosatsu tôge)"));
    }

    @Test
    void searchPage_emptySearchQuery_getErrorMessageOnPage() throws Exception {
        mockMvc.perform(get("/search/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attributeExists("searchError"));
    }
}