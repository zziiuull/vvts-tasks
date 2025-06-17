package br.ifsp.demo.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class RegisterPageObject extends BasePageObject {
    public static final String PAGE_TITLE = "Register";

    public RegisterPageObject(WebDriver driver) {
        super(driver);
        new WebDriverWait(driver, Duration.ofSeconds(3))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("registerForm")));
        if (!PAGE_TITLE.equals(pageTitle())) throw new IllegalStateException("Wrong page url: " + driver.getCurrentUrl());
    }

    private final By nameField = By.id("name");
    private final By lastnameField = By.id("lastname");
    private final By emailField = By.id("email");
    private final By passwordField = By.id("password");
    private final By registerButton = By.cssSelector("button[type='submit']");

    public By registerButton() {
        return registerButton;
    }

    public void fillName(String name) {
        driver.findElement(nameField).sendKeys(name);
    }

    public void fillLastnameField(String lastname) {
        driver.findElement(lastnameField).sendKeys(lastname);
    }

    public void fillEmail(String email) {
        driver.findElement(emailField).sendKeys(email);
    }

    public void fillPassword(String password) {
        driver.findElement(passwordField).sendKeys(password);
    }

    public String getEmailError() {
        return driver.findElement(By.id("email-error")).getText();
    }

    public LoginPageObject clickRegisterExpectingSuccess() {
        driver.findElement(registerButton).click();
        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(5))
                .pollingEvery(Duration.ofMillis(300))
                .until(ExpectedConditions.titleIs(LoginPageObject.PAGE_TITLE));
        return new LoginPageObject(driver);
    }


}
