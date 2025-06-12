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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

class TaskControllerTest extends BaseApiIntegrationTest {
    @Autowired
    private JpaTaskRepository taskRepository;

    @AfterEach
    public void tearDown(){
        taskRepository.deleteAll();
    }

    @Nested
    @DisplayName("POST /api/v1/task/create")
    class CreateTask {
        @Nested
        @DisplayName("201 created")
        class Created {
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

        @Nested
        @DisplayName("400 bad request")
        class BadRequest {
            @Test
            @Tag("ApiTest")
            @DisplayName("Should return a bad request status code when createTaskDTO title is blank")
            void shouldReturnABadRequestStatusCodeWhenCreateTaskDTOTitleIsBlank() {
                String password = "user123";
                User user = registerUser(password);
                final String token = authenticate(user.getEmail(), password);
                final CreateTaskDTO invalidCreateTaskDTO = new CreateTaskDTO(
                        "",
                        "Descrição",
                        LocalDateTime.now().plusMinutes(10),
                        10L,
                        "Sugestão"
                );


                given().contentType("application/json")
                    .port(RestAssured.port)
                    .body(invalidCreateTaskDTO)
                    .header("Authorization", "Bearer " + token)
                .when()
                    .post("api/v1/task/create")
                .then()
                    .log()
                    .ifValidationFails(LogDetail.BODY)
                    .statusCode(HttpStatus.BAD_REQUEST.value());
            }

            @Test
            @Tag("ApiTest")
            @DisplayName("Should return a bad request status code when createTaskDTO deadline is in past")
            void shouldReturnABadRequestStatusCodeWhenCreateTaskDTODeadlineIsInPast() {
                String password = "user123";
                User user = registerUser(password);
                final String token = authenticate(user.getEmail(), password);
                final CreateTaskDTO invalidCreateTaskDTO = new CreateTaskDTO(
                        "Title",
                        "Descrição",
                        LocalDateTime.now().minusMinutes(10),
                        10L,
                        "Sugestão"
                );

                given().contentType("application/json")
                        .port(RestAssured.port)
                        .body(invalidCreateTaskDTO)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .post("api/v1/task/create")
                        .then()
                        .log()
                        .ifValidationFails(LogDetail.BODY)
                        .statusCode(HttpStatus.BAD_REQUEST.value());
            }
        }

        @Nested
        @DisplayName("401 unauthorized")
        class Unauthorized {
            @Test
            @Tag("ApiTest")
            @DisplayName("Should return an unauthorized status code when user is unauthorized")
            void shouldReturnAnUnauthorizedStatusCodeWhenUserIsUnauthorized() {
                final CreateTaskDTO createTaskDTO = EntityBuilder.createRandomCreateTaskDTO();

                given().contentType("application/json")
                    .port(RestAssured.port)
                    .body(createTaskDTO)
                .when()
                    .post("api/v1/task/create")
                .then()
                    .log()
                    .ifValidationFails(LogDetail.BODY)
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
            }
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

                given().header("Authorization", "Bearer " + token)
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

                given().header("Authorization", "Bearer " + token)
                    .when()
                    .put("/api/v1/task/mark-completed/" + randomId)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
            }

            @Test
            @Tag("ApiTest")
            @Tag("IntegrationTest")
            @DisplayName("Should return 403 if task belongs to another user")
            void shouldReturn403IfNotOwner() {
                String passA = "123";
                User userA = registerUser(passA);
                String tokenA = authenticate(userA.getEmail(), passA);

                String passB = "456";
                User userB = registerUser(passB);
                String tokenB = authenticate(userB.getEmail(), passB);

                CreateTaskDTO dto = EntityBuilder.createRandomCreateTaskDTO();
                ResponseTaskDTO taskCreated =
                        given().contentType("application/json")
                            .header("Authorization", "Bearer " + tokenA)
                            .body(dto)
                        .when().post("/api/v1/task/create")
                        .then().statusCode(HttpStatus.CREATED.value())
                        .extract().as(ResponseTaskDTO.class);

                given().header("Authorization", "Bearer " + tokenB)
                    .when().put("/api/v1/task/mark-completed/" + taskCreated.id())
                    .then().statusCode(HttpStatus.FORBIDDEN.value());
            }
        }

        @Nested
        @DisplayName("PUT /clock-in/{id}")
        class ClockInTests {

            @Test
            @Tag("ApiTest")
            @Tag("IntegrationTest")
            @DisplayName("Should clock-in task and return 204")
            void shouldClockInTaskAndReturn204() {
                String password = "abc123";
                User user = registerUser(password);
                String token = authenticate(user.getEmail(), password);
                CreateTaskDTO dto = EntityBuilder.createRandomCreateTaskDTO();

                ResponseTaskDTO createdTask =
                    given().contentType("application/json")
                        .header("Authorization", "Bearer " + token)
                        .body(dto)
                    .when().post("api/v1/task/create")
                    .then().statusCode(HttpStatus.CREATED.value())
                    .extract().as(ResponseTaskDTO.class);

                given().header("Authorization", "Bearer " + token)
                    .when().put("api/v1/task/clock-in/" + createdTask.id())
                    .then().statusCode(HttpStatus.NO_CONTENT.value());

                TaskEntity updated = taskRepository.findById(createdTask.id()).orElseThrow();
                assertThat(updated.getStartTime()).isNotNull();
            }

            @Test
            @Tag("ApiTest")
            @Tag("IntegrationTest")
            @DisplayName("Should return 404 when task does not exist")
            void shouldReturn404WhenTaskDoesNotExist() {
                String password = "pass123";
                User user = registerUser(password);
                String token = authenticate(user.getEmail(), password);

                UUID nonExistentId = UUID.randomUUID();

                given().header("Authorization", "Bearer " + token)
                    .when().put("api/v1/task/clock-in/" + nonExistentId)
                    .then().statusCode(HttpStatus.NOT_FOUND.value());
            }
        }
    }
}