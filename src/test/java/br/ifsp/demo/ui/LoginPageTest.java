package br.ifsp.demo.ui;

import br.ifsp.demo.ui.pages.LoginPageObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
}