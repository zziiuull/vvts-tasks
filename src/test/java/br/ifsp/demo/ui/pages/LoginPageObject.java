package br.ifsp.demo.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPageObject extends BasePageObject {
    private static final String PAGE_TITLE = "Login";

    public LoginPageObject(WebDriver driver) {
        super(driver);
        if(!PAGE_TITLE.equals(pageTitle()))
            throw new IllegalStateException("Wrong page url:" + driver.getCurrentUrl());
    }

    private final By usernameField = By.id("username");
    private final By passwordField = By.id("password");
    private final By loginButton = By.cssSelector("button[type='submit']");
    private final By registerButton = By.id("toRegisterBtn");
    private final By errorMessage = By.id("errorMessage");

    public By byUsernameField() {
        return usernameField;
    }

    public void fillUsername(String username) {
        driver.findElement(usernameField).clear();
        driver.findElement(usernameField).sendKeys(username);
    }

    public void fillPassword(String password) {
        driver.findElement(passwordField).clear();
        driver.findElement(passwordField).sendKeys(password);
    }

    public TaskListPageObject clickLogin() {
        driver.findElement(loginButton).click();
        return new TaskListPageObject(driver);
    }

    public RegisterPageObject clickRegister() {
        driver.findElement(registerButton).click();
        return new RegisterPageObject(driver);
    }

    public String getUsernameErrorMessage() {
        return driver.findElement(By.id("username-error")).getText();
    }

    public String getPasswordErrorMessage() {
        return driver.findElement(By.id("password-error")).getText();
    }

    public String waitForUsernameError() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        return wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.id("username-error"))).getText();
    }

    public String waitForPasswordError() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        return wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.id("password-error"))).getText();
    }

    public String waitForErrorMessage() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage));
        return error.getText();
    }
}
