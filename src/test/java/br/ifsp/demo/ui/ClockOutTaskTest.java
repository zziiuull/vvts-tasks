package br.ifsp.demo.ui;

import br.ifsp.demo.ui.pages.RegisterPageObject;
import br.ifsp.demo.ui.pages.TaskListPageObject;
import br.ifsp.demo.ui.pages.TaskPageObject;
import br.ifsp.demo.ui.utils.Auth;
import br.ifsp.demo.ui.utils.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class ClockOutTaskTest extends BaseSeleniumTest {

    @Override
    protected void setInitialPage() {
        driver.get("http://localhost:8081/index.html");
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Should clock in and clock out successfuly")
    void shouldClockInAndClockOutSuccessfuly(){
        String email = faker.internet().emailAddress();
        String password = faker.internet().password();
        var taskListPageObject = Auth.registerAndLogin(driver, email, password);

        String title = faker.name().title();
        String description = faker.lorem().sentence(3);
        Date futureDate = faker.date().future(365, TimeUnit.DAYS);
        LocalDateTime futureDateTime = futureDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        String date = futureDateTime.format(dateFormatter);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmm");
        String time = futureDateTime.format(timeFormatter);
        taskListPageObject = Task.createTask(driver, taskListPageObject, title, description, date, time);

        var taskPage = taskListPageObject.navigateToTaskPage(title);

        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(5))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .until(ExpectedConditions.presenceOfElementLocated(taskPage.byTaskTitle()));

        taskPage = taskPage.clockIn();

        final Alert alertClockIn = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.alertIsPresent());
        String alertClockInMsg = alertClockIn.getText();
        alertClockIn.accept();

        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(5))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .until(ExpectedConditions.presenceOfElementLocated(taskPage.byTaskTitle()));

        String taskStatusClockIn = taskPage.getTaskStatus();

        taskPage = taskPage.clockOut();

        final Alert alertClockOut = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.alertIsPresent());
        String alertClockOutMsg = alertClockOut.getText();
        alertClockOut.accept();

        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(5))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .until(ExpectedConditions.presenceOfElementLocated(taskPage.byTaskTitle()));

        String taskStatusClockOut = taskPage.getTaskStatus();

        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(5))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .until(ExpectedConditions.presenceOfElementLocated(taskPage.byTaskTitle()));

        assertThat(taskPage.getTaskTitle()).isEqualTo(title);
        assertThat(taskPage.getTaskDescription()).isEqualTo(description);
        assertThat(alertClockInMsg).isEqualTo("Clock-in successful.");
        assertThat(alertClockOutMsg).isEqualTo("Clock-out successful.");
        assertThat(taskStatusClockIn).isEqualTo("Status: IN_PROGRESS");
        assertThat(taskStatusClockOut).isEqualTo("Status: COMPLETED");
        assertThat(taskPage.getStartTime()).isNotNull();
        assertThat(taskPage.getFinishTime()).isNotNull();
    }
}
