package br.ifsp.demo.controller;

import br.ifsp.demo.security.user.User;
import br.ifsp.demo.tasks.JpaTaskRepository;
import br.ifsp.demo.tasks.TaskEntity;
import br.ifsp.demo.tasks.dtos.CreateTaskDTO;
import br.ifsp.demo.tasks.dtos.ResponseTaskDTO;
import io.restassured.RestAssured;
import io.restassured.filter.log.LogDetail;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

class TaskControllerTest extends BaseApiIntegrationTest {
    @Autowired
    private JpaTaskRepository taskRepository;

    @AfterEach
    public void tearDown (){
        taskRepository.deleteAll();
    }

    @Nested
    @DisplayName("For valid tests")
    class ValidTest {
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

        @Nested
        @DisplayName("PUT /mark-completed/{id}")
        class MarkCompletedTests {

            @Test
            @Tag("ApiTest")
            @Tag("IntegrationTest")
            @DisplayName("Should mark a task as completed and return 204")
            void shouldMarkATaskAsCompletedAndReturn204() {

                String password = "abc123";
                User user = registerUser(password);
                String token = authenticate(user.getEmail(), password);

                CreateTaskDTO createTaskDTO = EntityBuilder.createRandomCreateTaskDTO();

                final ResponseTaskDTO response =
                        given().contentType("application/json")
                                .header("Authorization", "Bearer " + token)
                                .body(createTaskDTO)
                        .when()
                                .post("/api/v1/task/create")
                        .then()
                                .statusCode(HttpStatus.CREATED.value())
                                .extract()
                                .as(ResponseTaskDTO.class);

                given().header("Authorization", "Bearer" + token)
                        .when()
                        .put("/api/v1/task/mark-completed/" + response.id())
                        .then()
                        .statusCode(HttpStatus.NO_CONTENT.value());

                TaskEntity updated = taskRepository.findById(response.id()).orElseThrow();
                assertThat(updated.getStatus().name()).isEqualTo("COMPLETED");
            }

            @Test
            @Tag("ApiTest")
            @Tag("IntegrationTest")
            @DisplayName("Should return 404 if task does not exist")
            void shouldReturn404IfTaskDoesNotExist() {
                String password = "pass123";
                User user = registerUser(password);
                String token = authenticate(user.getEmail(), password);

                UUID randomId = UUID.randomUUID();

                given().header("Authorization", "Bearer" + token)
                        .when()
                        .put("/api/v1/task/mark-completed/" + randomId)
                        .then()
                        .statusCode(HttpStatus.NOT_FOUND.value());
            }
        }
    }
}