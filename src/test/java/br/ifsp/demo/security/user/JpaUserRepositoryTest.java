package br.ifsp.demo.security.user;

import br.ifsp.demo.util.UserBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JpaUserRepositoryTest {

    @Autowired
    private JpaUserRepository userRepository;

    private User user;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        user = UserBuilder.withEmail("carlos@ifsp.edu.br");
        userRepository.save(user);
    }

}