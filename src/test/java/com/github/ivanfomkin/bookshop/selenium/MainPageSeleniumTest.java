package com.github.ivanfomkin.bookshop.selenium;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
class MainPageSeleniumTest extends AbstractSeleniumTest {
    private final MessageSource messageSource;

    @Autowired
    public MainPageSeleniumTest(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Test
    void testMainPageAccess() throws InterruptedException {
        var page = new BookShopMainPage(driver, "http://localhost:8085");
        page
                .callPage();
        assertTrue(driver.getPageSource().contains("BOOKSHOP"));
    }

    @Test
    void testSearchQueryForExistingBook() throws InterruptedException {
        var searchQuery = "Doom";
        var expectedBookTitle = "Sword of Doom, The (Dai-bosatsu tôge)";
        var page = new BookShopMainPage(driver, "http://localhost:8085");
        page
                .callPage()
                .setUpSearchQuery(searchQuery)
                .submitSearchQuery();

        assertTrue(driver.getPageSource().contains(expectedBookTitle));
    }

    @Test
    void testSearchQueryErrorIfQueryIsEmpty() throws InterruptedException {
        var expectedErrorMessage = messageSource.getMessage("search.error", new Object[]{}, Locale.ENGLISH);
        var page = new BookShopMainPage(driver, "http://localhost:8085");
        page
                .callPage()
                .submitSearchQuery();

        assertTrue(driver.getPageSource().contains(expectedErrorMessage));
    }
}
