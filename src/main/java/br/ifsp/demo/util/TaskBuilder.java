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
                .title("Estudar VVTS")
                .description("Revisar conteúdo de teste")
                .deadline(LocalDateTime.now().plusDays(3))
                .status(TaskStatus.PENDING)
                .userId(userId)
                .build();
    }

    public static TaskEntity randomTask(UUID userId) {
        return TaskEntity.builder()
                .title(faker.lorem().sentence(3))
                .description(faker.lorem().paragraph())
                .deadline(LocalDateTime.now().plusDays(faker.number().numberBetween(1, 10)))
                .status(TaskStatus.PENDING)
                .userId(userId)
                .build();
    }

    public static TaskEntity completedTask(UUID userId) {
        return TaskEntity.builder()
                .title("Finalizado")
                .status(TaskStatus.COMPLETED)
                .deadline(LocalDateTime.now())
                .startTime(LocalDateTime.now().minusHours(2))
                .finishTime(LocalDateTime.now())
                .timeSpent(120L)
                .userId(userId)
                .build();
    }
}
