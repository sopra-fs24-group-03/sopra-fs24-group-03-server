package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class LobbyRepositoryIntegrationTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LobbyRepository lobbyRepository;

    @Test
    public void findById_success() {
        // Given
        Lobby lobby = new Lobby();


        entityManager.persist(lobby);
        entityManager.flush();

        // When

        Lobby found = lobbyRepository.findById(1L);

        // Then
        assertNotNull(found);
        assertEquals(found.getId(), lobby.getId());

    }

}
