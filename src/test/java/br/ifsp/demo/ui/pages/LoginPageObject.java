package br.ifsp.demo.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

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

    public void fillUsername(String username) {
        driver.findElement(usernameField).clear();
        driver.findElement(usernameField).sendKeys(username);
    }

    public void fillPassword(String password) {
        driver.findElement(passwordField).clear();
        driver.findElement(passwordField).sendKeys(password);
    }


}
