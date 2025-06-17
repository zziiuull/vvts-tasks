package br.ifsp.demo.ui;

import br.ifsp.demo.ui.pages.LoginPageObject;
import br.ifsp.demo.ui.pages.RegisterPageObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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

        LoginPageObject loginPage = registerPage.clickRegister();

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.titleIs(LoginPageObject.PAGE_TITLE));

        assertThat(loginPage.pageTitle()).isEqualTo(LoginPageObject.PAGE_TITLE);
    }
}