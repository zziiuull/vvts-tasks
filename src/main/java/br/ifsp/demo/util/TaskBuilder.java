package br.ifsp.demo.util;

import br.ifsp.demo.tasks.TaskEntity;
import br.ifsp.demo.tasks.TaskStatus;
import com.github.javafaker.Faker;

import java.time.LocalDateTime;
import java.util.UUID;

public class TaskBuilder {

    private static final Faker faker = new Faker();

    public static TaskEntity defaultTask(UUID userId) {
        return TaskEntity.builder()
                .id(UUID.randomUUID())
                .title("Estudar VVTS")
                .description("Revisar conteúdo de teste")
                .deadline(LocalDateTime.now().plusDays(3))
                .status(TaskStatus.PENDING)
                .userId(userId)
                .build();
    }

}
