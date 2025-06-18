package br.ifsp.demo.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;

import java.time.Duration;
import java.util.List;

public class TaskListPageObject extends BasePageObject {
    public static final String PAGE_TITLE = "Task List";

    public TaskListPageObject(WebDriver driver) {
        super(driver);
        if (!PAGE_TITLE.equals(pageTitle())) throw new IllegalStateException("Wrong page url: " + driver.getCurrentUrl());
    }

    private final By createTask = By.id("add-task-btn");

    public By getCreateTaskLocator(){
        return createTask;
    }

    public CreateTaskPageObject navigateToCreateTaskPage() {
        driver.findElement(createTask).click();
        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(5))
                .pollingEvery(Duration.ofMillis(300))
                .until(ExpectedConditions.titleIs(CreateTaskPageObject.PAGE_TITLE));
        return new CreateTaskPageObject(driver);
    }

    public TaskPageObject navigateToTaskPage(String taskTitle) {
        List<WebElement> elements = driver.findElements(By.cssSelector("li.list-group-item"));
        for (WebElement element : elements) {
            String title = element.findElement(By.className("task-title")).getText();
            if (title.equals(taskTitle)) {
                element.click();
                return new TaskPageObject(driver);
            }
        }
        throw new IllegalStateException("Task title not found: " + taskTitle);
    }

    public List<WebElement> getTasks(){
        return driver.findElements(By.cssSelector("#taskList li"));
    }
}
