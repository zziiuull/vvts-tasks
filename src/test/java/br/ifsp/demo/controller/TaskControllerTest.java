package br.ifsp.demo.controller;

import br.ifsp.demo.security.user.User;
import br.ifsp.demo.tasks.JpaTaskRepository;
import br.ifsp.demo.tasks.TaskEntity;
import br.ifsp.demo.tasks.dtos.CreateTaskDTO;
import br.ifsp.demo.tasks.dtos.ResponseTaskDTO;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.filter.log.LogDetail;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

class TaskControllerTest extends BaseApiIntegrationTest {
    @Autowired
    private JpaTaskRepository taskRepository;

    String password;
    User user;
    String token;
    String authorizationHeader;

    @BeforeEach
    void setUp() {
        password = "user123";
        user = registerUser(password);
        token = authenticate(user.getEmail(), password);
        authorizationHeader = "Bearer " + token;
    }

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
                final CreateTaskDTO createTaskDTO = EntityBuilder.createRandomCreateTaskDTO();

                final ResponseTaskDTO response =
                        given().contentType("application/json")
                                .port(RestAssured.port)
                                .body(createTaskDTO)
                                .header("Authorization", authorizationHeader)
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
                assertThat(found.getSuggestion()).isEqualTo(createTaskDTO.suggestion());
                assertThat(found.getUserId()).isEqualTo(user.getId());
            }
        }

        @Nested
        @DisplayName("400 bad request")
        class BadRequest {
            @Test
            @Tag("ApiTest")
            @Tag("IntegrationTest")
            @DisplayName("Should return a bad request status code when createTaskDTO title is blank")
            void shouldReturnABadRequestStatusCodeWhenCreateTaskDTOTitleIsBlank() {
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
                        .header("Authorization", authorizationHeader)
                        .when()
                        .post("api/v1/task/create")
                        .then()
                        .log()
                        .ifValidationFails(LogDetail.BODY)
                        .statusCode(HttpStatus.BAD_REQUEST.value());
            }

            @Test
            @Tag("ApiTest")
            @Tag("IntegrationTest")
            @DisplayName("Should return a bad request status code when createTaskDTO deadline is in past")
            void shouldReturnABadRequestStatusCodeWhenCreateTaskDTODeadlineIsInPast() {
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
                        .header("Authorization", authorizationHeader)
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
            @Tag("IntegrationTest")
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
    }

    @Nested
    @DisplayName("PUT /api/v1/task/edit/{id}")
    class EditTask {
        @Nested
        @DisplayName("200 ok")
        class Ok {
            @Test
            @Tag("ApiTest")
            @Tag("IntegrationTest")
            @DisplayName("Should return an ok status code when editing a task")
            void shouldReturnAnOkStatusCodeWhenEditingATask(){
                final CreateTaskDTO createTaskDTO = EntityBuilder.createRandomCreateTaskDTO();

                final ResponseTaskDTO createdTaskDTO =
                        given()
                                .contentType("application/json")
                                .port(RestAssured.port)
                                .body(createTaskDTO)
                                .header("Authorization", authorizationHeader)
                                .when()
                                .post("api/v1/task/create")
                                .then()
                                .log()
                                .ifValidationFails(LogDetail.BODY)
                                .statusCode(HttpStatus.CREATED.value())
                                .extract()
                                .as(ResponseTaskDTO.class);

                final CreateTaskDTO editTaskDTO = EntityBuilder.createRandomCreateTaskDTO();

                ResponseTaskDTO editedTaskDTO = given()
                        .contentType("application/json")
                        .port(RestAssured.port)
                        .body(editTaskDTO)
                        .when()
                        .put("api/v1/task/edit/" + createdTaskDTO.id())
                        .then()
                        .log()
                        .ifValidationFails(LogDetail.BODY)
                        .statusCode(HttpStatus.OK.value())
                        .extract()
                        .as(ResponseTaskDTO.class);

                assertThat(editedTaskDTO.id()).isEqualTo(createdTaskDTO.id());
                assertThat(editedTaskDTO.title()).isEqualTo(editTaskDTO.title());
                assertThat(editedTaskDTO.description()).isEqualTo(editTaskDTO.description());
                assertThat(editedTaskDTO.deadline()).isEqualTo(editTaskDTO.deadline());
                assertThat(editedTaskDTO.estimatedTime()).isEqualTo(editTaskDTO.estimatedTime());
                assertThat(editedTaskDTO.suggestion()).isEqualTo(editTaskDTO.suggestion());
            }
        }

        @Nested
        @DisplayName("400 bad request")
        class BadRequest {
            @Test
            @Tag("ApiTest")
            @Tag("IntegrationTest")
            @DisplayName("Should return a bad request status code when editing a task and title is blank")
            void shouldReturnABadRequestStatusCodeWhenEditingATaskAndTitleIsBlank() {
                final CreateTaskDTO createTaskDTO = EntityBuilder.createRandomCreateTaskDTO();

                final ResponseTaskDTO response =
                        given().contentType("application/json")
                                .port(RestAssured.port)
                                .body(createTaskDTO)
                                .header("Authorization", authorizationHeader)
                                .when()
                                .post("api/v1/task/create")
                                .then()
                                .log()
                                .ifValidationFails(LogDetail.BODY)
                                .statusCode(HttpStatus.CREATED.value())
                                .extract()
                                .as(ResponseTaskDTO.class);

                final CreateTaskDTO invalidCreateTaskDTO = new CreateTaskDTO(
                        "",
                        "Descrição",
                        LocalDateTime.now().plusMinutes(10),
                        10L,
                        "Sugestão"
                );

                given()
                        .contentType("application/json")
                        .port(RestAssured.port)
                        .body(invalidCreateTaskDTO)
                        .header("Authorization", authorizationHeader)
                        .when()
                        .put("api/v1/task/edit/" + response.id())
                        .then()
                        .log()
                        .ifValidationFails(LogDetail.BODY)
                        .statusCode(HttpStatus.BAD_REQUEST.value());
            }

            @Test
            @Tag("ApiTest")
            @Tag("IntegrationTest")
            @DisplayName("Should return a bad request status code when editing a task and deadline is in past")
            void shouldReturnABadRequestStatusCodeWhenEditingATaskAndDeadlineIsInPast() {
                final CreateTaskDTO createTaskDTO = EntityBuilder.createRandomCreateTaskDTO();

                final ResponseTaskDTO response =
                        given().contentType("application/json")
                                .port(RestAssured.port)
                                .body(createTaskDTO)
                                .header("Authorization", authorizationHeader)
                                .when()
                                .post("api/v1/task/create")
                                .then()
                                .log()
                                .ifValidationFails(LogDetail.BODY)
                                .statusCode(HttpStatus.CREATED.value())
                                .extract()
                                .as(ResponseTaskDTO.class);

                final CreateTaskDTO invalidCreateTaskDTO = new CreateTaskDTO(
                        "Título",
                        "Descrição",
                        LocalDateTime.now().minusMinutes(10),
                        10L,
                        "Sugestão"
                );

                given()
                        .contentType("application/json")
                        .port(RestAssured.port)
                        .body(invalidCreateTaskDTO)
                        .header("Authorization", authorizationHeader)
                        .when()
                        .put("api/v1/task/edit/" + response.id())
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
            @Tag("IntegrationTest")
            @DisplayName("Should return an unauthorized status code when editing a task and user is unauthorized")
            void shouldReturnAnUnauthorizedStatusCodeWhenEditingATaskAndUserIsUnauthorized() {
                final CreateTaskDTO createTaskDTO = EntityBuilder.createRandomCreateTaskDTO();

                final ResponseTaskDTO response =
                        given().contentType("application/json")
                                .port(RestAssured.port)
                                .body(createTaskDTO)
                                .header("Authorization", authorizationHeader)
                                .when()
                                .post("api/v1/task/create")
                                .then()
                                .log()
                                .ifValidationFails(LogDetail.BODY)
                                .statusCode(HttpStatus.CREATED.value())
                                .extract()
                                .as(ResponseTaskDTO.class);

                final CreateTaskDTO editTaskDTO = EntityBuilder.createRandomCreateTaskDTO();

                given().contentType("application/json")
                        .port(RestAssured.port)
                        .body(editTaskDTO)
                        .when()
                        .put("api/v1/task/edit")
                        .then()
                        .log()
                        .ifValidationFails(LogDetail.BODY)
                        .statusCode(HttpStatus.UNAUTHORIZED.value());
            }
        }

        @Nested
        @DisplayName("404 not found")
        class NotFound {
            @Test
            @Tag("ApiTest")
            @Tag("IntegrationTest")
            @DisplayName("Should return a not found status code when editing a non existing task")
            void shouldReturnANotFoundStatusCodeWhenEditingANonExistingTask() {
                final CreateTaskDTO createTaskDTO = EntityBuilder.createRandomCreateTaskDTO();

                given()
                        .contentType("application/json")
                        .port(RestAssured.port)
                        .body(createTaskDTO)
                        .header("Authorization", authorizationHeader)
                        .when()
                        .put("api/v1/task/edit/" + UUID.randomUUID())
                        .then()
                        .log()
                        .ifValidationFails(LogDetail.BODY)
                        .statusCode(HttpStatus.NOT_FOUND.value());
            }
        }
    }

    @Nested
    @DisplayName("GET /api/v1/task/get-all")
    class GetAllTask {
        @Nested
        @DisplayName("200 ok")
        class Ok {
            @Test
            @Tag("ApiTest")
            @Tag("IntegrationTest")
            @DisplayName("Should return an ok status code when retrieving all tasks")
            void shouldReturnAnOkStatusCodeWhenRetrievingAllTasks(){
                final CreateTaskDTO createTaskDTO = EntityBuilder.createRandomCreateTaskDTO();

                final ResponseTaskDTO response =
                    given()
                        .contentType("application/json")
                        .port(RestAssured.port)
                        .body(createTaskDTO)
                        .header("Authorization", authorizationHeader)
                    .when()
                        .post("api/v1/task/create")
                    .then()
                        .log()
                        .ifValidationFails(LogDetail.BODY)
                        .statusCode(HttpStatus.CREATED.value())
                        .extract()
                        .as(ResponseTaskDTO.class);

                final List<ResponseTaskDTO> allTasks =
                    given()
                        .port(RestAssured.port)
                        .header("Authorization", authorizationHeader)
                    .when()
                        .get("api/v1/task/get-all")
                    .then()
                        .log()
                        .ifValidationFails(LogDetail.BODY)
                        .statusCode(HttpStatus.OK.value())
                        .extract()
                        .as(new TypeRef<>() {
                        });

                assertThat(allTasks).hasSize(1);

                ResponseTaskDTO found = allTasks.getFirst();

                assertThat(found.id()).isEqualTo(response.id());
                assertThat(found.title()).isEqualTo(response.title());
                assertThat(found.description()).isEqualTo(response.description());
                assertThat(found.deadline()).isEqualTo(response.deadline());
                assertThat(found.status()).isEqualTo(response.status());
                assertThat(found.startTime()).isEqualTo(response.startTime());
                assertThat(found.finishTime()).isEqualTo(response.finishTime());
                assertThat(found.timeSpent()).isEqualTo(response.timeSpent());
                assertThat(found.estimatedTime()).isEqualTo(createTaskDTO.estimatedTime());
//                assertThat(found.suggestion()).isEqualTo(createTaskDTO.suggestion());
                assertThat(found.suggestion()).isNull();
                assertThat(found.userId()).isEqualTo(user.getId());

            }
        }

        @Nested
        @DisplayName("401 unauthorized")
        class Unauthorized {
            @Test
            @Tag("ApiTest")
            @Tag("IntegrationTest")
            @DisplayName("Should return an unauthorized status code when retrieving all tasks user is unauthorized")
            void shouldReturnAnUnauthorizedStatusCodeWhenRetrievingAllTasksAndUserIsUnauthorized() {
                given().contentType("application/json")
                        .port(RestAssured.port)
                        .when()
                        .get("api/v1/task/get-all")
                        .then()
                        .log()
                        .ifValidationFails(LogDetail.BODY)
                        .statusCode(HttpStatus.UNAUTHORIZED.value());
            }
        }
    }

    @Nested
    @DisplayName("GET /api/v1/task/get/{id}")
    class GetTask {
        @Nested
        @DisplayName("200 ok")
        class Ok {
            @Test
            @Tag("ApiTest")
            @Tag("IntegrationTest")
            @DisplayName("Should return an ok status code when retrieving a task")
            void shouldReturnAnOkStatusCodeWhenRetrievingATask(){
                final CreateTaskDTO createTaskDTO = EntityBuilder.createRandomCreateTaskDTO();

                final ResponseTaskDTO response =
                    given()
                        .contentType("application/json")
                        .port(RestAssured.port)
                        .body(createTaskDTO)
                        .header("Authorization", authorizationHeader)
                    .when()
                        .post("api/v1/task/create")
                    .then()
                        .log()
                        .ifValidationFails(LogDetail.BODY)
                        .statusCode(HttpStatus.CREATED.value())
                        .extract()
                        .as(ResponseTaskDTO.class);

                final ResponseTaskDTO found =
                    given()
                        .port(RestAssured.port)
                        .header("Authorization", authorizationHeader)
                    .when()
                        .get("api/v1/task/get/" + response.id())
                    .then()
                        .log()
                        .ifValidationFails(LogDetail.BODY)
                        .statusCode(HttpStatus.OK.value())
                        .extract()
                        .as(ResponseTaskDTO.class);

                assertThat(found.id()).isEqualTo(response.id());
                assertThat(found.title()).isEqualTo(response.title());
                assertThat(found.description()).isEqualTo(response.description());
                assertThat(found.deadline()).isEqualTo(response.deadline());
                assertThat(found.status()).isEqualTo(response.status());
                assertThat(found.startTime()).isEqualTo(response.startTime());
                assertThat(found.finishTime()).isEqualTo(response.finishTime());
                assertThat(found.timeSpent()).isEqualTo(response.timeSpent());
                assertThat(found.estimatedTime()).isEqualTo(createTaskDTO.estimatedTime());
//                assertThat(found.suggestion()).isEqualTo(createTaskDTO.suggestion());
                assertThat(found.suggestion()).isNull();
                assertThat(found.userId()).isEqualTo(user.getId());
            }
        }

        @Nested
        @DisplayName("401 unauthorized")
        class Unauthorized {
            @Test
            @Tag("ApiTest")
            @Tag("IntegrationTest")
            @DisplayName("Should return an unauthorized status code when retrieving a task and user is unauthorized")
            void shouldReturnAnUnauthorizedStatusCodeWhenRetrievingATaskAndUserIsUnauthorized() {
                given()
                    .contentType("application/json")
                    .port(RestAssured.port)
                .when()
                    .get("api/v1/task/get/" + UUID.randomUUID())
                .then()
                    .log()
                    .ifValidationFails(LogDetail.BODY)
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
            }
        }

        @Nested
        @DisplayName("404 not found")
        class NotFound {
            @Test
            @Tag("ApiTest")
            @Tag("IntegrationTest")
            @DisplayName("Should return a not found status code when retrieving a non existing task")
            void shouldReturnANotFoundStatusCodeWhenRetrievingANonExistingTask() {
                final CreateTaskDTO createTaskDTO = EntityBuilder.createRandomCreateTaskDTO();

                given()
                        .contentType("application/json")
                        .port(RestAssured.port)
                        .body(createTaskDTO)
                        .header("Authorization", authorizationHeader)
                        .when()
                        .get("api/v1/task/get/" + UUID.randomUUID())
                        .then()
                        .log()
                        .ifValidationFails(LogDetail.BODY)
                        .statusCode(HttpStatus.NOT_FOUND.value());
            }
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/task/delete/{id}")
    class DeleteTest {
        @Nested
        @DisplayName("204 no content")
        class Ok {
            @Test
            @Tag("ApiTest")
            @Tag("IntegrationTest")
            @DisplayName("Should return a no content status code when deleting a task")
            void shouldReturnANoContentStatusCodeWhenDeletingATask() {
                final CreateTaskDTO createTaskDTO = EntityBuilder.createRandomCreateTaskDTO();

                final ResponseTaskDTO response =
                        given()
                                .contentType("application/json")
                                .port(RestAssured.port)
                                .body(createTaskDTO)
                                .header("Authorization", authorizationHeader)
                                .when()
                                .post("api/v1/task/create")
                                .then()
                                .log()
                                .ifValidationFails(LogDetail.BODY)
                                .statusCode(HttpStatus.CREATED.value())
                                .extract()
                                .as(ResponseTaskDTO.class);

                given()
                .port(RestAssured.port)
                .header("Authorization", authorizationHeader)
                .when()
                .delete("api/v1/task/delete/" + response.id())
                .then()
                .log()
                .ifValidationFails(LogDetail.BODY)
                .statusCode(HttpStatus.NO_CONTENT.value());
            }
        }

        @Nested
        @DisplayName("401 unauthorized")
        class Unauthorized {
            @Test
            @Tag("ApiTest")
            @Tag("IntegrationTest")
            @DisplayName("Should return an unauthorized status code when deleting a task and user is unauthorized")
            void shouldReturnAnUnauthorizedStatusCodeWhenDeletingATaskAndUserIsUnauthorized() {
                given()
                    .contentType("application/json")
                    .port(RestAssured.port)
                .when()
                    .get("api/v1/task/delete/" + UUID.randomUUID())
                .then()
                    .log()
                    .ifValidationFails(LogDetail.BODY)
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
            }
        }

        @Nested
        @DisplayName("404 not found")
        class NotFound {
            @Test
            @Tag("ApiTest")
            @Tag("IntegrationTest")
            @DisplayName("Should return a not found status code when deleting a non existing task")
            void shouldReturnANotFoundStatusCodeWhenDeletingANonExistingTask() {
                final CreateTaskDTO createTaskDTO = EntityBuilder.createRandomCreateTaskDTO();

                given()
                    .contentType("application/json")
                    .port(RestAssured.port)
                    .body(createTaskDTO)
                    .header("Authorization", authorizationHeader)
                .when()
                    .delete("api/v1/task/delete/" + UUID.randomUUID())
                .then()
                    .log()
                    .ifValidationFails(LogDetail.BODY)
                    .statusCode(HttpStatus.NOT_FOUND.value());
            }
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
            CreateTaskDTO createTaskDTO = EntityBuilder.createRandomCreateTaskDTO();

            final ResponseTaskDTO response =
                    given().contentType("application/json")
                        .header("Authorization", authorizationHeader)
                        .body(createTaskDTO)
                    .when()
                        .post("/api/v1/task/create")
                    .then()
                        .statusCode(HttpStatus.CREATED.value())
                        .extract()
                        .as(ResponseTaskDTO.class);

            given().header("Authorization", authorizationHeader)
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
            UUID randomId = UUID.randomUUID();

            given().header("Authorization", authorizationHeader)
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
            CreateTaskDTO dto = EntityBuilder.createRandomCreateTaskDTO();

            ResponseTaskDTO createdTask =
                given().contentType("application/json")
                    .header("Authorization", authorizationHeader)
                    .body(dto)
                .when().post("/api/v1/task/create")
                .then().statusCode(HttpStatus.CREATED.value())
                .extract().as(ResponseTaskDTO.class);

            given().header("Authorization", authorizationHeader)
                .when().put("/api/v1/task/clock-in/" + createdTask.id())
                .then().statusCode(HttpStatus.NO_CONTENT.value());

            TaskEntity updated = taskRepository.findById(createdTask.id()).orElseThrow();
            assertThat(updated.getStartTime()).isNotNull();
        }

        @Test
        @Tag("ApiTest")
        @Tag("IntegrationTest")
        @DisplayName("Should return 404 when task does not exist")
        void shouldReturn404WhenTaskDoesNotExist() {
            UUID nonExistentId = UUID.randomUUID();

            given().header("Authorization", authorizationHeader)
                .when().put("/api/v1/task/clock-in/" + nonExistentId)
                .then().statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @Tag("ApiTest")
        @Tag("IntegrationTest")
        @DisplayName("Should return 403 if task does not belong to user")
        void shouldReturn403IfTaskDoesNotBelongToUser() {
            User userA = registerUser("123");
            String tokenA = authenticate(userA.getEmail(), "123");

            User userB = registerUser("456");
            String tokenB = authenticate(userB.getEmail(), "456");

            ResponseTaskDTO taskFromA =
                given().contentType("application/json")
                    .header("Authorization", "Bearer " + tokenA)
                    .body(EntityBuilder.createRandomCreateTaskDTO())
                .when().post("/api/v1/task/create")
                .then().statusCode(HttpStatus.CREATED.value())
                .extract().as(ResponseTaskDTO.class);

            given().header("Authorization", "Bearer " + tokenB)
                .when().put("/api/v1/task/clock-in/" + taskFromA.id())
                .then().statusCode(HttpStatus.FORBIDDEN.value());
        }
    }

    @Nested
    @DisplayName("PUT /clock-out/{id}")
    class ClockOutTests {

        @Test
        @Tag("ApiTest")
        @Tag("IntegrationTest")
        @DisplayName("Should clock-out task and return 204")
        void shouldClockOutTaskAndReturn204() {
            CreateTaskDTO dto = EntityBuilder.createRandomCreateTaskDTO();
            ResponseTaskDTO createdTask =
                given().contentType("application/json")
                    .header("Authorization", authorizationHeader)
                    .body(dto)
                .when().post("/api/v1/task/create")
                .then().statusCode(HttpStatus.CREATED.value())
                .extract().as(ResponseTaskDTO.class);

            given().header("Authorization", authorizationHeader)
                .when().put("/api/v1/task/clock-in/" + createdTask.id())
                .then().statusCode(HttpStatus.NO_CONTENT.value());

            given().header("Authorization", authorizationHeader)
                .when().put("/api/v1/task/clock-out/" + createdTask.id())
                .then().statusCode(HttpStatus.NO_CONTENT.value());

            TaskEntity updated = taskRepository.findById(createdTask.id()).orElseThrow();
            assertThat(updated.getFinishTime()).isNotNull();
        }

        @Test
        @Tag("ApiTest")
        @Tag("IntegrationTest")
        @DisplayName("Should return 404 if task not found")
        void shouldReturn404IfTaskNotFound() {
            UUID fakeId = UUID.randomUUID();

            given().header("Authorization", authorizationHeader)
                .when().put("/api/v1/task/clock-out/" + fakeId)
                .then().statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @Tag("ApiTest")
        @Tag("IntegrationTest")
        @DisplayName("Should return 403 if task does not belong to user when clock-out")
        void shouldReturn403IfTaskDoesNotBelongToUserWhenClockOut() {
            User userA = registerUser("123");
            String tokenA = authenticate(userA.getEmail(), "123");

            User userB = registerUser("456");
            String tokenB = authenticate(userB.getEmail(), "456");

            ResponseTaskDTO taskCreated =
                given().contentType("application/json")
                    .header("Authorization", "Bearer " + tokenA)
                    .body(EntityBuilder.createRandomCreateTaskDTO())
                    .when().post("/api/v1/task/create")
                    .then().statusCode(HttpStatus.CREATED.value())
                    .extract().as(ResponseTaskDTO.class);

            given().header("Authorization", "Bearer " + tokenA)
                .when().put("/api/v1/task/clock-in/" + taskCreated.id())
                .then().statusCode(HttpStatus.NO_CONTENT.value());

            given().header("Authorization", "Bearer " + tokenB)
                .when().put("/api/v1/task/clock-out/" + taskCreated.id())
                .then().statusCode(HttpStatus.FORBIDDEN.value());
        }
    }

    @Nested
    @DisplayName("GET /spent-time/{id}")
    class SpentTimeTests {
        @Test
        @Tag("ApiTest")
        @Tag("IntegrationTest")
        @DisplayName("Should return spent time of task with 200")
        void shouldReturnSpentTimeOfTaskWith200() {
            CreateTaskDTO dto = EntityBuilder.createRandomCreateTaskDTO();
            ResponseTaskDTO createdTask =
                given().contentType("application/json")
                    .header("Authorization", authorizationHeader)
                    .body(dto)
                    .when().post("/api/v1/task/create")
                    .then().statusCode(HttpStatus.CREATED.value())
                    .extract().as(ResponseTaskDTO.class);

            given().header("Authorization", authorizationHeader)
                .when().put("/api/v1/task/clock-in/" + createdTask.id())
                .then().statusCode(HttpStatus.NO_CONTENT.value());

            given().header("Authorization", authorizationHeader)
                    .when().put("/api/v1/task/clock-out/" + createdTask.id())
                    .then().statusCode(HttpStatus.NO_CONTENT.value());

            final Integer timeSpent =
                    given().header("Authorization", authorizationHeader)
                            .when().get("/api/v1/task/spent-time/" + createdTask.id())
                            .then()
                            .statusCode(HttpStatus.OK.value())
                            .extract()
                            .jsonPath().getInt("status");

            TaskEntity task = taskRepository.findById(createdTask.id()).orElseThrow();
            assertThat(timeSpent).isEqualTo(task.getTimeSpent().intValue());
        }

        @Test
        @Tag("ApiTest")
        @Tag("IntegrationTest")
        @DisplayName("Should return not found if task does not exist")
        void shouldReturnNotFoundIfTaskDoesNotExist() {
            UUID invalidId = UUID.randomUUID();

            given().header("Authorization", authorizationHeader)
                    .when().get("/api/v1/task/spent-time/" + invalidId)
                    .then().statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @Tag("ApiTest")
        @Tag("IntegrationTest")
        @DisplayName("Should return forbidden if user does not own task")
        void shouldReturnForbiddenIfUserDoesNotOwnTask() {
            User userA = registerUser("passA");
            String tokenA = authenticate(userA.getEmail(), "passA");

            User userB = registerUser("passB");
            String tokenB = authenticate(userB.getEmail(), "passB");

            ResponseTaskDTO task =
                given().contentType("application/json")
                    .header("Authorization", "Bearer" + tokenA)
                    .body(EntityBuilder.createRandomCreateTaskDTO())
                    .when().post("/api/v1/task/create")
                    .then().statusCode(HttpStatus.CREATED.value())
                    .extract().as(ResponseTaskDTO.class);

            given().header("Authorization", "Bearer " + tokenB)
                .when().get("/api/v1/task/spent-time/" + task.id())
                .then().statusCode(HttpStatus.FORBIDDEN.value());
        }
    }

    @Nested
    @DisplayName("GET /check-time-exceeded/{id}")
    class CheckTimeExceededTests {
        @Nested
        @DisplayName("200 OK")
        class Ok {
            @Test
            @Tag("ApiTest")
            @Tag("IntegrationTest")
            @DisplayName("should return false if time is not exceeded")
            void shouldReturnFalseIfTimeIsNotExceeded() {
                CreateTaskDTO taskDTO = EntityBuilder.createRandomCreateTaskDTO();

                ResponseTaskDTO task =
                        given()
                            .contentType("application/json")
                            .header("Authorization", authorizationHeader)
                            .body(taskDTO)
                        .when()
                            .post("/api/v1/task/create")
                        .then()
                            .statusCode(HttpStatus.CREATED.value())
                            .extract().as(ResponseTaskDTO.class);

                Map response = given()
                    .header("Authorization", authorizationHeader)
                .when()
                    .get("/api/v1/task/check-time-exceeded/" + task.id())
                .then()
                    .statusCode(HttpStatus.OK.value())
                        .extract().as(Map.class);

                assertThat(response.get("status")).isEqualTo(false);
            }
        }

        @Nested
        @DisplayName("404 not found")
        class NotFound {
            @Test
            @Tag("ApiTest")
            @Tag("IntegrationTest")
            @DisplayName("should return not found status code when checking a non existent task")
            void shouldReturnNotFoundStatusCodeWhenCheckingANonExistentTask() {
                given()
                    .port(RestAssured.port)
                    .header("Authorization", authorizationHeader)
                .when()
                    .get("/api/v1/task/check-time-exceeded/" + UUID.randomUUID())
                .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
            }
        }
    }

    @Nested
    @DisplayName("GET /notify-time-exceeded/{id}")
    class CheckAndNotifyTimeExceededTests {
        @Nested
        @DisplayName("200 OK")
        class Ok {
            @Test
            @Tag("ApiTest")
            @Tag("IntegrationTest")
            @DisplayName("should notify if time is exceeded")
            void shouldNotifyIfTimeIsNotExceeded() {
                CreateTaskDTO taskDTO = EntityBuilder.createRandomCreateTaskDTO();

                ResponseTaskDTO task =
                        given()
                                .contentType("application/json")
                                .header("Authorization", authorizationHeader)
                                .body(taskDTO)
                                .when()
                                .post("/api/v1/task/create")
                                .then()
                                .statusCode(HttpStatus.CREATED.value())
                                .extract().as(ResponseTaskDTO.class);

                given()
                        .header("Authorization", authorizationHeader)
                        .when()
                        .put("/api/v1/task/clock-in/" + task.id())
                        .then()
                        .statusCode(HttpStatus.NO_CONTENT.value());

                Map response = given()
                        .header("Authorization", authorizationHeader)
                        .when()
                        .get("/api/v1/task/notify-time-exceeded/" + task.id())
                        .then()
                        .statusCode(HttpStatus.OK.value())
                        .extract().as(Map.class);

                assertThat(response.get("status")).isEqualTo("Time exceeded! Please register the clock-out.");
            }
        }

        @Nested
        @DisplayName("404 not found")
        class NotFound {
            @Test
            @Tag("ApiTest")
            @Tag("IntegrationTest")
            @DisplayName("should return not found status code when checking a non existent task")
            void shouldReturnNotFoundStatusCodeWhenCheckingANonExistentTask() {
                given()
                        .port(RestAssured.port)
                        .header("Authorization", authorizationHeader)
                        .when()
                        .get("/api/v1/task/notify-time-exceeded/" + UUID.randomUUID())
                        .then()
                        .statusCode(HttpStatus.NOT_FOUND.value());
            }
        }
    }

    @Nested
    @DisplayName("GET /clock-out-forgotten/{id}")
    class CheckForForgotten {
        @Nested
        @DisplayName("200 OK")
        class Ok {
            @Test
            @Tag("ApiTest")
            @Tag("IntegrationTest")
            @DisplayName("should check if clock out was forgotten")
            void shouldCheckIfClockOutWasForgotten() {
                CreateTaskDTO taskDTO = EntityBuilder.createRandomCreateTaskDTO();

                ResponseTaskDTO task =
                        given()
                                .contentType("application/json")
                                .header("Authorization", authorizationHeader)
                                .body(taskDTO)
                                .when()
                                .post("/api/v1/task/create")
                                .then()
                                .statusCode(HttpStatus.CREATED.value())
                                .extract().as(ResponseTaskDTO.class);

                given()
                        .header("Authorization", authorizationHeader)
                        .when()
                        .put("/api/v1/task/clock-in/" + task.id())
                        .then()
                        .statusCode(HttpStatus.NO_CONTENT.value());

                Map response = given()
                        .header("Authorization", authorizationHeader)
                        .when()
                        .get("/api/v1/task/clock-out-forgotten/" + task.id())
                        .then()
                        .statusCode(HttpStatus.OK.value())
                        .extract().as(Map.class);

                assertThat(response.get("status")).isEqualTo("Task is within the estimated time or clock-out is already registered.");
            }
        }

        @Nested
        @DisplayName("404 not found")
        class NotFound {
            @Test
            @Tag("ApiTest")
            @Tag("IntegrationTest")
            @DisplayName("should return not found status code when checking a non existent task")
            void shouldReturnNotFoundStatusCodeWhenCheckingANonExistentTask() {
                given()
                        .port(RestAssured.port)
                        .header("Authorization", authorizationHeader)
                        .when()
                        .get("/api/v1/task/clock-out-forgotten/" + UUID.randomUUID())
                        .then()
                        .statusCode(HttpStatus.NOT_FOUND.value());
            }
        }
    }

    @Nested
    @DisplayName("GET /clock-out-forgotten/{id}")
    class CheckForForgottenCompletedTask {
        @Nested
        @DisplayName("200 OK")
        class Ok {
            @Test
            @Tag("ApiTest")
            @Tag("IntegrationTest")
            @DisplayName("should check if clock out was forgotten")
            void shouldCheckIfClockOutWasForgotten() {
                CreateTaskDTO taskDTO = EntityBuilder.createRandomCreateTaskDTO();

                ResponseTaskDTO task =
                        given()
                                .contentType("application/json")
                                .header("Authorization", authorizationHeader)
                                .body(taskDTO)
                                .when()
                                .post("/api/v1/task/create")
                                .then()
                                .statusCode(HttpStatus.CREATED.value())
                                .extract().as(ResponseTaskDTO.class);

                given()
                        .header("Authorization", authorizationHeader)
                        .when()
                        .put("/api/v1/task/clock-in/" + task.id())
                        .then()
                        .statusCode(HttpStatus.NO_CONTENT.value());

                given()
                        .header("Authorization", authorizationHeader)
                        .when()
                        .put("/api/v1/task/clock-out/" + task.id())
                        .then()
                        .statusCode(HttpStatus.NO_CONTENT.value());

                Map response = given()
                        .header("Authorization", authorizationHeader)
                        .when()
                        .get("/api/v1/task/clock-out-forgotten-completed-task/" + task.id())
                        .then()
                        .statusCode(HttpStatus.OK.value())
                        .extract().as(Map.class);

                assertThat(response.get("status")).isEqualTo("Clock-out is no longer necessary as the task is already completed.");
            }
        }


    }
}
