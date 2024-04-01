package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.service.LobbyService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LobbyServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LobbyRepository lobbyRepository;

    @InjectMocks
    private LobbyService lobbyService;


    private User user;
    private Lobby lobby;

    @BeforeEach
    public void setUp() {
        // Create a sample user
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setToken("validToken");

        // Create a sample lobby
        lobby = new Lobby();
        lobby.setId(2L);
        lobby.setLobbyLeader(user);
        lobby.addUserToLobby(user);

        // Mock userRepository behavior
        when(userRepository.findByToken(anyString())).thenReturn(user);



    }

    @Test
    public void testJoinLobbyById_Success() {
        // Call the method under test
        when(lobbyRepository.findById(anyLong())).thenReturn(lobby);
        Lobby joinedLobby = lobbyService.joinLobbyById(2L, "validToken");

        // Verify the outcome
        assertNotNull(joinedLobby);
        assertTrue(joinedLobby.getLobbyusers().contains(user));
    }

    @Test
    public void testJoinLobbyById_LobbyFull() {
        when(lobbyRepository.findById(anyLong())).thenReturn(lobby);
        // Add more users to fill the lobby
        for (int i = 0; i < 7; i++) {
            User newUser = new User();
            newUser.setId((long) (i + 2)); // IDs start from 2
            newUser.setUsername("user" + (i + 2));
            lobby.addUserToLobby(newUser);
        }

        // Call the method under test
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> lobbyService.joinLobbyById(2L, "validToken"));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Lobby is full!", exception.getReason());
    }
    @Test
    public void testRemoveUserFromLobby_Success() {
        // Mock userRepository behavior
        when(userRepository.findByToken(anyString())).thenReturn(user);

        // Set up the user's lobby
        user.setLobby(lobby);

        // Call the method under test
        lobbyService.removeUserFromLobby("validToken");

        // Verify that the user is removed from the lobby
        assertNull(user.getLobby());
        assertFalse(lobby.getLobbyusers().contains(user));
    }

    @Test
    public void testRemoveUserFromLobby_NotInLobby() {
        // Mock userRepository behavior
        when(userRepository.findByToken(anyString())).thenReturn(user);

        // Call the method under test
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> lobbyService.removeUserFromLobby("validToken"));

        // Verify that the correct exception is thrown
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("User is not in a Lobby!", exception.getReason());
    }


}
