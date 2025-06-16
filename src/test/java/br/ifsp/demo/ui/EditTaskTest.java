package br.ifsp.demo.ui;

import br.ifsp.demo.ui.pages.*;
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

        taskListPage = createTaskPage.submitTask();

        var taskPage = taskListPage.navigateToTaskPage(title);

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

        assertThat(taskPageObject.getTaskTitle()).isEqualTo(newTaskTitle);
        assertThat(taskPageObject.getTaskDescription()).isEqualTo(newDescription);
        assertThat(taskPageObject.getTaskDeadline()).isEqualTo("Deadline: " + expectedDeadline);
        assertThat(taskPageObject.getTaskStatus()).isEqualTo("Status: PENDING");
    }
}
