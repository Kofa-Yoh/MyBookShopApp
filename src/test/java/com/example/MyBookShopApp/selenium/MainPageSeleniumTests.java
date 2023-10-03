package com.example.MyBookShopApp.selenium;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.List;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MainPageSeleniumTests {

    private static ChromeDriver driver;
    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @BeforeAll
    static void setup() {
        System.setProperty("webdriver.chrome.driver", "D:\\Prjcts\\chromedriver-win64\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(5));
    }

    @Test
    public void testMainPageAccess() throws InterruptedException {
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callPage()
                .pause();

        assertTrue(driver.getPageSource().contains("BOOKSHOP"));
    }

    @Test
    public void testUserAuthAndPagesAccess() throws InterruptedException {
        MainPage mainPage = new MainPage(driver);

        String signInPageUrl = mainPage.getUrl() + "/signin";
        String myPageUrl = mainPage.getUrl() + "/my";
        String profilePageUrl = mainPage.getUrl() + "/profile";

        String userEmail = "1@gmail.com";
        String rightPassword = "123";
        String wrongPassword = "456";

        mainPage.callPage();

        // Sign in
        driver.findElement(By.xpath("//a[@href='/my'][1]")).click();
        logger.info(driver.getCurrentUrl());
        assertThat(driver.getCurrentUrl()).isEqualTo(signInPageUrl);
        this.pause();

        // User details
        driver.findElement(By.xpath("//input[@value='mailtype'][1]")).click();
        driver.findElement(By.id("mail")).sendKeys(userEmail);
        this.pause();
        driver.findElement(By.id("sendauth")).click();
        // Wrong password
        driver.findElement(By.id("mailcode")).sendKeys(wrongPassword);
        driver.findElement(By.id("toComeInMail")).click();
        this.pause();
        assertTrue(driver.findElement(By.className("form-error")).isDisplayed());
        // Right password
        driver.findElement(By.id("mailcode")).clear();
        driver.findElement(By.id("mailcode")).sendKeys(rightPassword);
        driver.findElement(By.tagName("body")).click(); // relocate cursor from input to remove div.form-error
        this.pause();
        driver.findElement(By.id("toComeInMail")).click();
        this.pause();

        // User personal page
        logger.info(driver.getCurrentUrl());
        assertThat(driver.getCurrentUrl()).isEqualTo(myPageUrl);
        WebElement profileIcon = driver.findElement(By.xpath("//a[@href='/profile'][1]"));
        assertThat(profileIcon.isDisplayed());
        assertThat(profileIcon.findElement(By.className("CartBlock-text")).getText()).isNotEmpty();
        assertThat(profileIcon.findElement(By.className("CartBlock-budget")).getText()).isNotEmpty();
        this.pause();

        // Profile page
        profileIcon.click();
        this.pause();
        logger.info(driver.getCurrentUrl());
        assertThat(driver.getCurrentUrl()).isEqualTo(profilePageUrl);
        assertTrue(driver.getPageSource().toLowerCase().contains("личный кабинет"));

        // Logout
        driver.findElement(By.xpath("//a[@href='/user_logout'][1]")).click();
        this.pause();
        logger.info(driver.getCurrentUrl());
        assertThat(driver.getCurrentUrl()).isEqualTo(signInPageUrl);

        // Try to access to profile page for anonymous user
        driver.get(profilePageUrl);
        assertThat(driver.getCurrentUrl()).isNotEqualTo(profilePageUrl);
    }

    @Test
    public void testMenuPages() throws InterruptedException {
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callPage();

        List<String> menuLinks = driver.findElements(By.cssSelector(".menu_main .menu-link"))
                .stream()
                .map(elem -> elem.getAttribute("href"))
                .toList();
        for (String link : menuLinks) {
            driver.get(link);
            logger.info(driver.getCurrentUrl());
            this.pause();
            // Main page
            if (link.equals(mainPage.getUrl() + "/")) {
                // Tag page
                driver.findElement(By.cssSelector(".Tag_lg a")).click();
                logger.info(driver.getCurrentUrl());
            }
            // Genres page
            else if (link.contains("/genres")) {
                driver.findElement(By.cssSelector(".Tag a")).click();
                logger.info(driver.getCurrentUrl());
            }
            // Authors books page
            else if (link.contains("/authors")) {
                WebElement authorElement = driver.findElement(By.cssSelector(".Authors-item a"));
                String authorName = authorElement.getText();
                authorElement.click();
                logger.info(driver.getCurrentUrl());
                assertThat(driver.findElement(By.xpath("/html/body/div/div/main/h1")).getText()).isEqualTo(authorName);
            }

            // For pages with books block check books list is not empty and check a book page
            if (link.contains("/genres") || link.contains("/recent") || link.contains("/popular") ||
                    driver.getCurrentUrl().contains("/tags/")) {
                assertFalse(driver.findElements(By.className("Card")).isEmpty());
                // Book page from tag page
                WebElement card = driver.findElement(By.cssSelector(".Card-title"));
                String bookTitle = card.getText();
                logger.info("Book: " + bookTitle);
                card.findElement(By.tagName("a")).click();
                logger.info(driver.getCurrentUrl());
                assertThat(driver.findElement(By.xpath("/html/body/div/div/main/ul/li[3]")).getText()).isEqualTo(bookTitle);
                this.pause();
                continue;
            }
        }
    }

    @Test
    public void testMainPageSearchByQuery() throws InterruptedException {
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callPage()
                .pause()
                .setUpSearchToken("Love at the")
                .pause()
                .submit()
                .pause();

        assertTrue(driver.getPageSource().contains("Love at the Top"));
    }

    private void pause() throws InterruptedException {
        Thread.sleep(2000);
    }
}