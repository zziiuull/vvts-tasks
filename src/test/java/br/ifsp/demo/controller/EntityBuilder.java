package br.ifsp.demo.controller;

import br.ifsp.demo.security.user.Role;
import br.ifsp.demo.security.user.User;
import br.ifsp.demo.tasks.dtos.CreateTaskDTO;
import com.github.javafaker.Faker;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class EntityBuilder {
    private static final Faker faker = new Faker();

    public static User createRandomUser(String password) {
        return User
                .builder()
                .id(UUID.randomUUID())
                .name(faker.name().firstName())
                .lastname(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .password(password)
                .role(Role.USER)
                .build();
    }

    public static CreateTaskDTO createRandomCreateTaskDTO() {
        Date futureDate = faker.date().future(10, TimeUnit.DAYS);
        LocalDateTime deadline = futureDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        long estimatedTime = faker.number().numberBetween(10, 60);

        return new CreateTaskDTO(
                faker.name().title(),
                faker.lorem().sentence(10),
                deadline,
                estimatedTime,
                faker.hipster().word()
        );
    }
}
