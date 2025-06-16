package br.ifsp.demo.ui;

import br.ifsp.demo.ui.pages.LoginPageObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class LoginPageTest extends BaseSeleniumTest {

    private LoginPageObject loginPage;

    @Override
    protected void setInitialPage() {
        driver.get("http://localhost:8081/index.html");
        loginPage = new LoginPageObject(driver);
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Should show error when fields are empty")
    void shouldShowErrorWhenFieldsAreEmpty() {
        loginPage.clickLogin();
        String error = loginPage.waitForErrorMessage();
        assertThat(error).isNotBlank();
        assertThat(error).contains("required");
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

    @Test
    @Tag("UiTest")
    @DisplayName("Should show error for invalid username")
    void shouldShowErrorForInvalidUsername() {
        loginPage.fillUsername("a");
        loginPage.fillPassword("validPass123");
        loginPage.clickLogin();

        String error = loginPage.waitForErrorMessage();
        assertThat(error).contains("invalid username");
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Should show error for empty password")
    void shouldShowErrorForEmptyPassword() {
        loginPage.fillUsername("user@email.com");
        loginPage.fillPassword("");
        loginPage.clickLogin();

        String error = loginPage.waitForErrorMessage();
        assertThat(error).contains("password");
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Should reject incorrect credentials")
    void shouldRejectIncorrectCredentials() {
        loginPage.fillUsername("fakeuser@ifsp.edu.br");
        loginPage.fillPassword("wrongpass");
        loginPage.clickLogin();

        String error = loginPage.waitForErrorMessage();
        assertThat(error).contains("incorrect");
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Should login with valid credentials")
    void shouldLoginWithValidCredentials() {
        String email = "valid.user@ifsp.edu.br";
        String password = "validPass123";

        loginPage.fillUsername(email);
        loginPage.fillPassword(password);
        loginPage.clickLogin();

        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(web -> Objects.requireNonNull(web.getCurrentUrl()).contains("tasklist.html"));

        assertThat(driver.getCurrentUrl()).contains("tasklist.html");
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Should render correctly on small screen")
    void shouldRenderCorrectlyOnSmallScreen() {
        driver.manage().window().setSize(new Dimension(375, 667));

        assertThat(driver.findElement(By.className("login-container")).isDisplayed()).isTrue();
    }

}