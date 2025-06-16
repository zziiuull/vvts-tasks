package br.ifsp.demo.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;

import java.time.Duration;

public class CreateTaskPageObject extends BasePageObject {
    public static final String PAGE_TITLE = "Create-task";

    public CreateTaskPageObject(WebDriver driver) {
        super(driver);
        if (!PAGE_TITLE.equals(pageTitle())) throw new IllegalStateException("Wrong page url: " + driver.getCurrentUrl());
    }

    private final By createButton = By.id("create-task-btn");

    public By byCreateButton(){
        return createButton;
    }

    public void fillTaskTitle(String taskTitle) {
        var title = driver.findElement(By.id("task-title"));
        title.sendKeys(taskTitle);
    }

    public void fillTaskDescription(String taskDescription) {
        var description = driver.findElement(By.id("task-description"));
        description.sendKeys(taskDescription);
    }

    public void fillTaskDeadline(String date, String time) {
        var deadline = driver.findElement(By.id("task-deadline"));
        deadline.sendKeys(date);
        deadline.sendKeys(Keys.TAB);
        deadline.sendKeys(time);
    }

    public TaskListPageObject submitTask() {
        driver.findElement(createButton).click();
        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(5))
                .pollingEvery(Duration.ofMillis(300))
                .until(ExpectedConditions.titleIs(TaskListPageObject.PAGE_TITLE));
        return new TaskListPageObject(driver);
    }

    public String getErrorMessage(){
        return driver.findElement(By.id("error-message")).getText();
    }
}
