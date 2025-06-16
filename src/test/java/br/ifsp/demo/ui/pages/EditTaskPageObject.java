package br.ifsp.demo.ui.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;

import java.time.Duration;

public class EditTaskPageObject extends BasePageObject{
    public static final String PAGE_TITLE = "Edit-task";

    public EditTaskPageObject(WebDriver driver) {
        super(driver);
        if(!PAGE_TITLE.equals(pageTitle()))
            throw new IllegalStateException("Wrong page url:" + driver.getCurrentUrl());
    }

    private final By taskTitleInput = By.id("task-title");
    private final By taskDescriptionInput = By.id("task-description");
    private final By taskDeadlineInput = By.id("task-deadline");
    private final By createTaskButton = By.id("create-task-btn");
    private final By errorMessage = By.id("error-message");

    public void fillTaskTitleInput(String taskTitle){
        driver.findElement(taskTitleInput).clear();
        driver.findElement(taskTitleInput).sendKeys(taskTitle);
    }

    public void fillTaskDescriptionInput(String taskDescription){
        driver.findElement(taskDescriptionInput).clear();
        driver.findElement(taskDescriptionInput).sendKeys(taskDescription);
    }

    public void fillTaskDeadlineInput(String date, String time){
         var element = driver.findElement(taskDeadlineInput);
        element.sendKeys(date);
        element.sendKeys(Keys.TAB);
        element.sendKeys(time);
    }

    public TaskListPageObject editTask(){
        driver.findElement(createTaskButton).click();
        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(5))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .until(ExpectedConditions.titleIs(TaskListPageObject.PAGE_TITLE));
        return new TaskListPageObject(driver);
    }

    public String getErrorMessage(){
        return driver.findElement(errorMessage).getText();
    }
}
