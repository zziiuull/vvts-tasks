package br.ifsp.demo.ui;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class TaskListPageObject extends BasePageObject {
    private static final String PAGE_TITLE = "Task List";

    public TaskListPageObject(WebDriver driver) {
        super(driver);
        if (!PAGE_TITLE.equals(pageTitle())) throw new IllegalStateException("Wrong page url: " + driver.getCurrentUrl());
    }

    public CreateTaskPageObject navigateToCreateTaskPage() {
        driver.findElement(By.id("add-task-btn")).click();
        return new CreateTaskPageObject(driver);
    }

    public BasePageObject navigateToTaskPage(String taskId) {
        driver.findElement(By.id("task-" + taskId)).click();
        return new BasePageObject(driver);
    }

    public WebElement taskList(){
        return driver.findElement(By.id("task-list"));
    }

    public String taskTitle(String taskId) {
        return taskList().findElement(By.cssSelector("task-" + taskId + " task-title")).getText();
    }

    public String taskDescription(String taskId) {
        return taskList().findElement(By.cssSelector("task-" + taskId + " task-description")).getText();
    }

    public String taskStatus(String taskId) {
        return taskList().findElement(By.cssSelector("task-" + taskId + " task-status")).getText();}

    public String taskDeadline(String taskId) {
        return taskList().findElement(By.cssSelector("task-" + taskId + " task-deadline")).getText();
    }
}
