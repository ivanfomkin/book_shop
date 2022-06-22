package com.example.MyBookShopApp.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class MainPageSeleniumTest {
    private static ChromeDriver driver;
    private final MessageSource messageSource;

    @Autowired
    public MainPageSeleniumTest(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @BeforeAll
    private static void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().timeouts().pageLoadTimeout(Duration.of(5, ChronoUnit.SECONDS));
    }

    @AfterAll
    private static void tearDown() {
        driver.quit();
    }

    @Test
    void testMainPageAccess() throws InterruptedException {
        var page = new BookShopPage(driver, "http://localhost:8085");
        page
                .callPage()
                .pause();
        assertTrue(driver.getPageSource().contains("BOOKSHOP"));
    }

    @Test
    void testSearchQueryForExistingBook() throws InterruptedException {
        var searchQuery = "Doom";
        var expectedBookTitle = "Sword of Doom, The (Dai-bosatsu tôge)";
        var page = new BookShopPage(driver, "http://localhost:8085");
        page
                .callPage()
                .pause()
                .setUpSearchQuery(searchQuery)
                .pause()
                .submitSearchQuery()
                .pause();

        assertTrue(driver.getPageSource().contains(expectedBookTitle));
    }

    @Test
    void testSearchQueryErrorIfQueryIsEmpty() throws InterruptedException {
        var expectedErrorMessage = messageSource.getMessage("search.error", new Object[]{}, Locale.ENGLISH);
        var page = new BookShopPage(driver, "http://localhost:8085");
        page
                .callPage()
                .pause()
                .submitSearchQuery()
                .pause();

        assertTrue(driver.getPageSource().contains(expectedErrorMessage));
    }
}
