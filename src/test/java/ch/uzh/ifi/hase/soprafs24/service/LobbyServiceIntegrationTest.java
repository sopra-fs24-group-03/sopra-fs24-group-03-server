package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import static org.mockito.ArgumentMatchers.isA;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class LobbyServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Qualifier("lobbyRepository")
    @Autowired
    private LobbyRepository lobbyRepository;

    @Autowired
    private LobbyService lobbyService;

    private User user;

    @BeforeEach
    public void setUp() {
        lobbyRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testCreateLobby_Success() {
        //Setup
        User user = new User();
        user.setUsername("testUser");
        user.setToken("validToken");
        user.setPassword("Password");
        user.setStatus(UserStatus.ONLINE);
        user.setMoney(2000);
        userRepository.save(user);

        // Call the method under test
        Lobby createdLobby = lobbyService.createLobby("validToken");


        // Verify the outcome
        assertNotNull(createdLobby);
        assertNotNull(createdLobby.getId());
        assertNotNull(createdLobby.getLobbyLeader());
        assertEquals(user.getId(), createdLobby.getLobbyLeader().getId());
        assertEquals(user.getUsername(), createdLobby.getLobbyLeader().getUsername());

        //cleanup
        user.setLobby(null);
        userRepository.save(user);
        createdLobby.setLobbyLeader(null);
        createdLobby.removeUserFromLobby(user);
        lobbyRepository.delete(createdLobby);
        userRepository.delete(user);
    }

    @Test
    public void testCreateLobby_UserAlreadyInLobby() {
        // Create a lobby
        Lobby lobby = new Lobby();
        lobby = lobbyRepository.save(lobby);
        // Create a user
        User user = new User();
        user.setUsername("testUser");
        user.setToken("validToken");
        user.setPassword("Password");
        user.setStatus(UserStatus.ONLINE);
        user.setMoney(2000);
        user.setLobby(lobby);

        userRepository.save(user);

        // Verify that an exception is thrown
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> lobbyService.createLobby("validToken"));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());


        //cleanup
        user.setLobby(null);
        userRepository.save(user);
        lobby.setLobbyLeader(null);
        lobby.removeUserFromLobby(user);
        lobbyRepository.delete(lobby);
        userRepository.delete(user);


    }

    @Test
    public void testGetLobbyById_Success() {
        // Create a lobby
        Lobby lobby = new Lobby();
        lobby = lobbyRepository.save(lobby);
        // Create a user
        User user = new User();
        user.setUsername("testUser");
        user.setToken("validToken");
        user.setPassword("Password");
        user.setStatus(UserStatus.ONLINE);
        user.setMoney(2000);
        user.setLobby(lobby);

        userRepository.save(user);


        // Call the method under test
        Lobby retrievedLobby = lobbyService.getLobbyById(lobby.getId(), "validToken");

        // Verify the outcome
        assertNotNull(retrievedLobby);
        assertEquals(lobby.getId(), retrievedLobby.getId());

        //cleanup
        user.setLobby(null);
        userRepository.save(user);
        lobby.setLobbyLeader(null);
        lobby.removeUserFromLobby(user);
        lobbyRepository.delete(lobby);
        userRepository.delete(user);
    }

    @Test
    public void testGetLobbyById_Unauthorized() {
        // Create a lobby
        Lobby lobby = new Lobby();
        lobbyRepository.save(lobby);
        // Create a user (without adding him to lobby)
        User user = new User();
        user.setUsername("testUser");
        user.setToken("validToken");
        user.setPassword("Password");
        user.setStatus(UserStatus.ONLINE);
        user.setMoney(2000);

        userRepository.save(user);


        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> lobbyService.getLobbyById(lobby.getId(), "validToken"));
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());


        //cleanup
        user.setLobby(null);
        userRepository.save(user);
        lobby.setLobbyLeader(null);
        lobby.removeUserFromLobby(user);
        lobbyRepository.delete(lobby);
        userRepository.delete(user);
    }

    @Test
    public void testJoinLobbyById_NotFound() {

        User user = new User();
        user.setUsername("testUser");
        user.setToken("validToken");
        user.setPassword("Password");
        user.setStatus(UserStatus.ONLINE);
        user.setMoney(2000);
        userRepository.save(user);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> lobbyService.joinLobbyById(1234L, "validToken"));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

        // Cleanup
        userRepository.delete(user);
    }


    @Test
    public void testCreateGame_notEnoughPlayers(){

        //Setup
        User user1 = new User();

        user1.setUsername("testUser");
        user1.setToken("token");
        user1.setPassword("Password");
        user1.setStatus(UserStatus.ONLINE);
        user1.setMoney(2000);
        user1 = userRepository.save(user1);

        Lobby lobby = lobbyService.createLobby("token");




        //method call
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                ()->lobbyService.startGame("token", lobby.getId())
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());



        //cleanup
        user1.setLobby(null);
        userRepository.save(user1);
        lobby.setLobbyLeader(null);
        lobby.removeUserFromLobby(user1);
        lobbyRepository.delete(lobby);
        userRepository.delete(user1);
    }
    @Test
    public void testCreateGame_Success(){

        //Setup
        User user1 = new User();
        User user2 = new User();
        User user3 = new User();

        user1.setUsername("testUser");
        user1.setToken("token");
        user1.setPassword("Password");
        user1.setStatus(UserStatus.ONLINE);
        user1.setMoney(2000);
        userRepository.save(user1);

        user2.setUsername("testUser2");
        user2.setToken("token2");
        user2.setPassword("Password");
        user2.setStatus(UserStatus.ONLINE);
        user2.setMoney(2000);
        userRepository.save(user2);

        user3.setUsername("testUser3");
        user3.setToken("token3");
        user3.setPassword("Password");
        user3.setStatus(UserStatus.ONLINE);
        user3.setMoney(2000);
        userRepository.save(user3);

        Lobby lobby = lobbyService.createLobby("token");
        lobbyService.joinLobbyById(4, "token2");
        lobbyService.joinLobbyById(4, "token3");


        //method call
        Game createdGame = lobbyService.startGame("token", lobby.getId());

        assertNotNull(createdGame);
        //assertEquals(lobby.getId(), createdGame.getLobby().getId());


        //cleanup
        user1.setLobby(null);
        user2.setLobby(null);
        user3.setLobby(null);
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        lobby.setLobbyLeader(null);
        lobby.removeUserFromLobby(user1);
        lobby.removeUserFromLobby(user2);
        lobby.removeUserFromLobby(user3);


    }

}