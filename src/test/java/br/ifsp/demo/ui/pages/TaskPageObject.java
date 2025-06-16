package br.ifsp.demo.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class TaskPageObject extends BasePageObject {
    public static final String PAGE_TITLE = "Task";

    public TaskPageObject(WebDriver driver) {
        super(driver);
        if (!PAGE_TITLE.equals(pageTitle())) throw new IllegalStateException("Wrong page url: " + driver.getCurrentUrl());
    }

    private final By taskTitle = By.cssSelector("#task-container h3");

    public By byTaskTitle() {
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
}
