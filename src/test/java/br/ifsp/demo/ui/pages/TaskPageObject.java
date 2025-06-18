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

    public By getTaskTitleLocator() {
        return taskTitle;
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
}
