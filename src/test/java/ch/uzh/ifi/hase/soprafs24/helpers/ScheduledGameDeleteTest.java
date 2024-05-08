package ch.uzh.ifi.hase.soprafs24.helpers;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.repository.LobbyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ScheduledGameDeleteTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private LobbyRepository lobbyRepository;

    @InjectMocks
    private ScheduledGameDelete scheduledGameDelete;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testDeleteGame() {
        // Given
        Game mockGame = mock(Game.class);
        when(mockGame.getId()).thenReturn(1L);

        Lobby mockLobby = mock(Lobby.class);
        when(lobbyRepository.findById(1L)).thenReturn(mockLobby);

        // When
        scheduledGameDelete.deleteGame(mockGame);

        // Then
        verify(lobbyRepository).findById(1L); // Verify that findLobby was called
        verify(mockLobby).setGameToNull();    // Verify that the game was set to null in the lobby
        verify(lobbyRepository).save(mockLobby); // Verify that the lobby was saved
        verify(gameRepository).delete(mockGame); // Verify that the game was deleted
        verify(gameRepository).flush(); // Verify that the repository was flushed
    }
}

