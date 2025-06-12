package br.ifsp.demo.tasks;

import br.ifsp.demo.util.TaskBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JpaTaskRepositoryTest {

    @Autowired
    private JpaTaskRepository taskRepository;

    @BeforeEach
    void setup() {
        taskRepository.deleteAll();

        UUID userA = UUID.randomUUID();
        UUID userB = UUID.randomUUID();

        TaskEntity taskA = TaskBuilder.defaultTask(userA);
        taskRepository.save(taskA);
    }


}