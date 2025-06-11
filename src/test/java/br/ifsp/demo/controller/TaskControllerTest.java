package br.ifsp.demo.controller;

import br.ifsp.demo.security.user.User;
import br.ifsp.demo.tasks.JpaTaskRepository;
import br.ifsp.demo.tasks.TaskEntity;
import br.ifsp.demo.tasks.dtos.CreateTaskDTO;
import br.ifsp.demo.tasks.dtos.ResponseTaskDTO;
import io.restassured.RestAssured;
import io.restassured.filter.log.LogDetail;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

class TaskControllerTest extends BaseApiIntegrationTest {
    @Autowired
    private JpaTaskRepository taskRepository;

    @AfterEach
    public void tearDown (){
        taskRepository.deleteAll();
    }

    @Test
    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @DisplayName("Should create a task")
    void shouldCreateATask() {
        String password = "user123";
        User user = registerUser(password);
        final CreateTaskDTO createTaskDTO = EntityBuilder.createRandomCreateTaskDTO();
        final String token = authenticate(user.getEmail(), password);

        final ResponseTaskDTO response =
            given().contentType("application/json")
                .port(RestAssured.port)
                .body(createTaskDTO)
                .header("Authorization", "Bearer " + token)
            .when()
                .post("api/v1/task/create")
            .then()
                .log()
                .ifValidationFails(LogDetail.BODY)
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(ResponseTaskDTO.class);

        List<TaskEntity> tasks = taskRepository.findAll();
        TaskEntity found = tasks.getFirst();
        assertThat(tasks).hasSize(1);

        assertThat(found.getId()).isEqualTo(response.id());
        assertThat(found.getTitle()).isEqualTo(response.title());
        assertThat(found.getDescription()).isEqualTo(response.description());
        assertThat(found.getDeadline()).isEqualTo(response.deadline());
        assertThat(found.getStatus()).isEqualTo(response.status());
        assertThat(found.getStartTime()).isEqualTo(response.startTime());
        assertThat(found.getFinishTime()).isEqualTo(response.finishTime());
        assertThat(found.getTimeSpent()).isEqualTo(response.timeSpent());
        assertThat(found.getEstimatedTime()).isEqualTo(createTaskDTO.estimatedTime());
        assertThat(found.getSuggestion()).isNull();
        assertThat(found.getUserId()).isEqualTo(user.getId());
    }
}