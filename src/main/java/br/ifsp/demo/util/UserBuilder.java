package br.ifsp.demo.util;

import br.ifsp.demo.security.user.Role;
import br.ifsp.demo.security.user.User;
import com.github.javafaker.Faker;

import java.util.UUID;

public class UserBuilder {

    private static final Faker faker = new Faker();

    public static User randomUser() {
        return User.builder()
                .id(UUID.randomUUID())
                .name(faker.name().firstName())
                .lastname(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .password("encoded-password")
                .role(Role.USER)
                .build();
    }
}
