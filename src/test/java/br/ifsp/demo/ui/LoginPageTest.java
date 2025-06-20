package br.ifsp.demo.ui;

import br.ifsp.demo.ui.pages.LoginPageObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;

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
        loginPage.clickLoginExpectingFailure();

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
        loginPage.clickLoginExpectingFailure();

        String error = loginPage.waitForErrorMessage();
        assertThat(error).contains("Username or password is incorrect.");
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Should show error for empty password")
    void shouldShowErrorForEmptyPassword() {
        loginPage.fillUsername("user@email.com");
        loginPage.fillPassword("");
        loginPage.clickLoginExpectingFailure();

        String error = loginPage.waitForPasswordError();
        assertThat(error).contains("Password is required");
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Should reject incorrect credentials")
    void shouldRejectIncorrectCredentials() {
        loginPage.fillUsername("fakeuser@ifsp.edu.br");
        loginPage.fillPassword("wrongpass");
        loginPage.clickLoginExpectingFailure();

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
        loginPage.clickLoginExpectingSuccess();

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
        loginPage.clickLoginExpectingFailure();

        String error = loginPage.waitForErrorMessage();
        assertThat(error).contains("Username or password is incorrect.");
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Should login successfully with email and password containing spaces")
    void shouldLoginSuccessfullyWithEmailAndPasswordContainingSpaces() {
        loginPage.fillUsername("    valid.user@ifsp.edu.br  ");
        loginPage.fillPassword("    validPass123    ");
        loginPage.clickLoginExpectingSuccess();

        assertThat(driver.getCurrentUrl()).contains("tasklist.html");
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Should allow login with email using uppercase letters")
    void shouldAllowLoginWithEmailUsingUppercaseLetters() {
        loginPage.fillUsername("VALID.USER@IFSP.EDU.BR");
        loginPage.fillPassword("validPass123");
        loginPage.clickLoginExpectingSuccess();

        assertThat(driver.getCurrentUrl()).contains("tasklist.html");
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Should store token in localStorage after login")
    void shouldStoreTokenInLocalStorageAfterLogin() {
        loginPage.fillUsername("valid.user@ifsp.edu.br");
        loginPage.fillPassword("validPass123");
        loginPage.clickLoginExpectingSuccess();

        JavascriptExecutor js = (JavascriptExecutor) driver;
        String token = (String) js.executeScript("return localStorage.getItem('tokenTaskVVTS');");

        assertThat(token).isNotBlank();
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Should not allow access to register page after login")
    void shouldNotAllowAccessToRegisterPageAfterLogin() {
        loginPage.fillUsername("valid.user@ifsp.edu.br");
        loginPage.fillPassword("validPass123");
        loginPage.clickLoginExpectingSuccess();

        driver.get("http://localhost:8081/register.html");

        assertThat(driver.getCurrentUrl()).doesNotContain("register.html");
    }
}
