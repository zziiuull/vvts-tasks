package br.ifsp.demo.ui;

import br.ifsp.demo.ui.pages.*;
import br.ifsp.demo.ui.utils.Auth;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class EditTaskTest extends BaseSeleniumTest {
    @Override
    protected void setInitialPage(){
        driver.get("http://localhost:8081/index.html");
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Should edit a task")
    void shouldEditATask(){
        String email = faker.internet().emailAddress();
        String password = faker.internet().password();
        var taskListPage = Auth.registerAndLogin(driver, email, password);
        
        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(5))
                .pollingEvery(Duration.ofMillis(300))
                .ignoring(NoSuchElementException.class)
                .until(ExpectedConditions.elementToBeClickable(taskListPage.byCreateTask()));

        var createTaskPage = taskListPage.navigateToCreateTaskPage();

        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(5))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .until(ExpectedConditions.elementToBeClickable(createTaskPage.byCreateButton()));

        String title = faker.name().title();
        createTaskPage.fillTaskTitle(title);

        String description = faker.lorem().sentence();
        createTaskPage.fillTaskDescription(description);

        Date futureDate = faker.date().future(365, TimeUnit.DAYS);
        LocalDateTime futureDateTime = futureDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        String date = futureDateTime.format(dateFormatter);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmm");
        String time = futureDateTime.format(timeFormatter);
        createTaskPage.fillTaskDeadline(date, time);

        taskListPage = createTaskPage.submitTask();

        new FluentWait<>(driver)
            .withTimeout(Duration.ofSeconds(5))
            .pollingEvery(Duration.ofMillis(500))
            .ignoring(NoSuchElementException.class)
            .until(ExpectedConditions.elementToBeClickable(taskListPage.byCreateTask()));

        var taskPage = taskListPage.navigateToTaskPage(title);

        new FluentWait<>(driver)
            .withTimeout(Duration.ofSeconds(5))
            .pollingEvery(Duration.ofMillis(500))
            .ignoring(NoSuchElementException.class)
            .until(ExpectedConditions.presenceOfElementLocated(taskPage.byTaskTitle()));

        EditTaskPageObject editTaskPageObject = taskPage.editTask();

        String newTaskTitle = faker.name().title();
        String newDescription = faker.lorem().sentence(10);
        Date newFutureDate = faker.date().future(365, TimeUnit.DAYS);
        LocalDateTime newFutureDateTime = newFutureDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        String newDate = newFutureDateTime.format(dateFormatter);
        String newTime = newFutureDateTime.format(timeFormatter);

        editTaskPageObject.fillTaskTitleInput(newTaskTitle);
        editTaskPageObject.fillTaskDescriptionInput(newDescription);
        editTaskPageObject.fillTaskDeadlineInput(newDate, newTime);

        TaskListPageObject taskListPageObject = editTaskPageObject.editTask();

        DateTimeFormatter expectedDeadlineFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String expectedDeadline = newFutureDateTime.format(expectedDeadlineFormatter) + ":00";

        TaskPageObject taskPageObject = taskListPageObject.navigateToTaskPage(newTaskTitle);

        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(5))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .until(ExpectedConditions.presenceOfElementLocated(taskPageObject.byTaskTitle()));

        assertThat(taskPageObject.getTaskTitle()).isEqualTo(newTaskTitle);
        assertThat(taskPageObject.getTaskDescription()).isEqualTo(newDescription);
        assertThat(taskPageObject.getTaskDeadline()).isEqualTo("Deadline: " + expectedDeadline);
        assertThat(taskPageObject.getTaskStatus()).isEqualTo("Status: PENDING");
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Should not edit a task and show all fiels are required message when title is empty")
    void shouldNotEditATaskAndShowAllFielsAreRequiredMessageWhenTitleIsEmpty(){
        String email = faker.internet().emailAddress();
        String password = faker.internet().password();
        var taskListPage = Auth.registerAndLogin(driver, email, password);

        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(5))
                .pollingEvery(Duration.ofMillis(300))
                .ignoring(NoSuchElementException.class)
                .until(ExpectedConditions.elementToBeClickable(taskListPage.byCreateTask()));

        var createTaskPage = taskListPage.navigateToCreateTaskPage();

        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(5))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .until(ExpectedConditions.elementToBeClickable(createTaskPage.byCreateButton()));

        String title = faker.name().title();
        createTaskPage.fillTaskTitle(title);

        String description = faker.lorem().sentence();
        createTaskPage.fillTaskDescription(description);

        Date futureDate = faker.date().future(365, TimeUnit.DAYS);
        LocalDateTime futureDateTime = futureDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        String date = futureDateTime.format(dateFormatter);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmm");
        String time = futureDateTime.format(timeFormatter);
        createTaskPage.fillTaskDeadline(date, time);

        taskListPage = createTaskPage.submitTask();

        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(5))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .until(ExpectedConditions.elementToBeClickable(taskListPage.byCreateTask()));

        var taskPage = taskListPage.navigateToTaskPage(title);

        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(5))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .until(ExpectedConditions.presenceOfElementLocated(taskPage.byTaskTitle()));

        EditTaskPageObject editTaskPageObject = taskPage.editTask();

        String newDescription = faker.lorem().sentence(3);
        Date newFutureDate = faker.date().future(365, TimeUnit.DAYS);
        LocalDateTime newFutureDateTime = newFutureDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        String newDate = newFutureDateTime.format(dateFormatter);
        String newTime = newFutureDateTime.format(timeFormatter);

        editTaskPageObject.blankTaskTitle();
        editTaskPageObject.fillTaskDescriptionInput(newDescription);
        editTaskPageObject.fillTaskDeadlineInput(newDate, newTime);

        editTaskPageObject.tryEditTask();

        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(5))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .until(ExpectedConditions.visibilityOfElementLocated(editTaskPageObject.getErrorMessageLocator()));

        assertThat(editTaskPageObject.getErrorMessage()).isEqualTo("All fields are required.");
        assertThat(driver.getTitle()).isEqualTo(EditTaskPageObject.PAGE_TITLE);
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Should not edit a task and show all fiels are required message when description is empty")
    void shouldNotEditATaskAndShowAllFielsAreRequiredMessageWhenDescriptionIsEmpty(){
        String email = faker.internet().emailAddress();
        String password = faker.internet().password();
        var taskListPage = Auth.registerAndLogin(driver, email, password);

        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(5))
                .pollingEvery(Duration.ofMillis(300))
                .ignoring(NoSuchElementException.class)
                .until(ExpectedConditions.elementToBeClickable(taskListPage.byCreateTask()));

        var createTaskPage = taskListPage.navigateToCreateTaskPage();

        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(5))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .until(ExpectedConditions.elementToBeClickable(createTaskPage.byCreateButton()));

        String title = faker.name().title();
        createTaskPage.fillTaskTitle(title);

        String description = faker.lorem().sentence();
        createTaskPage.fillTaskDescription(description);

        Date futureDate = faker.date().future(365, TimeUnit.DAYS);
        LocalDateTime futureDateTime = futureDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        String date = futureDateTime.format(dateFormatter);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmm");
        String time = futureDateTime.format(timeFormatter);
        createTaskPage.fillTaskDeadline(date, time);

        taskListPage = createTaskPage.submitTask();

        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(5))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .until(ExpectedConditions.elementToBeClickable(taskListPage.byCreateTask()));

        var taskPage = taskListPage.navigateToTaskPage(title);

        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(5))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .until(ExpectedConditions.presenceOfElementLocated(taskPage.byTaskTitle()));

        EditTaskPageObject editTaskPageObject = taskPage.editTask();

        String newTaskTitle = faker.name().title();
        Date newFutureDate = faker.date().future(365, TimeUnit.DAYS);
        LocalDateTime newFutureDateTime = newFutureDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        String newDate = newFutureDateTime.format(dateFormatter);
        String newTime = newFutureDateTime.format(timeFormatter);

        editTaskPageObject.fillTaskTitleInput(newTaskTitle);
        editTaskPageObject.blankTaskDescription();
        editTaskPageObject.fillTaskDeadlineInput(newDate, newTime);

        editTaskPageObject.tryEditTask();

        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(5))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .until(ExpectedConditions.visibilityOfElementLocated(editTaskPageObject.getErrorMessageLocator()));

        assertThat(editTaskPageObject.getErrorMessage()).isEqualTo("All fields are required.");
        assertThat(driver.getTitle()).isEqualTo(EditTaskPageObject.PAGE_TITLE);
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Should not edit a task and show all fiels are required message when deadline is empty")
    void shouldNotEditATaskAndShowAllFielsAreRequiredMessageWhenDeadlineIsEmpty(){
        String email = faker.internet().emailAddress();
        String password = faker.internet().password();
        var taskListPage = Auth.registerAndLogin(driver, email, password);

        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(5))
                .pollingEvery(Duration.ofMillis(300))
                .ignoring(NoSuchElementException.class)
                .until(ExpectedConditions.elementToBeClickable(taskListPage.byCreateTask()));

        var createTaskPage = taskListPage.navigateToCreateTaskPage();

        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(5))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .until(ExpectedConditions.elementToBeClickable(createTaskPage.byCreateButton()));

        String title = faker.name().title();
        createTaskPage.fillTaskTitle(title);

        String description = faker.lorem().sentence();
        createTaskPage.fillTaskDescription(description);

        Date futureDate = faker.date().future(365, TimeUnit.DAYS);
        LocalDateTime futureDateTime = futureDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        String date = futureDateTime.format(dateFormatter);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmm");
        String time = futureDateTime.format(timeFormatter);
        createTaskPage.fillTaskDeadline(date, time);

        taskListPage = createTaskPage.submitTask();

        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(5))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .until(ExpectedConditions.elementToBeClickable(taskListPage.byCreateTask()));

        var taskPage = taskListPage.navigateToTaskPage(title);

        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(5))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .until(ExpectedConditions.presenceOfElementLocated(taskPage.byTaskTitle()));

        EditTaskPageObject editTaskPageObject = taskPage.editTask();

        String newTaskTitle = faker.name().title();
        String newDescription = faker.lorem().sentence(3);

        editTaskPageObject.fillTaskTitleInput(newTaskTitle);
        editTaskPageObject.fillTaskDescriptionInput(newDescription);
        editTaskPageObject.blankTaskDeadline();

        editTaskPageObject.tryEditTask();

        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(5))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .until(ExpectedConditions.visibilityOfElementLocated(editTaskPageObject.getErrorMessageLocator()));

        assertThat(editTaskPageObject.getErrorMessage()).isEqualTo("All fields are required.");
        assertThat(driver.getTitle()).isEqualTo(EditTaskPageObject.PAGE_TITLE);
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Should not edit a task and show alert when deadline is in past")
    void shouldNotEditATaskAndShowAlertWhenDeadlineIsInPast(){
        String email = faker.internet().emailAddress();
        String password = faker.internet().password();
        var taskListPage = Auth.registerAndLogin(driver, email, password);

        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(5))
                .pollingEvery(Duration.ofMillis(300))
                .ignoring(NoSuchElementException.class)
                .until(ExpectedConditions.elementToBeClickable(taskListPage.byCreateTask()));

        var createTaskPage = taskListPage.navigateToCreateTaskPage();

        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(5))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .until(ExpectedConditions.elementToBeClickable(createTaskPage.byCreateButton()));

        String title = faker.name().title();
        createTaskPage.fillTaskTitle(title);

        String description = faker.lorem().sentence();
        createTaskPage.fillTaskDescription(description);

        Date futureDate = faker.date().past(365, TimeUnit.DAYS);
        LocalDateTime futureDateTime = futureDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        String date = futureDateTime.format(dateFormatter);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmm");
        String time = futureDateTime.format(timeFormatter);
        createTaskPage.fillTaskDeadline(date, time);

        taskListPage = createTaskPage.submitTask();

        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(5))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .until(ExpectedConditions.elementToBeClickable(taskListPage.byCreateTask()));

        var taskPage = taskListPage.navigateToTaskPage(title);

        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(5))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .until(ExpectedConditions.presenceOfElementLocated(taskPage.byTaskTitle()));

        EditTaskPageObject editTaskPageObject = taskPage.editTask();

        String newTaskTitle = faker.name().title();
        String newDescription = faker.lorem().sentence(3);
        Date newPastDate = faker.date().past(365, TimeUnit.DAYS);
        LocalDateTime newPastDateTime = newPastDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        String newDate = newPastDateTime.format(dateFormatter);
        String newTime = newPastDateTime.format(timeFormatter);

        editTaskPageObject.fillTaskTitleInput(newTaskTitle);
        editTaskPageObject.fillTaskDescriptionInput(newDescription);
        editTaskPageObject.fillTaskDeadlineInput(newDate, newTime);

        editTaskPageObject.tryEditTask();

        final Alert alert = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.alertIsPresent());
        final String alertText = alert.getText();

        assertThat(alertText).isEqualTo("Error editing task. Please try again later.");
    }
}
