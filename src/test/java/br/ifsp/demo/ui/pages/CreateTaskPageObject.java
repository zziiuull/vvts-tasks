package br.ifsp.demo.ui.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CreateTaskPageObject extends BasePageObject {
    public static final String PAGE_TITLE = "Create-task";

    public CreateTaskPageObject(WebDriver driver) {
        super(driver);
        if (!PAGE_TITLE.equals(pageTitle())) throw new IllegalStateException("Wrong page url: " + driver.getCurrentUrl());
    }

    private final By createButton = By.id("create-task-btn");

    public By getCreateButtonLocator(){
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

    public TaskListPageObject submitTaskExpectingSuccess() {
        driver.findElement(createButton).click();
        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(5))
                .pollingEvery(Duration.ofMillis(300))
                .until(ExpectedConditions.titleIs(TaskListPageObject.PAGE_TITLE));
        return new TaskListPageObject(driver);
    }

    public void submitTaskExpectingFailure(){
        driver.findElement(createButton).click();
    }

    public String getAlertMessage(){
        final Alert alert = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.alertIsPresent());
        return alert.getText();
    }

    public String getErrorMessage(){
        return driver.findElement(By.id("error-message")).getText();
    }
}
