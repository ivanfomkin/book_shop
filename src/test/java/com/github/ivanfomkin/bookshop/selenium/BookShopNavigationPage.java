package com.github.ivanfomkin.bookshop.selenium;

import lombok.SneakyThrows;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;
import java.util.random.RandomGenerator;

public class BookShopNavigationPage {
    private final ChromeDriver driver;
    private final String mainPageUrl;
    private final RandomGenerator randomGenerator;

    public BookShopNavigationPage(ChromeDriver driver, String mainPageUrl) {
        this.driver = driver;
        this.mainPageUrl = mainPageUrl;
        randomGenerator = RandomGenerator.getDefault();
    }

    public BookShopNavigationPage callMainPage() {
        driver.get(mainPageUrl);
        return this;
    }

    public BookShopNavigationPage callFirstBookPage() {
        List<WebElement> carTitles = getElementsByClassName("Card-title");
        carTitles.get(0).click();
        return this;
    }

    public BookShopNavigationPage callSecondBookPage() {
        List<WebElement> carTitles = getElementsByClassName("Card-title");
        carTitles.get(1).click();
        return this;
    }

    public BookShopNavigationPage callMainPageFromMenu() {
        var mainMenuLink = getElementById("main_page_link");
        mainMenuLink.click();
        return this;
    }

    public BookShopNavigationPage callGenresPageFromMenu() {
        var genresLink = getElementById("genres_page_link");
        genresLink.click();
        return this;
    }

    public BookShopNavigationPage callBooksByGenrePage() {
        var genres = getElementsByClassName("Tag");
        genres.get(randomGenerator.nextInt(genres.size())).click();
        return this;
    }

    public BookShopNavigationPage callRecentBooksPageFromMenu() {
        var recentBooksLink = getElementById("recent_page_link");
        recentBooksLink.click();
        return this;
    }

    public BookShopNavigationPage callPopularBooksPageFromMenu() {
        var popularBooksLink = getElementById("popular_page_link");
        popularBooksLink.click();
        return this;
    }

    public BookShopNavigationPage callAuthorsPageFromMenu() {
        var authorsLink = getElementById("authors_page_link");
        authorsLink.click();
        return this;
    }

    public BookShopNavigationPage callAuthorsNavigation() {
        var authorsNavigationLinks = getElementsByClassName("Authors-link");
        for (WebElement link : authorsNavigationLinks) {
            link.click();
        }
        return this;
    }

    @SneakyThrows
    public BookShopNavigationPage callRandomAuthorPage() {
        var authorLinks = getElementsByClassName("Authors-item");
        authorLinks.get(randomGenerator.nextInt(authorLinks.size())).findElements(By.tagName("a")).get(0).click();
        return this;
    }

    private WebElement getElementById(String id) {
        return driver.findElement(By.id(id));
    }

    private List<WebElement> getElementsByClassName(String className) {
        return driver.findElements(By.className(className));
    }
}
