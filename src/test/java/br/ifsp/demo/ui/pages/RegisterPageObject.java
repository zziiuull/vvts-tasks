package br.ifsp.demo.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class RegisterPageObject extends BasePageObject {
    private static final String PAGE_TITLE = "Register";

    public RegisterPageObject(WebDriver driver) {
        super(driver);
        if (!PAGE_TITLE.equals(pageTitle())) throw new IllegalStateException("Wrong page url: " + driver.getCurrentUrl());
    }

    private final By nameField = By.id("name");
    private final By lastnameField = By.id("lastname");
    private final By emailField = By.id("email");
    private final By passwordField = By.id("password");
    private final By registerButton = By.xpath("//button[text()='Register']");

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

    public LoginPageObject clickRegister() {
        driver.findElement(registerButton).click();
        return new LoginPageObject(driver);
    }
}
