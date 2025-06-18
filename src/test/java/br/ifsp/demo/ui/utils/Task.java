package br.ifsp.demo.ui.utils;

import br.ifsp.demo.ui.CreateTaskTest;
import br.ifsp.demo.ui.pages.CreateTaskPageObject;
import br.ifsp.demo.ui.pages.TaskListPageObject;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;

import java.time.Duration;

public class Task {
    public static TaskListPageObject createTask(WebDriver driver, TaskListPageObject taskListPageObject, String title, String description, String date, String time){
        CreateTaskPageObject createTaskPageObject = taskListPageObject.navigateToCreateTaskPage();

        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(5))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .until(ExpectedConditions.titleIs(CreateTaskPageObject.PAGE_TITLE));

        createTaskPageObject.fillTaskTitle(title);
        createTaskPageObject.fillTaskDescription(description);
        createTaskPageObject.fillTaskDeadline(date, time);

        return createTaskPageObject.submitTaskExpectingSuccess();
    }
}
