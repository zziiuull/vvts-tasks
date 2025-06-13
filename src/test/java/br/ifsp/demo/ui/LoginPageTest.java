package br.ifsp.demo.ui;

import br.ifsp.demo.ui.pages.LoginPageObject;

import static org.junit.jupiter.api.Assertions.*;

class LoginPageTest extends BaseSeleniumTest {

    private LoginPageObject loginPage;

    @Override
    protected void setInitialPage() {
        driver.get("file://front/html/index.html");
        loginPage = new LoginPageObject(driver);
    }

}