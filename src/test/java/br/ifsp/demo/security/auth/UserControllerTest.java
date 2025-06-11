package br.ifsp.demo.security.auth;

import br.ifsp.demo.controller.BaseApiIntegrationTest;
import br.ifsp.demo.controller.EntityBuilder;
import br.ifsp.demo.security.user.User;
import io.restassured.filter.log.LogDetail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

class UserControllerTest extends BaseApiIntegrationTest {
    @Test
    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @DisplayName("Should register user")
    void shouldRegisterUser(){
        final User user = EntityBuilder.createRandomUser("123");

        given()
            .contentType("application/json").port(port).body(user)
        .when()
                .post("/api/v1/register")
        .then()
                .log()
                .ifValidationFails(LogDetail.BODY)
                .statusCode(201)
                .body("id", notNullValue());
    }

    @Test
    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @DisplayName("Should login")
    void shouldLoginWithValidCredentials() {
        final String plainTextPassword = "123password";
        final User user = registerUser(plainTextPassword);
        AuthRequest authRequest = new AuthRequest(user.getEmail(), plainTextPassword);
        given()
                .contentType("application/json")
                .port(port)
                .body(authRequest).
        when().
                post("/api/v1/authenticate").
        then()
                .log()
                .ifValidationFails(LogDetail.BODY)
                .statusCode(200)
                .body("token", notNullValue());
    }
}