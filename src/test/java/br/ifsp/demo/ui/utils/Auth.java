package br.ifsp.demo.ui.utils;

import br.ifsp.demo.ui.pages.*;
import com.github.javafaker.Faker;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;

import java.time.Duration;

public class Auth {
    private final static Faker faker = new Faker();

    public static TaskListPageObject registerAndLogin(WebDriver driver, String email, String password) {
        var loginPage = new LoginPageObject(driver);
        var registerPage = loginPage.navigateToRegisterPage();

        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(5))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .until(ExpectedConditions.elementToBeClickable(registerPage.registerButton()));

        registerPage.fillName(faker.name().firstName());
        registerPage.fillLastnameField(faker.name().lastName());
        registerPage.fillEmail(email);
        registerPage.fillPassword(password);
        loginPage = registerPage.clickRegister();

        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(5))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .until(ExpectedConditions.elementToBeClickable(loginPage.byUsernameField()));

        loginPage.fillUsername(email);
        loginPage.fillPassword(password);

        var taskListPage = loginPage.login();
        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(5))
                .pollingEvery(Duration.ofMillis(300))
                .ignoring(NoSuchElementException.class)
                .until(ExpectedConditions.elementToBeClickable(taskListPage.byCreateTask()));

        return taskListPage;
    }
}
