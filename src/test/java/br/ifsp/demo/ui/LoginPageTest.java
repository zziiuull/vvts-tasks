package br.ifsp.demo.ui;

import br.ifsp.demo.ui.pages.LoginPageObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.time.Duration;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class LoginPageTest extends BaseSeleniumTest {

    private LoginPageObject loginPage;

    @Override
    protected void setInitialPage() {
        driver.get("file://front/html/index.html");
        loginPage = new LoginPageObject(driver);
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Should show error when fields are empty")
    void shouldShowErrorWhenFieldsAreEmpty() {
        loginPage.clickLogin();
        String error = loginPage.waitForErrorMessage();
        assertThat(error).isNotBlank();
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Should navigate to register page")
    void shouldNavigateToRegisterPage() {
        loginPage.clickRegister();

        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(5))
                .pollingEvery(Duration.ofMillis(300))
                .ignoring(NoSuchElementException.class);

        wait.until(webDriver -> Objects.requireNonNull(webDriver.getTitle()).equalsIgnoreCase("Register"));
        assertThat(driver.getTitle()).isEqualTo("Register");
    }
}