package br.ifsp.demo.ui;

import br.ifsp.demo.ui.pages.CreateTaskPageObject;

public class CreateTaskTest extends BaseSeleniumTest {
    private CreateTaskPageObject createTaskPage;

    @Override
    public void setInitialPage(){
        String page = "file://front/html/createTask.html";
        driver.get(page);
        createTaskPage = new CreateTaskPageObject(driver);
    }
}
