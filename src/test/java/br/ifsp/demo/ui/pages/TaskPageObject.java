package br.ifsp.demo.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class TaskPageObject extends BasePageObject {
    private static final String PAGE_TITLE = "Task";

    public TaskPageObject(WebDriver driver) {
        super(driver);
        if (!PAGE_TITLE.equals(pageTitle())) throw new IllegalStateException("Wrong page url: " + driver.getCurrentUrl());
    }

    public WebElement getTaskTitle() {
        return driver.findElement(By.cssSelector("#task-container h3"));
    }

    public WebElement getTaskDescription() {
        return driver.findElement(By.cssSelector("#task-container p:first-of-type"));
    }

    public WebElement getTaskStatus() {
        return driver.findElement(By.cssSelector("#task-container p:nth-of-type(2)"));
    }

    public WebElement getTaskDeadline() {
        return driver.findElement(By.cssSelector("#task-container p:nth-of-type(3)"));
    }
}
