package com.github.ivanfomkin.bookshop.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class BookShopMainPage {

    private static final String SEARCH_QUERY_ELEMENT_ID = "query";
    private static final String SEARCH_BUTTON_ELEMENT_ID = "search";
    private final ChromeDriver driver;
    private final String url;

    public BookShopMainPage(ChromeDriver driver, String url) {
        this.driver = driver;
        this.url = url;
    }

    public BookShopMainPage callPage() {
        driver.get(url);
        return this;
    }

    public BookShopMainPage setUpSearchQuery(String searchQuery) {
        var searchInputElement = getElementById(SEARCH_QUERY_ELEMENT_ID);
        searchInputElement.sendKeys(searchQuery);
        return this;
    }

    public BookShopMainPage submitSearchQuery() {
        var submitSearchQueryButtonElement = getElementById(SEARCH_BUTTON_ELEMENT_ID);
        submitSearchQueryButtonElement.submit();
        return this;
    }

    private WebElement getElementById(String id) {
        return driver.findElement(By.id(id));
    }
}
