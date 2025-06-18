package br.ifsp.demo.ui;

import br.ifsp.demo.ui.pages.CreateTaskPageObject;
import br.ifsp.demo.ui.utils.Auth;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CreateTaskTest extends BaseSeleniumTest {
    @Override
    protected void setInitialPage() {
        driver.get("http://localhost:8081/index.html");
    }

    @Test
    @Tag("UiTest")
    @DisplayName("should create a task")
    void shouldCreateATask() {
        String email = faker.internet().emailAddress();
        String password = faker.internet().password();
        var taskListPage = Auth.registerAndLogin(driver, email, password);

        var createTaskPage = taskListPage.navigateToCreateTaskPage();

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

        taskListPage = createTaskPage.submitTaskExpectingSuccess();

        var taskPage = taskListPage.navigateToTaskPage(title);

        DateTimeFormatter expectedDeadlineFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String expectedDeadline = futureDateTime.format(expectedDeadlineFormatter) + ":00";

        assertThat(taskPage.getTaskTitle()).isEqualTo(title);
        assertThat(taskPage.getTaskDescription()).isEqualTo(description);
        assertThat(taskPage.getTaskDeadline()).isEqualTo("Deadline: " + expectedDeadline);
        assertThat(taskPage.getTaskStatus()).isEqualTo("Status: PENDING");
    }

    @Test
    @Tag("UiTest")
    @DisplayName("should not create a task when title is empty")
    void shouldNotCreateATaskWhenTitleIsEmpty() {
        String email = faker.internet().emailAddress();
        String password = faker.internet().password();
        var taskListPage = Auth.registerAndLogin(driver, email, password);

        var createTaskPage = taskListPage.navigateToCreateTaskPage();

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

        createTaskPage.submitTaskExpectingFailure();

        assertThat(createTaskPage.getErrorMessage()).isEqualTo("All fields are required.");
        assertThat(driver.getTitle()).isEqualTo(CreateTaskPageObject.PAGE_TITLE);
    }

    @Test
    @DisplayName("should not create a task when description is empty")
    void shouldNotCreateATaskWhenDescriptionIsEmpty() {
        String email = faker.internet().emailAddress();
        String password = faker.internet().password();
        var taskListPage = Auth.registerAndLogin(driver, email, password);

        var createTaskPage = taskListPage.navigateToCreateTaskPage();

        String title = faker.name().title();
        createTaskPage.fillTaskTitle(title);

        Date futureDate = faker.date().future(365, TimeUnit.DAYS);
        LocalDateTime futureDateTime = futureDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        String date = futureDateTime.format(dateFormatter);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmm");
        String time = futureDateTime.format(timeFormatter);
        createTaskPage.fillTaskDeadline(date, time);

        createTaskPage.submitTaskExpectingFailure();

        assertThat(createTaskPage.getErrorMessage()).isEqualTo("All fields are required.");
        assertThat(driver.getTitle()).isEqualTo(CreateTaskPageObject.PAGE_TITLE);
    }
}
