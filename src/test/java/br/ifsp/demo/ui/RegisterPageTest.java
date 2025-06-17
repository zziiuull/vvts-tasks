package br.ifsp.demo.ui;

import br.ifsp.demo.ui.pages.LoginPageObject;
import br.ifsp.demo.ui.pages.RegisterPageObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterPageTest extends BaseSeleniumTest {

    private RegisterPageObject registerPage;

    @Override
    protected void setInitialPage() {
        driver.get("http://localhost:8081/register.html");
        registerPage = new RegisterPageObject(driver);
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Should register user successfully")
    void shouldRegisterUserSuccessfully() {
        String name = faker.name().firstName();
        String lastname = faker.name().lastName();
        String email = faker.internet().emailAddress();
        String password = faker.internet().password(6, 20);

        registerPage.fillName(name);
        registerPage.fillLastnameField(lastname);
        registerPage.fillEmail(email);
        registerPage.fillPassword(password);

        LoginPageObject loginPage = registerPage.clickRegisterExpectingSuccess();

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.titleIs(LoginPageObject.PAGE_TITLE));

        assertThat(loginPage.pageTitle()).isEqualTo(LoginPageObject.PAGE_TITLE);
    }

    @ParameterizedTest(name = "[{index}] Invalid email: {0}")
    @CsvSource({
            "user@mail",
            "user@.com",
            "user@com.",
            ".@mail.com",
            "?@mail.com",
            "user@@mail.com",
            "<>@mail.com"
    })
    @Tag("UiTest")
    @DisplayName("Should show error for invalid email format")
    void shouldShowErrorForInvalidEmailFormat(String email) {
        String name = faker.name().firstName();
        String lastname = faker.name().lastName();
        String password = faker.internet().password(6, 20);

        registerPage.fillName(name);
        registerPage.fillLastnameField(lastname);
        registerPage.fillEmail(email);
        registerPage.fillPassword(password);

        registerPage.clickRegisterExpectingFailure();

        WebElement emailError = new WebDriverWait(driver, Duration.ofSeconds(3))
                .until(ExpectedConditions.visibilityOfElementLocated(By.id("email-error")));

        assertThat(emailError.getText())
                .as("Esperava mensagem para e-mail inválido: %s", email)
                .contains("Invalid email format.");
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Should show error for empty name")
    void shouldShowErrorForEmptyName() {
        String lastname = faker.name().lastName();
        String email = faker.internet().emailAddress();
        String password = faker.internet().password(6, 20);

        registerPage.fillName("");
        registerPage.fillLastnameField(lastname);
        registerPage.fillEmail(email);
        registerPage.fillPassword(password);

        registerPage.clickRegisterExpectingFailure();

        assertThat(registerPage.getNameError()).contains("Name is required.");
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Should show error for empty lastname")
    void shouldShowErrorForEmptyLastName() {
        String name = faker.name().firstName();
        String email = faker.internet().emailAddress();
        String password = faker.internet().password(6, 20);

        registerPage.fillName(name);
        registerPage.fillLastnameField("");
        registerPage.fillEmail(email);
        registerPage.fillPassword(password);

        registerPage.clickRegisterExpectingFailure();

        assertThat(registerPage.getLastNameError()).contains("Last name is required.");
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Should show error for empty email")
    void shouldShowErrorForEmptyError() {
        String name = faker.name().firstName();
        String lastname = faker.name().lastName();
        String password = faker.internet().password(6, 20);

        registerPage.fillName(name);
        registerPage.fillLastnameField(lastname);
        registerPage.fillEmail("");
        registerPage.fillPassword(password);

        registerPage.clickRegisterExpectingFailure();

        assertThat(registerPage.getEmailError()).contains("Email is required.");

    }
}