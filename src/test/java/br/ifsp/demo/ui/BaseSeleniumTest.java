package br.ifsp.demo.ui;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class BaseSeleniumTest {
    protected static final Faker faker = new Faker();
    protected WebDriver driver;

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        setInitialPage();
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }

    protected void setInitialPage(){}
}
