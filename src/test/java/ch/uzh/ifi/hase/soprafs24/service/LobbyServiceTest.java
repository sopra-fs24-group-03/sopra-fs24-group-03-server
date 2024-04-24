package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LobbyServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private GameService gameService;

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



    }

    @Test
    public void testJoinLobbyById_Success() {

        // Call the method under test
        when(userService.getUserByToken(anyString())).thenReturn(user);
        when(lobbyRepository.findById(anyLong())).thenReturn(lobby);
        Lobby joinedLobby = lobbyService.joinLobbyById(2L, "validToken");

        // Verify the outcome
        assertNotNull(joinedLobby);
        assertTrue(joinedLobby.getLobbyusers().contains(user));
    }

    @Test
    public void testJoinLobbyById_LobbyFull() {
        when(userService.getUserByToken(anyString())).thenReturn(user);
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
        when(userService.getUserByToken(anyString())).thenReturn(user);


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
        when(userService.getUserByToken(anyString())).thenReturn(user);


        // Call the method under test
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> lobbyService.removeUserFromLobby("validToken"));

        // Verify that the correct exception is thrown
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("User is not in a Lobby!", exception.getReason());
    }

    @Test
    public void testCreateGame_Success(){

        //Setup
        User user2 = new User();


        user2.setUsername("testUser2");
        user2.setToken("token2");
        user2.setPassword("Password");



        user2.setLobby(lobby);
        lobby.addUserToLobby(user2);

        when(lobbyRepository.findById(anyLong())).thenReturn(lobby);

        //method call
        Game createdGame = lobbyService.startGame(user.getToken(), lobby.getId());
        assertNotNull(createdGame);
//        assertEquals(lobby, createdGame.getLobby());
    }

    @Test
    public void testCreateGame_notEnoughPlayers(){

        //Setup
        when(lobbyRepository.findById(anyLong())).thenReturn(lobby);

        //method call
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                ()->lobbyService.startGame(user.getToken(), lobby.getId()));


        // Verify that the correct exception is thrown
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Not enough players", exception.getReason());
    }

    @Test
    public void testCreateGame_notLeader(){

        //Setup
        User user2 = new User();


        user2.setUsername("testUser2");
        user2.setToken("token2");
        user2.setPassword("Password");


        user2.setLobby(lobby);
        lobby.addUserToLobby(user2);


        when(lobbyRepository.findById(anyLong())).thenReturn(lobby);

        //method call
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                ()->lobbyService.startGame(user2.getToken(), lobby.getId()));


        // Verify that the correct exception is thrown
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("Only the lobby leader can do this!", exception.getReason());
    }

    @Test
    public void testCreateGame_GameAlreadyExists(){

        User user1 = new User();
        ArrayList<User> Userlist = new ArrayList<User>();
        Userlist.add(user1);
        //Setup
        lobby.createGame(Userlist);

        User user2 = new User();


        user2.setUsername("testUser2");
        user2.setToken("token2");
        user2.setPassword("Password");


        user2.setLobby(lobby);
        lobby.addUserToLobby(user2);


        when(lobbyRepository.findById(anyLong())).thenReturn(lobby);

        //method call
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                ()->lobbyService.startGame(user.getToken(), lobby.getId()));


        // Verify that the correct exception is thrown
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("lobby already has running game!", exception.getReason());
    }

    @Test
    public void testCreateGame_LeaderOutOfMoney(){

        //Setup

        User user2 = new User();
        User user3 = new User();

        user.setMoney(0);

        user2.setUsername("testUser2");
        user2.setToken("token2");
        user2.setPassword("Password");

        user3.setUsername("testUser3");
        user3.setToken("token3");
        user3.setPassword("Password");

        user2.setLobby(lobby);
        lobby.addUserToLobby(user2);

        user3.setLobby(lobby);
        lobby.addUserToLobby(user3);

        when(lobbyRepository.findById(anyLong())).thenReturn(lobby);

        //method call
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                ()->lobbyService.startGame(user.getToken(), lobby.getId()));


        // Verify that the correct exception is thrown
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("could not start game, lobby leader has insufficient money!", exception.getReason());
    }
}



