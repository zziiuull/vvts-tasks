package br.ifsp.demo.ui;

import br.ifsp.demo.ui.pages.TaskListPageObject;
import br.ifsp.demo.ui.utils.Auth;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class DeleteTaskTest extends BaseSeleniumTest {
    @Override
    protected void setInitialPage() {
        driver.get("http://localhost:8081/index.html");
    }

    @Test
    @Tag("UiTest")
    @DisplayName("should delete a task")
    void shouldDeleteATask() {
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

        String alertMessage = taskPage.deleteTask();
        taskListPage = new TaskListPageObject(driver);

        assertThat(alertMessage).isEqualTo("Task deleted successfully.");
        assertThat(taskListPage.getTasks()).isEqualTo(List.of());
    }
}
