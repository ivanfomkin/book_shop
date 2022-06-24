package com.github.ivanfomkin.bookshop.selenium;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BookShopNavigationSeleniumTest extends AbstractSeleniumTest {

    @Test
    void testBookShopNavigation() {
        var navigation = new BookShopNavigationPage(driver, "http://localhost:8085");
        navigation.callMainPage();
        assertTrue(driver.getPageSource().contains("BOOKSHOP"));

        navigation.callFirstBookPage();
        assertTrue(driver.getPageSource().contains("Оцените книгу:"));

        navigation.callGenresPageFromMenu();
        assertTrue(driver.getPageSource().contains("Жанры"));

        navigation.callBooksByGenrePage();
        assertTrue(driver.getCurrentUrl().contains("/genres/"));

        navigation.callRecentBooksPageFromMenu();
        assertTrue(driver.getPageSource().contains("Новинки"));

        navigation.callFirstBookPage();
        assertTrue(driver.getPageSource().contains("Оцените книгу:"));

        navigation.callPopularBooksPageFromMenu();
        assertTrue(driver.getPageSource().contains("Популярное"));

        navigation.callSecondBookPage();
        assertTrue(driver.getPageSource().contains("Оцените книгу:"));

        navigation.callAuthorsPageFromMenu().callAuthorsNavigation();
        assertTrue(driver.getPageSource().contains("Авторы"));

        navigation.callRandomAuthorPage();
        assertTrue(driver.getPageSource().contains("Биография"));

        navigation.callMainPageFromMenu();
        assertTrue(driver.getPageSource().contains("BOOKSHOP"));

    }
}
