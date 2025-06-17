package br.ifsp.demo.ui;

import br.ifsp.demo.ui.pages.LoginPageObject;
import br.ifsp.demo.ui.pages.RegisterPageObject;

import static org.junit.jupiter.api.Assertions.*;

class RegisterPageTest extends BaseSeleniumTest {

    private RegisterPageObject registerPage;

    @Override
    protected void setInitialPage() {
        driver.get("http://localhost:8081/index.html");
        registerPage = new RegisterPageObject(driver);
    }


}