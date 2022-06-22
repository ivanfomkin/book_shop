package com.example.MyBookShopApp.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class BookShopPage {

    private static final String SEARCH_QUERY_ELEMENT_ID = "query";
    private static final String SEARCH_BUTTON_ELEMENT_ID = "search";
    private static final long SLEEP_TIME = 2000L;
    private final ChromeDriver driver;
    private final String url;

    public BookShopPage(ChromeDriver driver, String url) {
        this.driver = driver;
        this.url = url;
    }

    public BookShopPage callPage() {
        driver.get(url);
        return this;
    }

    public BookShopPage pause() throws InterruptedException {
        Thread.sleep(SLEEP_TIME);
        return this;
    }

    public BookShopPage setUpSearchQuery(String searchQuery) {
        var searchInputElement = getElementById(SEARCH_QUERY_ELEMENT_ID);
        searchInputElement.sendKeys(searchQuery);
        return this;
    }

    public BookShopPage submitSearchQuery() {
        var submitSearchQueryButtonElement = getElementById(SEARCH_BUTTON_ELEMENT_ID);
        submitSearchQueryButtonElement.submit();
        return this;
    }

    private WebElement getElementById(String id) {
        return driver.findElement(By.id(id));
    }
}
