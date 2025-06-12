package br.ifsp.demo.ui;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CreateTaskPageObject extends BasePageObject {
    private static final String PAGE_TITLE = "Create-task";

    public CreateTaskPageObject(WebDriver driver) {
        super(driver);
        if (!PAGE_TITLE.equals(pageTitle())) throw new IllegalStateException("Wrong page url: " + driver.getCurrentUrl());
    }

    public void typeTaskTitle(String taskTitle) {
        var title = driver.findElement(By.id("task-title"));
        title.sendKeys(taskTitle);
    }

    public void typeTaskDescription(String taskDescription) {
        var description = driver.findElement(By.id("task-description"));
        description.sendKeys(taskDescription);
    }

    public void typeTaskDeadline(String taskDeadline) {
        var deadline = driver.findElement(By.id("task-deadline"));
        deadline.sendKeys(taskDeadline);
    }

    public void submitTask() {
        var submitButton = driver.findElement(By.id("create-task-btn"));
        submitButton.click();
    }

    public String errorMessage(){
        return driver.findElement(By.id("error-message")).getText();
    }
}
