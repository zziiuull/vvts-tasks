package br.ifsp.demo.repository;

import br.ifsp.demo.security.user.JpaUserRepository;
import br.ifsp.demo.security.user.User;
import br.ifsp.demo.util.UserBuilder;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Test
    @Tag("PersistenceTest")
    @Tag("IntegrationTest")
    @DisplayName("Should find user by email")
    void shouldFindUserByEmail() {
        Optional<User> found = userRepository.findByEmail("carlos@ifsp.edu.br");

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(user.getId());
        assertThat(found.get().getEmail()).isEqualTo("carlos@ifsp.edu.br");
    }

    @Test
    @Tag("PersistenceTest")
    @Tag("IntegrationTest")
    @DisplayName("Should return empty if email does not exist")
    void shouldReturnEmptyIfEmailDoesNotExist() {
        Optional<User> result = userRepository.findByEmail("notfound@ifsp.edu.br");

        assertThat(result).isEmpty();
    }

    @Test
    @Tag("PersistenceTest")
    @Tag("IntegrationTest")
    @DisplayName("Should save and retrieve user with specific Id")
    void shouldSaveAndRetrieveUserWithSpecificId() {
        UUID specificId = UUID.randomUUID();
        User customUser = UserBuilder.withIdAndEmail(specificId, "custom@ifsp.edu.br");
        userRepository.save(customUser);

        Optional<User> found = userRepository.findById(specificId);

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("custom@ifsp.edu.br");
    }
}