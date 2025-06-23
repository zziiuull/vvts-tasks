package br.ifsp.demo.repository;

import br.ifsp.demo.tasks.JpaTaskRepository;
import br.ifsp.demo.tasks.TaskEntity;
import br.ifsp.demo.repository.util.TaskBuilder;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JpaTaskRepositoryTest {

    @Autowired
    private JpaTaskRepository taskRepository;

    private UUID userA;
    private UUID userB;
    private TaskEntity taskA;


    @BeforeEach
    void setup() {
        taskRepository.deleteAll();

        userA = UUID.randomUUID();
        userB = UUID.randomUUID();

        taskA = TaskBuilder.defaultTask(userA);
        taskRepository.save(taskA);
    }

    @Test
    @Tag("PersistenceTest")
    @Tag("IntegrationTest")
    @DisplayName("Should return all tasks for given user")
    void shouldReturnAllTasksForGivenUser() {
        List<TaskEntity> result = taskRepository.findAllByUserId(userA);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getUserId()).isEqualTo(userA);
    }

    @Test
    @Tag("PersistenceTest")
    @Tag("IntegrationTest")
    @DisplayName("Should return empty list for user without tasks")
    void shouldReturnEmptyListForUserWithoutTasks() {
        List<TaskEntity> result = taskRepository.findAllByUserId(userB);
        assertThat(result).isEmpty();
    }

    @Test
    @Tag("PersistenceTest")
    @Tag("IntegrationTest")
    @DisplayName("Should find task by id and user id")
    void shouldFindTaskByIdAndUserId() {
        Optional<TaskEntity> result = taskRepository.findByIdAndUserId(taskA.getId(), userA);

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo(taskA.getTitle());
    }

    @Test
    @Tag("PersistenceTest")
    @Tag("IntegrationTest")
    @DisplayName("Should return empty when task does not belong to user")
    void shouldReturnEmptyWhenTaskDoesNotBelongToUser() {
        Optional<TaskEntity> result = taskRepository.findByIdAndUserId(taskA.getId(), userB);
        assertThat(result).isEmpty();
    }

}
