package br.ifsp.demo.tasks;

import br.ifsp.demo.util.TaskBuilder;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
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

}