package br.ifsp.demo.ui.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class TaskPageObject extends BasePageObject {
    public static final String PAGE_TITLE = "Task";

    public TaskPageObject(WebDriver driver) {
        super(driver);
        if (!PAGE_TITLE.equals(pageTitle())) throw new IllegalStateException("Wrong page url: " + driver.getCurrentUrl());
    }

    private final By taskTitle = By.cssSelector("#task-container h3");
    private final By errorMessage = By.id("error-message");

    public By getTaskTitleLocator() {
        return taskTitle;
    }

    public By byErrorMessage() {
        return errorMessage;
    }

    public String getTaskTitle() {
        return driver.findElement(taskTitle).getText();
    }

    public String getTaskDescription() {
        return driver.findElement(By.cssSelector("#task-container p:first-of-type")).getText();
    }

    public String getTaskStatus() {
        return driver.findElement(By.cssSelector("#task-container p:nth-of-type(2)")).getText();
    }

    public String getTaskDeadline() {
        return driver.findElement(By.cssSelector("#task-container p:nth-of-type(3)")).getText();
    }

    public String getStartTime() {
        return driver.findElement(By.cssSelector("#task-container p:nth-of-type(4)")).getText();
    }

    public String getFinishTime() {
        return driver.findElement(By.cssSelector("#task-container p:nth-of-type(5)")).getText();
    }

    public String getErrorMessage(){
        return driver.findElement(errorMessage).getText();
    }

    public EditTaskPageObject navigateToEditPage(){
        driver.findElement(By.id("edit-task-btn")).click();
        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(5))
                .pollingEvery(Duration.ofMillis(500))
                .until(ExpectedConditions.titleIs(EditTaskPageObject.PAGE_TITLE));

        return new EditTaskPageObject(driver);
    }

    public String deleteTask(){
        driver.findElement(By.id("delete-task-btn")).click();

        final Alert alert = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.alertIsPresent());
        String alertMsg = alert.getText();
        alert.accept();

        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(5))
                .pollingEvery(Duration.ofMillis(500))
                .until(ExpectedConditions.titleIs(TaskListPageObject.PAGE_TITLE));

        return alertMsg;
    }

    public TaskPageObject clockIn(){
        driver.findElement(By.id("clock-in-btn")).click();
        return this;
    }

    public TaskPageObject clockOut(){
        driver.findElement(By.id("clock-out-btn")).click();
        return this;
    }

    public TaskPageObject markAsCompleted(){
        driver.findElement(By.id("mark-complete-btn")).click();
        return this;
    }
}
