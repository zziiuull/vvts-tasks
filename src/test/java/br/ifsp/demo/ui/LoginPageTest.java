package br.ifsp.demo.ui;

import br.ifsp.demo.ui.pages.LoginPageObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
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
        loginPage.loginWithFailure();

        String usernameError = loginPage.waitForUsernameError();
        assertThat(usernameError).isNotBlank();
        assertThat(usernameError).contains("Username is required");
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Should navigate to register page")
    void shouldNavigateToRegisterPage() {
        loginPage.navigateToRegisterPage();

        assertThat(driver.getTitle()).isEqualTo("Register");
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Should show error for invalid username")
    void shouldShowErrorForInvalidUsername() {
        loginPage.fillUsername("a");
        loginPage.fillPassword("validPass123");
        loginPage.loginWithFailure();

        String error = loginPage.waitForErrorMessage();
        assertThat(error).contains("Username or password is incorrect.");
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Should show error for empty password")
    void shouldShowErrorForEmptyPassword() {
        loginPage.fillUsername("user@email.com");
        loginPage.fillPassword("");
        loginPage.loginWithFailure();

        String error = loginPage.waitForPasswordError();
        assertThat(error).contains("Password is required");
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Should reject incorrect credentials")
    void shouldRejectIncorrectCredentials() {
        loginPage.fillUsername("fakeuser@ifsp.edu.br");
        loginPage.fillPassword("wrongpass");
        loginPage.loginWithFailure();

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
        loginPage.login();

        assertThat(driver.getCurrentUrl()).contains("tasklist.html");
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Should render correctly on small screen")
    void shouldRenderCorrectlyOnSmallScreen() {
        driver.manage().window().setSize(new Dimension(375, 667));

        assertThat(driver.findElement(By.className("login-container")).isDisplayed()).isTrue();
    }

    @ParameterizedTest(name = "[{index}] Invalid email: {0}")
    @CsvSource({
            ".@mail.com",
            "?@mail.com",
            "@mail.com",
            "user@mail",
            "user@.com",
            "user@com.",
            "user@@mail.com",
            "user mail@mail.com",
            "user<>mail@mail.com"
    })
    @Tag("UiTest")
    @DisplayName("Should reject invalid email formats")
    void shouldRejectInvalidEmailFormats(String email) {
        loginPage.fillUsername(email);
        loginPage.fillPassword("somePassword123");
        loginPage.loginWithFailure();

        String error = loginPage.waitForErrorMessage();
        assertThat(error).contains("Username or password is incorrect.");
    }

}
